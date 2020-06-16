package com.icure.kses.modoo.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.icure.kses.modoo.activity.ModooSplashActivity;
import com.icure.kses.modoo.activity.ModooWelcomeActivity;
import com.icure.kses.modoo.log.Log4jHelper;

import java.util.ArrayList;
import java.util.List;

public class ModooPermissionHelper {
    private static Log4jHelper logger = Log4jHelper.getInstance();
    public final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 3000;

    private static AppCompatActivity activity = null;

    public static boolean checkPermissions(Activity activity){

        int permissionReadExternalStorate 	= ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage 	= ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if(permissionReadExternalStorate != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }

    public static boolean onRequestPermissionsResult(final Activity activity , int requestCode,
                                                     String permissions[], int[] grantResults) {
        logger.info( "onRequestPermissionsResult - Permission callback called-------");

        boolean isAllGranted = false;

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                int grantResultsLength = grantResults.length;
                for(int i = 0 ; i < grantResultsLength ; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        isAllGranted = false;
                        break;
                    } else {
                        isAllGranted = true;
                    }
                }
                break;
        }

        if(!isAllGranted){
            showDialogOK(activity, "앱을 구동하는데 필요한 권한이 모두 허용되지 않았습니다.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            checkPermissions(activity);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            activity.finish();
                            break;
                    }
                }
            });
        } else {
            Intent i = new Intent(activity, ModooWelcomeActivity.class);
            activity.startActivity(i);
            activity.finish();
        }
        return false;
    }

    private static void showDialogOK(Activity activity , String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
