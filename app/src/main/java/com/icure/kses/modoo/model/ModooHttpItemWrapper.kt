package com.icure.kses.modoo.model

import com.icure.kses.modoo.vo.ModooItemDetail
import com.icure.kses.modoo.vo.ModooListItem

class ModooHttpItemWrapper : ModooHttpResult() {
    var itemList: MutableList<ModooListItem>? = null
    var itemDetail: ModooItemDetail? = null
}