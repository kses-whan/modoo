package com.icure.kses.modoo.model;

import com.icure.kses.modoo.vo.ModooItemDetail;
import com.icure.kses.modoo.vo.ModooItemList;

import java.util.List;

public class ModooItemWrapper extends ModooHttpResult{
    public List<ModooItemList> itemList = null;
    public ModooItemDetail itemDetail = null;
}
