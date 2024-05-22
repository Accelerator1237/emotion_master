package com.project.vo;

import lombok.Data;

@Data
public class add_department {
    String deptName;    //部门名称
    String deptManagerId;  //部门经理Id
    Integer higherDeptNo;   //领导部门序号
}
