package com.icure.kses.modoo.model;


import com.icure.kses.modoo.constant.ModooApiCodes;

public abstract class ModooHttpResult {
    public String resultCode = ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR;
    public String resultMsg = null;
}
