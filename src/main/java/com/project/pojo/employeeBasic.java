package com.project.pojo;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class employeeBasic {
    String employeeName;        //员工姓名
    String employeePassword;    //登录密码
    String employeePhoneNumber; //员工电话号码
    String employeeAvatar;      //员工头像（保存头像的文件地址）
    String employeeBirthday;    //员工生日
    Integer employeeGender;         //员工性别
    String employeeId;          //员工id，作为主键


}
