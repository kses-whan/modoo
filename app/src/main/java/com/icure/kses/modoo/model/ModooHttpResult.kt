package com.icure.kses.modoo.model

import com.icure.kses.modoo.constant.ModooApiCodes

abstract class ModooHttpResult {
    var resultCode = ModooApiCodes.API_RETURNCODE_UNKNOWN_ERROR
    var resultMsg: String? = null
}