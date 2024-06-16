package com.project.mapper;

import com.project.pojo.employeeBasic;
import com.project.vo.*;
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
    void employeePasswordReset(String employeeId);
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

    @Select("select employeePassword from employeebasic where employeeId=#{claim}")
    String getpassword(String claim);

    @Update("update employeebasic set employeePassword=#{newPassword} where employeeId=#{claim}")
    void change_password(String newPassword, String claim);

    @Update("update employeebasic set employeeName=#{employeeName} where employeeId=#{claim}")
    void app_update_empbasic_name(String employeeName, String claim);

    @Update("update employeebasic set employeePhoneNumber=#{employeePhoneNumber} where employeeId=#{claim}")
    void app_update_empbasic_phonenum(String employeePhoneNumber, String claim);

    @Update("update employeebasic set employeeBirthday=#{employeeBirthday} where employeeId=#{claim}")
    void app_update_empbasic_birthday(String employeeBirthday, String claim);

    @Update("update employeejobinfo set employeeDepartmentNo=#{deptno} where employeeId=#{claim}")
    void app_update_jobinfo_deptno(String deptno, String claim);

    @Update("update employeejobinfo set employeeJob=#{employeeJob} where employeeId=#{claim}")
    void app_update_jobinfo_empjob(String employeeJob, String claim);

    @Update("UPDATE employeebasic SET employeeAvatar = #{url}, avatarEncoding = #{encoding} WHERE employeeId = #{employeeId}")
    void add_AvatarAndEncoding(String url, String encoding, String employeeId);

    @Select("SELECT avatarEncoding FROM employeebasic WHERE employeeId = #{employeeId}")
    String getAvatarEncoding(String employeeId);
}
