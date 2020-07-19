package com.icure.kses.modoo.vo

import java.io.Serializable

data class ModooListItem(
        val itemCode: String,
        val thumbUrl: String?,
        val repImageUrl: String?,
        val itemName: String,
        val itemPrice: Long,
        val itemCreateDate: String
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if(other is ModooListItem){
            return other.itemCode == itemCode
        }
        return false
    }

    override fun hashCode(): Int {
        var hash = itemCode.hashCode() * 31
        hash = hash * 31 + itemCode.hashCode()
        return hash
    }
}