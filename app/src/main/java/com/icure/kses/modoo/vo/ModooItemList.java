package com.icure.kses.modoo.vo;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ModooItemList implements Serializable {

    public String thumbUrl = null;
    public String repImageUrl = null;

    public String itemName = null;

    public long itemPrice = 0;

    public String itemCode = null;
    public String itemCreateDate = null;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ModooItemList){
            return (((ModooItemList) obj).itemCode.contentEquals(itemCode));
        } else {
            return false;
        }
    }
}
