package com.icure.kses.modoo.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;


import com.icure.kses.modoo.constant.Modoo_Api_Codes;
import com.icure.kses.modoo.log.Log4jHelper;

import org.json.JSONObject;

import java.io.File;


public class Modoo_HttpPriorityAsync extends PriorityAsyncTask<String, Integer, String[]> implements Modoo_HttpParams {

    private Modoo_HttpClient ksesHttpClient;
    private Context context;

    private ProgressDialog progress = null;
    private boolean useProgress = false;

    private Modoo_HttpAsyncResponseListener ksesHttpAsyncResponseListener;

    protected static Log4jHelper logger = Log4jHelper.getInstance();

    public Modoo_HttpPriorityAsync(Context context, String url, int encodeType, boolean useProgress, Modoo_HttpAsyncResponseListener listener) {
        super();

        if(!TextUtils.isEmpty(url)) {
            ksesHttpClient = new Modoo_HttpClient();
            ksesHttpClient.init(context, url, encodeType);
        } else {
            ksesHttpClient = null;
        }
        this.context = context;
        this.useProgress = useProgress;
        boolean bIsWriteDebugLog = false;

        if (bIsWriteDebugLog == true)
            logger.info("KSES_HttpPriorityAsync - url=" + url + ", encodeType=" + encodeType + ", useProgress=" + useProgress);

        this.ksesHttpAsyncResponseListener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (useProgress) {
            goProgressStart();
        }
    }

    @Override
    protected String[] doInBackground(String... params) {

        String[] resultArr = null;

        if (params != null && params.length > 0) {
            resultArr = new String[params.length + 1];
            try {
                for (int i = 0; i < params.length; i++) {
                    resultArr[i + 1] = params[i];
                }
            }catch (Exception e){
                logger.error("KSES_HttpPriorityAsync - doInBackground ERROR 1 : ", e);
            }
        } else {
            resultArr = new String[1];
        }

        if(ksesHttpClient != null){
            try {
                if (ksesHttpClient.encodeType == Modoo_HttpClient.GET)
                    resultArr[0] = ksesHttpClient.connectToServerGet();
                else resultArr[0] = ksesHttpClient.connectToServer();
            } catch (Exception e) {
                logger.error("KSES_HttpPriorityAsync - doInBackground ERROR 2 : ", e);
                return null;
            }
        }
        return resultArr;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String[] resultArr) {
        try {
            if (resultArr != null && resultArr.length >= 1) {
                logger.info("TSHttpPriorityAsync onPostExecute resultArr : " + resultArr[0]);
            }
            if (useProgress) {
                goProgressEnd();
            }
        } catch (Exception e) {
            logger.error("TSHttpPriorityAsync - onPostExecute ERROR 1 : " , e);
        }

        if (resultArr == null || resultArr.length <= 0 || resultArr[0] == null) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("returnvalue", "" + Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR);
                jo.put("message", "http error");
                resultArr = new String[]{jo.toString()};
            } catch (Exception e) {
                logger.error("TSHttpPriorityAsync - onPostExecute ERROR 2 : " , e);
                return;
            }
        }

        if (ksesHttpAsyncResponseListener != null) {
            ksesHttpAsyncResponseListener.onPostExcute(resultArr);
        }
    }

    @Override
    public void pushDataKey(String key, String value) {

        if (value != null && ksesHttpClient != null)
            ksesHttpClient.pushDataKey(key, value);
    }

    @Override
    public void pushFileKey(String key, File value) {

        if (value != null && ksesHttpClient != null)
            ksesHttpClient.pushFileKey(key, value);
    }

    @Override
    public void pushImageKey(String key, Bitmap value) {

        if (value != null && ksesHttpClient != null)
            ksesHttpClient.pushImageKey(key, value);
    }

    @Override
    public void addJsonBody(String jsonBody) {
        if (jsonBody != null && ksesHttpClient != null) {
            ksesHttpClient.addJsonBody(jsonBody);
        }
    }

    @Override
    public void addAuthorizationHeader(String authHeader) {
        if (authHeader != null && ksesHttpClient != null) {
            ksesHttpClient.addAuthorizationHeader(authHeader);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        Toast.makeText(context, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean isWifiConnected() {

    	boolean result = false;

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}

        return result;
    }

    public boolean isLTEConnected() {
		boolean result = false;

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}

		return result;
    }

    private void goProgressStart() {
        try {
            progress = new ProgressDialog(context);
            progress.setMessage("Processing...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        } catch (Exception e) {
        	logger.error("KSES_HttpPriorityAsync goProgressStart - ERROR : ", e);
        }
    }

    private void goProgressEnd() {
        if (progress != null) {
            try {
                progress.dismiss();
            } catch (Exception e) {
				logger.error("KSES_HttpPriorityAsync goProgressEnd - ERROR : ", e);
            }
            progress = null;
        }
    }

    public void setConnectTimeout(int timeoutMills) {
        if (null != ksesHttpClient) {
            ksesHttpClient.setHttpConnectionTimeout(timeoutMills);
        }
    }
}