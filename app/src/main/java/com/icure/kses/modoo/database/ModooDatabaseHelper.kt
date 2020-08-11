package com.icure.kses.modoo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log
import com.icure.kses.modoo.customview.search.HistoryContract
import com.icure.kses.modoo.log.Log4jHelper
import com.icure.kses.modoo.vo.SuggestionItem
import java.util.concurrent.atomic.AtomicInteger

const val DATABASE_NAME = "modoosubsdb"
const val DATABASE_VERSION = 1

class ModooDatabaseHelper(var context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var mOpenCounter : AtomicInteger = AtomicInteger()

    override fun onCreate(db: SQLiteDatabase) {
        Log.i("tagg", "database oncreate")
        logger.info("KSES_DatabaseHelper - onCreate")
        createTables(DATABASE_VERSION, db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (newVersion) {
            2 -> upgradeDBToVersion2(db)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    private fun createTables(nDBVer: Int, db: SQLiteDatabase) {
        logger.info("KSES_DatabaseHelper - createTables")
        addHistoryTable(db)
    }

    private fun upgradeDBToVersion2(db: SQLiteDatabase) {}
    private fun executeSQLWithLog(db: SQLiteDatabase, strQuery: String) {
        logger.info("KSES_DatabaseHelper - executeSQLWithLog : Execute SQL=\r\n$strQuery")
        try {
            db.execSQL(strQuery)
        } catch (e: SQLException) {
            logger.info("""KSES_DatabaseHelper - executeSQLWithLog : Exception=${e.message}""".trimIndent())
        }
    }

    private fun addHistoryTable(database: SQLiteDatabase) {
        database.execSQL(
                "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " (" +
                        HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                        HistoryContract.HistoryEntry.COLUMN_QUERY + " TEXT NOT NULL," +
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " INTEGER DEFAULT 0," +
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " INTEGER NOT NULL DEFAULT 0," +
                        "UNIQUE (" + HistoryContract.HistoryEntry.COLUMN_QUERY + ") ON CONFLICT REPLACE);"
        )
        Log.i("tagg","addHistoryTable : ${database?.isOpen}")
    }

    fun selectSearchQuery(query:String): MutableList<SuggestionItem>?{
        validateDatabase()?.let {
            try {
                it.beginTransaction()
                var cursor = it.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?",
                        arrayOf("%${query}%"),
                        null,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " + HistoryContract.HistoryEntry.COLUMN_QUERY
                )
                return cursor?.toMutableList {
                    SuggestionItem(
                            it.getString(it?.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY) ?: 0)
                            ,it.getInt(it?.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY) ?: 0) != 0
                    )
                }
                it.setTransactionSuccessful()
            }catch(e:Exception){
                Log.e("tagg","ModooDatabase selectSearchQuery ERROR : ${e}")
            } finally {
                it.endTransaction()
                closeDatabase()
            }
        }
        return null
    }


    fun selectAllHistory(): MutableList<SuggestionItem>? {
        validateDatabase()?.let {
            try {
                it.beginTransaction()
                var cursor = it?.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                        arrayOf("1"),
                        null,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + MAX_HISTORY
                )

                return cursor?.toMutableList {
                    SuggestionItem(
                            it.getString(it?.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY) ?: 0)
                            ,it.getInt(it?.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY) ?: 0) != 0
                    )
                }
                it.setTransactionSuccessful()
            }catch(e:Exception){
                Log.e("tagg","ModooDatabase selectAllHistory ERROR : ${e}")
            } finally {
                it.endTransaction()
                closeDatabase()
            }
        }
        return null
    }


    fun removeSuggestion(suggestion: String) {
        if (!TextUtils.isEmpty(suggestion)) {
            validateDatabase()?.let {
                try {
                    it.beginTransaction()
                    var result = it.delete(HistoryContract.HistoryEntry.TABLE_NAME,
                            HistoryContract.HistoryEntry.TABLE_NAME +
                                    "." +
                                    HistoryContract.HistoryEntry.COLUMN_QUERY +
                                    " = ? AND " +
                                    HistoryContract.HistoryEntry.TABLE_NAME +
                                    "." +
                                    HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                                    " = ?"
                            , arrayOf(suggestion, 1.toString())
                    )
                    it.setTransactionSuccessful()
                }catch(e:Exception){
                    Log.e("tagg","ModooDatabase removeSuggestion ERROR : ${e}")
                } finally {
                    it.endTransaction()
                    closeDatabase()
                }
            }
        }
    }


    fun insertSuggestions(suggestions: List<String?>) {
        validateDatabase()?.let {
            try {
                var sb = StringBuilder()
                sb.append("INSERT INTO ").append(HistoryContract.HistoryEntry.TABLE_NAME).append("(")
                        .append("${HistoryContract.HistoryEntry.COLUMN_QUERY},")
                        .append("${HistoryContract.HistoryEntry.COLUMN_INSERT_DATE},")
                        .append("${HistoryContract.HistoryEntry.COLUMN_IS_HISTORY}")
                        .append(")")
                        .append(" ")
                for (str in suggestions) {
                    sb.append("UNION SELECT ").append("'${str}'").append(",")
                            .append("'${System.currentTimeMillis()}'").append(",")
                            .append("'0'")
                            .append(" ")
                }
                it.beginTransaction()
                it.execSQL(sb.toString().replaceFirst("UNION", ""))
                it.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.e("tagg", "ModooDatabase insertSuggestions ERROR : ${e}")
            } finally {
                it.endTransaction()
                closeDatabase()
            }
        }
    }


    fun insertSuggestion(query: String?, ms: Long) {
        validateDatabase()?.let {
            try {
                val values = ContentValues()
                if (!TextUtils.isEmpty(query) && ms > 0) {
                    values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, query)
                    values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, ms)
                    values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 1) // Saving as history.
                }
                it.beginTransaction()
                it.insert(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        null,
                        values
                )
                it.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.e("tagg", "ModooDatabase insertSuggestion ERROR : ${e}")
            } finally {
                it.endTransaction()
                closeDatabase()
            }
        }
    }

    private fun validateDatabase(): SQLiteDatabase?{
        Log.i("tagg","validateDatabase start")

        if(mOpenCounter.incrementAndGet() == 1){
            Log.i("tagg","validateDatabase incrementAndGet is 1")

            try {
                db = writableDatabase
            } catch (e : Exception){
                Log.i("tagg","validateDatabase ERROR : ${e}")
            }
        }
        Log.i("tagg","validateDatabase db : ${db?.isOpen}")
        return db
    }

    private fun closeDatabase(){
        if(mOpenCounter.decrementAndGet() == 0){
            try {
                db?.close()
            } catch (e : Exception){
                Log.i("tagg","closeDatabase ERROR : ${e}")
            }
        }
    }

    init {
        var bIsFirstRun = true

        logger.info("KSES_DatabaseHelper : Database Version=$DATABASE_VERSION")
        if (bIsFirstRun == true) {
            bIsFirstRun = false
            logger.info("KSES_DatabaseHelper : Dump Creating Queryes")
        }
    }

    fun <T> Cursor.toMutableList(block: (Cursor) -> T) : MutableList<T> {
        return mutableListOf<T>().also { list ->
            if (moveToFirst()) {
                do {
                    list.add(block.invoke(this))
                } while (moveToNext())
            }
            close()
        }
    }

    companion object{
        val MAX_HISTORY = 10
        var db:SQLiteDatabase? = null
        var logger = Log4jHelper.getInstance()

        private var instance: ModooDatabaseHelper? = null
        fun getInstance(context: Context): ModooDatabaseHelper?{
            if(instance == null){
                instance = ModooDatabaseHelper(context)
            }
            return instance
        }
    }
}