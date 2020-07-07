package com.icure.kses.modoo.vo

import java.io.Serializable

data class ModooListItem(
        var itemCode: String,
        var thumbUrl: String?,
        var repImageUrl: String?,
        var itemName: String,
        var itemPrice: Long,
        var itemCreateDate: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        (other as ModooListItem)?.let{
            return it.itemCode.contentEquals(itemCode)
        }
    }
}