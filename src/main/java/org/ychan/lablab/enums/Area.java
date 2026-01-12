package org.ychan.lablab.enums;

import lombok.Data;

public enum Area {

    PLATFORM_DECISION(0, "跨境平台智能决策与管理"),
    CULTURE_COMMENCE_ACTION(1, "跨文化与商业互动行为分析"),
    NLP_DATA_INTELLIGENCE(2, "自然语言处理与跨境数据智能"),
    CULTURAL_BUSINESS_OPINION(3, "跨文化信息与商业舆情分析");

    private final int code;

    private final String areaName;

    Area(int code, String areaName){
        this.code = code;
        this.areaName = areaName;
    }

    public int getCode(){
        return code;
    }

    public String getAreaName(){
        return areaName;
    }
}
