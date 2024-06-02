package com.project.service;

import com.project.pojo.employeeBasic;
import com.project.pojo.pageBean;
import com.project.vo.*;

import java.util.List;

public interface employeeBasicService {
    void register(String employeeName, String employeePassword, String employeePhoneNumber, String employeeAvatar, String employeeBirthday, int employeeGender);
    employeeBasic findByEmployeeId(String employeeId);

    employee_Basic findByToken(String employeeId);

    void employeePasswordReset(String employeeId);

    //void employeeBasicUpdate(employeeBasic employeeBasic,String claim);

    void employeeDelete(String employeeId);

    employeeBasic findByEmployeePhoneNumber(String employeePhoneNumber);

    pageBean<employee> list(Integer pageNum, Integer pageSize, String role, String name, String phoneNumber, Integer gender, Integer departmentNo, String beginBirthday,String endBirthday);

    void employeeAdd(add_employee employee);

    void Update(update_employee updateEmployee);

    void import_emp(List<add_employee> emplist);

    void add_Avatar(String url,String claim);

    String getpassword(String claim);

    void password_change(String newPassword, String claim);

    void app_update(app_update_employee appUpdateEmployee, String claim);
}
