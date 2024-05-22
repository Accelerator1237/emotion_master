package com.project.mapper;

import com.project.pojo.employeeBasic;
import com.project.vo.add_employee;
import com.project.vo.employee;
import com.project.vo.employee_Basic;
import com.project.vo.update_employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface employeeBasicMapper {
    @Insert("insert into employeebasic(employeeName, employeePassword, employeePhoneNumber, employeeAvatar, employeeBirthday, employeeGender)"+
            "values (#{employeeName},#{employeePassword},#{employeePhoneNumber},#{employeeAvatar},#{employeeBirthday},#{employeeGender})")
    void register(@Param("employeeName") String employeeName,@Param("employeePassword") String employeePassword,@Param("employeePhoneNumber") String employeePhoneNumber,@Param("employeeAvatar") String employeeAvatar,@Param("employeeBirthday") String employeeBirthday,@Param("employeeGender") int employeeGender);

    @Select("select * from employeebasic where employeeId=#{employeeId}")
    employeeBasic findByEmployeeId(String employeeId);

    employee_Basic findByToken(String employeeId);

    @Update("update employeebasic set employeePassword=123456 where employeeId=#{employeeId}")
    void employeePasswordChange(String employeeId);
/*

    @Update("update employeebasic set employeeName=#{employeeBasic.employeeName},employeePhoneNumber=#{employeeBasic.employeePhoneNumber},employeeAvatar=#{employeeBasic.employeeAvatar},employeeBirthday=#{employeeBasic.employeeBirthday} where employeeId=#{claim}")
    void employeeBasicUpdate(employeeBasic employeeBasic,String claim);
*/

    @Delete("delete from employeebasic where employeeId=#{employeeId}")
    void employeeDelete(String employeeId);

    @Select("select * from employeebasic where employeePhoneNumber=#{employeePhoneNumber}")
    employeeBasic findByEmployeePhoneNumber(String employeePhoneNumber);


    List<employee> list(String role, String name, String phoneNumber, Integer gender, Integer departmentNo, String beginBirthday,String endBirthday);

    void employeeAdd(add_employee employee,String password);

    void employeeJobAdd(add_employee employee, String id);

    void employeeUpdate(update_employee updateEmployee, String password);

    void employeeJobUpdate(update_employee updateEmployee, String id);

    void import_emp(@Param("list") List<add_employee> emplist);


    @Update("update employeebasic set employeeAvatar=#{url} where employeeId=#{claim}")
    void add_Avatar(String url,String claim);
}
