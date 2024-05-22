package com.project.pojo;

import lombok.Data;

@Data
public class department {
    int deptNo;         //部门序号，主键
    String deptName;    //部门名称
    int deptManagerId;  //部门经理Id
    int higherDeptNo;   //领导部门序号
}
