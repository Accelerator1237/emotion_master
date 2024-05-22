package com.project.pojo;

import lombok.Data;

@Data
public class report {
    private String reportId;
    private String employeeId;
    private String recordTime;
    private String emotionData;
    private Integer warningType;
    private String suggestion;
}
