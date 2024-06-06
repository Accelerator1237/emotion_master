package com.project.service;

import com.project.pojo.data_center;
import com.project.pojo.deptEmotion;
import com.project.pojo.pageBean;
import com.project.pojo.report;
import com.project.vo.emotion_report;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface reportService {

    String detectEmotion(MultipartFile image) throws IOException;

    void addReport(report report);

    pageBean<emotion_report> list(Integer pageNum, Integer pageSize, String employeeId, String recordTime, String warningType);

    Integer count_num(String formattedDate);

    data_center calculateEmotionStatistics();

    deptEmotion getDeptEmotion(int deptNo);
}
