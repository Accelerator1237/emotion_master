package com.project.pojo;

import lombok.Data;

@Data
public class data_center {
    //每一种情绪的人有多少...
    private int anger;
    private int contempt;
    private int disgust;
    private int fear;
    private int happy;
    private int neutral;
    private int sad;
    private int surprise;
    //当日检查人数
    Integer inspectedNum;

}
