package com.icure.kses.modoo.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.icure.kses.modoo.log.Log4jHelper;

public class ModooConfig implements Parcelable {

    private static Log4jHelper logger = Log4jHelper.getInstance();


    protected ModooConfig(Parcel in) {
    }

    public static final Creator<ModooConfig> CREATOR = new Creator<ModooConfig>() {
        @Override
        public ModooConfig createFromParcel(Parcel in) {
            return new ModooConfig(in);
        }

        @Override
        public ModooConfig[] newArray(int size) {
            return new ModooConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
