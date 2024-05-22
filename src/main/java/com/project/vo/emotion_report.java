package com.project.vo;

import lombok.Data;

@Data

public class emotion_report {
    private Integer reportId;
    private String warningType;
    private String suggestion;
    private String recordTime;
    private String emotionData;
    private String employeeName;
    private String employeePhoneNumber;
}
