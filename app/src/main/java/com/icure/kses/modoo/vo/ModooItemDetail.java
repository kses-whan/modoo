package com.icure.kses.modoo.vo;

import androidx.annotation.Nullable;

import java.util.List;

public class ModooItemDetail {

    public String thumbUrl = null;
    public String repImageUrl = null;
    public List<String> detailImageUrls = null;

    public String itemName = null;
    public String itemDesc = null;

    public long itemPrice = 0;
    public int itemCurrency = 0;

    public int paySystem = 0;  // 결제방식  0: 단건결제  1: 정기결제
    public int payMethod = 0;  // 결제수단  0: 신용카드  1: 계좌이체 ...

    public String itemCode = null;
    public String itemCreateDate = null;

    public String partnerCompanyCode = null;
    public String partnerCompanyName = null;
    public String partnerCompanyIcon = null;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ModooItemList){
            return (((ModooItemList) obj).itemCode.contentEquals(itemCode));
        } else {
            return false;
        }
    }

}
