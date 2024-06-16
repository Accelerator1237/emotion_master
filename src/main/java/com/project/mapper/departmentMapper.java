package com.project.mapper;

import com.project.pojo.department;
import com.project.vo.add_department;
import com.project.vo.info_department;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface departmentMapper {

    @Select("select deptNo from department where deptName=#{deptname}")
     String findByDeptName(String deptname);


    @Insert("insert into department(deptName, deptManagerId,higherDeptNo)" +
    "values (#{deptName}, #{deptManagerId},#{higherDeptNo})")
    void add(add_department department);

    @Select("select * from department where deptNo=#{deptNo}")
    department findByDepartment(Integer deptNo);

    @Delete("delete from department where deptNo=#{deptNo}")
    void departmentDelete(Integer deptNo);

    @Update("update department set  deptName=#{deptName}, higherDeptNo=#{higherDeptNo},deptManagerId=#{deptManagerId} where deptNo=#{deptNo}")
    void departmentChange(department department);

    @Insert("insert into employeejobinfo(employeeId, employeeDepartmentNo) VALUES (#{employeeId}, #{deptNo})")
    void addEmpJobInfo(String employeeId, String deptNo);

    @Delete("delete from employeejobinfo where employeeId = #{employeeId}")
    void deleteEmpJobInfo(String employeeId);

    @Select("select deptNo from department where deptName = #{deptName} AND deptManagerId = #{deptManagerId}")
    String findDeptNo(String deptName, String deptManagerId);

    List<info_department> department_list(String departmentName, String phoneNumber, String manager);

//    int count(String departmentName);
}
