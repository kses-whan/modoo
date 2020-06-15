package com.icure.kses.modoo.model;

import com.icure.kses.modoo.constant.Modoo_Api_Codes;

public abstract class ModooHttpResult {
    public String resultCode = Modoo_Api_Codes.API_RETURNCODE_UNKNOWN_ERROR;
    public String resultMsg = null;
}
