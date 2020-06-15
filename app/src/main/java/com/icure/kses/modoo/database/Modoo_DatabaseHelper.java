package com.icure.kses.modoo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.icure.kses.modoo.log.Log4jHelper;

public class Modoo_DatabaseHelper extends SQLiteOpenHelper {

    static Log4jHelper logger = Log4jHelper.getInstance();

    private static final String DATABASE_NAME = "ksesdb";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_T_KSESINFO =
            " CREATE TABLE IF NOT EXISTS T_KSESINFO (  \r\n"
                    + "\t 		UID INTEGER PRIMARY KEY AUTOINCREMENT  \r\n"
                    + "\t 		, STATUS INT NOT NULL DEFAULT 0	 \r\n"
                    + "\t 		);\r\n";

    Context context;

    static boolean bIsFirstRun = true;

    public Modoo_DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        logger.info("KSES_DatabaseHelper : Database Version=" + DATABASE_VERSION);

        if (bIsFirstRun == true) {
            bIsFirstRun = false;
            logger.info("KSES_DatabaseHelper : Dump Creating Queryes");
            logger.info("KSES_DatabaseHelper : DATABASE_CREATE_T_CALLDATA=\r\n" + DATABASE_CREATE_T_KSESINFO);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tagg","database oncreate");
        logger.info("KSES_DatabaseHelper - onCreate");
        createTables(DATABASE_VERSION, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
                upgradeDBToVersion2(db);
                break;
        }
    }

    private void createTables(int nDBVer, SQLiteDatabase db) {
        logger.info("KSES_DatabaseHelper - createTables");
    }

    public void upgradeDBToVersion(SQLiteDatabase db, int oldVersion) {
        logger.info("KSES_DatabaseHelper - upgradeDBToVersion : oldVersion=[" + oldVersion + "]");
    }

    private void upgradeDBToVersion2(SQLiteDatabase db){

    }

    private void executeSQLWithLog(SQLiteDatabase db, String strQuery) {
        logger.info("KSES_DatabaseHelper - executeSQLWithLog : Execute SQL=\r\n" + strQuery);
        try {
            db.execSQL(strQuery);
        } catch (SQLException e) {
            logger.info("KSES_DatabaseHelper - executeSQLWithLog : Exception=\r\n" + e.getMessage());
        }
    }
}
