package com.mt.bbdj.baseconfig.model;

import com.contrarywind.interfaces.IPickerViewData;

/**
 * @Author : ZSK
 * @Date : 2019/8/29
 * @Description :
 */
public class TimeBean implements IPickerViewData {

    private String hour;

    @Override
    public String getPickerViewText() {
        return this.hour;
    }
}
