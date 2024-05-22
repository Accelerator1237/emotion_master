package com.project.mapper;

import com.project.pojo.report;
import com.project.vo.emotion_report;
import com.project.vo.emotion_response;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


// 使用 @Mapper 标注这是一个 MyBatis 映射器接口
@Mapper
public interface emotionMapper {

    // 定义一个插入操作的 SQL 映射，用于向 report 表中插入新的记录
    @Insert("insert into report(warningType, emotionData, suggestion, recordTime, employeeId)" +
            "values (#{warningType} , #{emotionData} , #{suggestion} , #{recordTime},#{employeeId})")
    void addReport(report report);

    List<emotion_report> list(String employeeId, String recordTime, String warningType);
}
