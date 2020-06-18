package com.icure.kses.modoo.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.icure.kses.modoo.constant.Modoo_Api_Codes;
import com.icure.kses.modoo.constant.Modoo_Constants;
import com.icure.kses.modoo.http.Modoo_HttpAsyncResponseListener;
import com.icure.kses.modoo.http.Modoo_HttpPriorityAsync;
import com.icure.kses.modoo.utility.ModooDataUtils;
import com.icure.kses.modoo.vo.ModooItemDetail;

public class ModooViewModel extends AndroidViewModel {

    private MutableLiveData<ModooItemWrapper> itemListData, itemDetailData;

    public ModooViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ModooItemWrapper> getItemListData(int category){
        if(itemListData == null){
            itemListData = new MutableLiveData<>();
        }
        loadItemListData(category);
        return itemListData;
    }

    public LiveData<ModooItemWrapper> getItemDetailData(String itemCode){
//        if(itemDetailData == null){
//            itemDetailData = new MutableLiveData<>();
//            loadItemDetailData(itemCode);
//        }
//        return itemDetailData;

        if(itemDetailData == null) {
            itemDetailData = new MutableLiveData<>();
        }
        loadItemDetailData(itemCode);
        return itemDetailData;
    }

    public void loadItemDetailData(final String itemCode){
        Log.i("tagg","loadItemDetailData : " + itemCode);

        Modoo_HttpPriorityAsync httpPriorityAsync = new Modoo_HttpPriorityAsync(
                getApplication().getApplicationContext()
                , null  //local test
                , Modoo_Constants.HTTP_POST
                , false
                , new Modoo_HttpAsyncResponseListener() {
            @Override
            public void onPostExcute(String[] resultArr) {
                try {
                    String resultStr = resultArr[0];

                    // local test
                    resultStr = ModooDataUtils.getDetailDataTest(itemCode);
                    //

                    ModooItemWrapper item = new Gson().fromJson(resultStr, ModooItemWrapper.class);
                    if(item == null){
                        setError(Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR);
                        return;
                    }

                    if (item.resultCode.equalsIgnoreCase(Modoo_Api_Codes.API_RETURNCODE_SUCCESS)) {
                        Log.i("tagg","API_RETURNCODE_SUCCESS : " + item.itemDetail.itemCode);
                        itemDetailData.setValue(item);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    setError(Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR);
                }
            }
        });

        httpPriorityAsync.executeOnExecutor(Modoo_Constants.EXECUTOR, itemCode);
    }


    public void loadItemListData(final int category){

        Modoo_HttpPriorityAsync httpPriorityAsync = new Modoo_HttpPriorityAsync(
                getApplication().getApplicationContext()
                , null  //local test
                , Modoo_Constants.HTTP_POST
                , false
                , new Modoo_HttpAsyncResponseListener() {
            @Override
            public void onPostExcute(String[] resultArr) {
                try {
                    String resultStr = resultArr[0];

                    Log.i("tagg","category : " + category);
                    // local test
                    resultStr = ModooDataUtils.getListDataTest(category);
                    //
                    Log.i("tagg","resultStr : " + resultStr);
                    ModooItemWrapper item = new Gson().fromJson(resultStr, ModooItemWrapper.class);
                    if(item == null){
                        Log.i("tagg","ERROR 111");
                        setError(Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR);
                        return;
                    }

                    if (item.resultCode.equalsIgnoreCase(Modoo_Api_Codes.API_RETURNCODE_SUCCESS)) {
                        itemListData.setValue(item);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Log.i("tagg","ERROR 222");
                    setError(Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR);
                }
            }
        });

        httpPriorityAsync.executeOnExecutor(Modoo_Constants.EXECUTOR, "" + category);
    }

    private void setError(String errorCode){
        Log.i("tagg","errorCode : " + errorCode);

        ModooItemWrapper errorResult = new ModooItemWrapper();
        errorResult.resultCode = errorCode;
        errorResult.resultMsg = getApplication().getResources().getString(
                getApplication().getResources().getIdentifier("http_result_msg_error_" + errorCode, "string", getApplication().getPackageName()));
        errorResult.itemList = null;
        errorResult.itemDetail = null;
        if(itemListData != null)
            itemListData.setValue(errorResult);
        if(itemDetailData != null)
            itemDetailData.setValue(errorResult);
    }
}
