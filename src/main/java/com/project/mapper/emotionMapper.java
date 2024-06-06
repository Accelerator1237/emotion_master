package com.project.mapper;

import com.project.pojo.report;
import com.project.vo.emotion_report;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


// 使用 @Mapper 标注这是一个 MyBatis 映射器接口
@Mapper
public interface emotionMapper {

    // 定义一个插入操作的 SQL 映射，用于向 report 表中插入新的记录
    @Insert("insert into report(warningType, emotionData, suggestion, recordTime, employeeId)" +
            "values (#{warningType} , #{emotionData} , #{suggestion} , #{recordTime},#{employeeId})")
    void addReport(report report);


    List<emotion_report> list(String employeeId, String recordTime, String warningType);

    @Select("select count(*) from report where date(recordTime) = date(#{formattedDate})")
    Integer countNum(String formattedDate);

    @Select("SELECT re.* FROM report_emp re JOIN (SELECT employeeId, MAX(recordTime) AS latestRecordTime FROM report_emp GROUP BY employeeId) latest ON re.employeeId = latest.employeeId AND re.recordTime = latest.latestRecordTime")
    List<report> findLatestReports();

    @Select("SELECT re.* FROM report_emp re JOIN (SELECT employeeId, MAX(recordTime) AS latestRecordTime FROM report_emp GROUP BY employeeId) latest ON re.employeeId = latest.employeeId AND re.recordTime = latest.latestRecordTime JOIN employee_dept ed ON re.employeeId = ed.employeeId WHERE ed.deptNo = #{deptNo}")
    List<report> findLatestReportsBydeptNo(int deptNo);

    @Select("SELECT deptName FROM department WHERE deptNo = #{deptNo}")
    String findDepartmentNameByDeptNo(int deptNo);
}
