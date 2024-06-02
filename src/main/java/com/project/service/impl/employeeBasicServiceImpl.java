package com.project.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.pojo.employeeBasic;
import com.project.pojo.pageBean;
import com.project.service.employeeBasicService;
import com.project.mapper.employeeBasicMapper;
import com.project.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.mapper.departmentMapper;

import java.util.List;

@Service
public class employeeBasicServiceImpl implements employeeBasicService {
    @Autowired(required = false)
    private employeeBasicMapper employeeBasicMapper;

    @Autowired
    private departmentMapper departmentMapper;
    @Override
    public void register(String employeeName, String employeePassword, String employeePhoneNumber, String employeeAvatar, String employeeBirthday, int employeeGender) {
        employeeBasicMapper.register(employeeName,employeePassword,employeePhoneNumber,employeeAvatar,employeeBirthday,employeeGender);
    }

    @Override
    public employeeBasic findByEmployeeId(String employeeId) {
        employeeBasic employeeBasic = employeeBasicMapper.findByEmployeeId(employeeId);
        return employeeBasic;
    }

    @Override
    public employee_Basic findByToken(String employeeId) {
        employee_Basic employee_Basic=employeeBasicMapper.findByToken(employeeId);
        return employee_Basic;
    }

    @Override
    public void employeePasswordReset(String employeeId) {
        employeeBasicMapper.employeePasswordReset(employeeId);
    }

/*
    @Override
    public void employeeBasicUpdate(employeeBasic employeeBasic,String claim) {
        employeeBasicMapper.employeeBasicUpdate(employeeBasic,claim);
    }
*/

    @Override
    public void employeeDelete(String employeeId) {
        employeeBasicMapper.employeeDelete(employeeId);
    }

    @Override
    public employeeBasic findByEmployeePhoneNumber(String employeePhoneNumber) {
        return employeeBasicMapper.findByEmployeePhoneNumber(employeePhoneNumber);
    }

    @Override
    public pageBean<employee> list(Integer pageNum, Integer pageSize, String role, String name, String phoneNumber, Integer gender, Integer departmentNo, String beginBirthday,String endBirthday) {
        pageBean<employee> employeeBasicpageBean=new pageBean<>();

        PageHelper.startPage( pageNum,pageSize);

        List<employee> employeeBasics= employeeBasicMapper.list(role,name,phoneNumber, gender, departmentNo,beginBirthday,endBirthday);

        Page<employee> p=(Page<employee>) employeeBasics;

        employeeBasicpageBean.setTotal(p.getTotal());
        employeeBasicpageBean.setItems(p.getResult());

        return employeeBasicpageBean;
    }

    @Override
    public void employeeAdd(add_employee employee) {
        String password= String.valueOf(123456);
        employeeBasicMapper.employeeAdd(employee,password);
        employeeBasic employeeBasic= employeeBasicMapper.findByEmployeePhoneNumber(employee.getEmployeePhoneNumber());
        String id=employeeBasic.getEmployeeId();
        employeeBasicMapper.employeeJobAdd(employee, id);
    }

    @Override
    public void Update(update_employee updateEmployee) {
        String password= String.valueOf(123456);
        employeeBasicMapper.employeeUpdate(updateEmployee,password);
        employeeBasic employeeBasic= employeeBasicMapper.findByEmployeePhoneNumber(updateEmployee.getEmployeePhoneNumber());
        String id=employeeBasic.getEmployeeId();
        employeeBasicMapper.employeeJobUpdate(updateEmployee, id);
    }

    @Override
    public void import_emp(List<add_employee> emplist) {
        for (int i=0;i<emplist.size();i++){
            String password=String.valueOf(123456);
            add_employee addEmployee=emplist.get(i);
            employeeBasicMapper.employeeAdd(addEmployee,password);
            employeeBasic employeeBasic=employeeBasicMapper.findByEmployeePhoneNumber(addEmployee.getEmployeePhoneNumber());
            String id=employeeBasic.getEmployeeId();
            employeeBasicMapper.employeeJobAdd(addEmployee,id);
        }
    }

    @Override
    public void add_Avatar(String url,String claim) {
        employeeBasicMapper.add_Avatar(url,claim);
    }

    @Override
    public String getpassword(String claim) {
      String password =  employeeBasicMapper.getpassword(claim);
      return password;
    }

    @Override
    public void password_change(String newPassword, String claim) {
        employeeBasicMapper.change_password(newPassword,claim);
    }

    @Override
    public void app_update(app_update_employee appUpdateEmployee, String claim) {
        if(appUpdateEmployee.getEmployeeName()!=null)
            employeeBasicMapper.app_update_empbasic_name(appUpdateEmployee.getEmployeeName(),claim);
        if(appUpdateEmployee.getEmployeePhoneNumber()!=null)
            employeeBasicMapper.app_update_empbasic_phonenum(appUpdateEmployee.getEmployeePhoneNumber(),claim);
        if(appUpdateEmployee.getEmployeeBirthday()!=null)
            employeeBasicMapper.app_update_empbasic_birthday(appUpdateEmployee.getEmployeeBirthday(),claim);
        if(appUpdateEmployee.getDeptName()!=null) {
            String deptname=appUpdateEmployee.getDeptName();
            String deptno=departmentMapper.findByDeptName(deptname);
            employeeBasicMapper.app_update_jobinfo_deptno(deptno, claim);
        }
        if(appUpdateEmployee.getEmployeeJob()!=null)
            employeeBasicMapper.app_update_jobinfo_empjob(appUpdateEmployee.getEmployeeJob(),claim);
    }
}
