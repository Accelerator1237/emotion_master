package com.project.vo;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class add_employee {
    @Alias("员工姓名")
    String employeeName;
    @Alias("员工电话号码")
    String employeePhoneNumber;
    @Alias("员工生日")
    String employeeBirthday;
    @Alias("员工性别")
    Integer employeeGender;
    @Alias("员工所属部门编号")
    Integer employeeDepartmentNo;
    @Alias("员工权限等级")
    Integer employeeRole;
    @Alias("员工职位")
    String employeeJob;
}
