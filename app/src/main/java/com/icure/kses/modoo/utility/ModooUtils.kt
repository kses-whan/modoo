package com.icure.kses.modoo.utility

class ModooUtils {
    companion object{
        fun removeSpecialChars(src:String):String{
            return src.replace("[^a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9+./_-]".toRegex(), "")
        }
    }
}