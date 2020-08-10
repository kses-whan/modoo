package com.icure.kses.modoo.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.log.Log4jHelper
import com.icure.kses.modoo.retrofit.IModooApi
import com.icure.kses.modoo.retrofit.IModooApi.Companion.instance
import com.icure.kses.modoo.utility.ModooDataUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModooViewModel(application: Application) : AndroidViewModel(application) {
    private var itemData: MutableLiveData<ModooHttpItemWrapper>? = null
    private var userData: MutableLiveData<ModooHttpUserInfoResult>? = null

    fun getLoginData(parameter: Map<String, String>): LiveData<ModooHttpUserInfoResult>? {
        loadLoginData(parameter)
        return userData
    }

    fun getItemListData(category: Int): LiveData<ModooHttpItemWrapper>? {
        loadItemListData(category)
        return itemData
    }

    fun getItemDetailData(itemCode: String?): LiveData<ModooHttpItemWrapper>? {
        loadItemDetailData(itemCode)
        return itemData
    }

    fun loadLoginData(parameter: Map<String, String>){
        try {
            userData = MutableLiveData()
            instance.testRequest(parameter)?.let {
                it.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
    //                            Log.i("tagg", "response : " + response.body()!!.string())
                            //                        if(response == null || !response.isSuccessful()){
                            //                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR);
                            //                            return;
                            //                        }

                            //                        ResponseBody body = response.body();
                            //                        String resultStr = body.string();

                            // local test
//                            val resultStr = ModooDataUtils.getListDataTest(parameter)
                            val resultStr = ModooDataUtils.getLoginDataTest(parameter)
                            //
                            val item = Gson().fromJson(resultStr, ModooHttpUserInfoResult::class.java)
                            if (item == null) {
                                setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                                return
                            }
                            if (item.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                                userData?.let { it.setValue(item) }
                            }
                        } catch (e: Exception) {
                            logger.error("loadItemListData enqueue ERROR : ", e)
                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                    }
                })
            }
        } catch (e: Exception) {
            logger.error("loadItemListData ERROR : ", e)
        }
    }

    fun loadItemListData(category: Int) {

        val pushData = mapOf(
                "param1" to "value1",
                "param2" to "value2"
        )

        try {
            itemData = MutableLiveData()
            val testPost = instance.testRequest(pushData)
            testPost?.let { it.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
//                            Log.i("tagg", "response : " + response.body()!!.string())
                            //                        if(response == null || !response.isSuccessful()){
                            //                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR);
                            //                            return;
                            //                        }

                            //                        ResponseBody body = response.body();
                            //                        String resultStr = body.string();

                            // local test
                            val resultStr = ModooDataUtils.getListDataTest(category)
                            //
                            val item = Gson().fromJson(resultStr, ModooHttpItemWrapper::class.java)
                            if (item == null) {
                                setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                                return
                            }
                            if (item.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                                itemData?.let {
                                    Log.i("tagg","setValue!!!!!!!!!")
                                    it.setValue(item)
                                }
                            }
                        } catch (e: Exception) {
                            logger.error("loadItemListData enqueue ERROR : ", e)
                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                    }
                })
            }
        } catch (e: Exception) {
            logger.error("loadItemListData ERROR : ", e)
        }
    }


    fun loadItemDetailData(itemCode: String?) {

        val pushData = mapOf(
            "param1" to "value1",
            "param2" to "value2"
        )

        try {
            itemData = MutableLiveData()
            val testPost = instance.testRequest(pushData)
            testPost.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
//                        Log.i("tagg", "loadItemDetailData response : " + response.body()!!.string())
                        //                        if(response == null || !response.isSuccessful()){
//                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR);
//                            return;
//                        }

//                        ResponseBody body = response.body();
//                        String resultStr = body.string();

                        // local test
                        val resultStr = ModooDataUtils.getDetailDataTest(itemCode)
                        //
                        val item = Gson().fromJson(resultStr, ModooHttpItemWrapper::class.java)
                        if (item == null) {
                            setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                            return
                        }
                        if (item.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                            itemData?.let { it.setValue(item) }
                        }
                    } catch (e: Exception) {
                        logger.error("loadItemDetailData enqueue ERROR : ", e)
                        setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    setError(ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR)
                }
            })
        } catch (e: Exception) {
            logger.error("loadItemDetailData ERROR : ", e)
        }
    }

    private fun setError(errorCode: String) {
        try {
            val errorResult = ModooHttpItemWrapper()
            errorResult.resultCode = errorCode
            errorResult.resultMsg = getApplication<Application>().resources.getString(
                    getApplication<Application>().resources.getIdentifier("http_result_msg_error_$errorCode", "string", getApplication<Application>().packageName))
            errorResult.itemList = null
            errorResult.itemDetail = null
            itemData?.let { it.value = errorResult }
        }catch(e:Exception){
            logger.error("setError ERROR : ", e)
        }
    }

    companion object {
        private val logger = Log4jHelper.getInstance()
    }
}