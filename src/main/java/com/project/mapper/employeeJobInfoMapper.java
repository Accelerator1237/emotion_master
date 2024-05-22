package com.project.mapper;

import com.project.pojo.employeeJobInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface employeeJobInfoMapper {
    @Insert("insert into employeejobinfo(employeeRole, employeeId, employeeDepartmentNo, employeeJob)" +
    "value (#{employeeRole}, #{employeeId}, #{employeeDepartmentNo}, #{employeeJob})")
    void empJobAdd(employeeJobInfo employeeJobInfo);

    @Select("select * from employeejobinfo where employeeId=#{employeeId}")
    employeeJobInfo employeeJobFindById(String employeeId);
}
