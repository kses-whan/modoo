package com.icure.kses.modoo.vo

data class ModooItemDetail(var itemCode: String) {
    var thumbUrl: String? = null
    var repImageUrl: String? = null
    var detailImageUrls: ArrayList<String>? = null
    var itemName: String = ""
    var itemDesc: String = ""
    var itemPrice: Long = 0
    var itemCurrency = 0
    var paySystem = 0 // 결제방식  0: 단건결제  1: 정기결제
    var payMethod = 0 // 결제수단  0: 신용카드  1: 계좌이체 ...
    var itemCreateDate: String = ""
    var partnerCompanyCode: String? = null
    var partnerCompanyName: String? = null
    var partnerCompanyIcon: String? = null
}