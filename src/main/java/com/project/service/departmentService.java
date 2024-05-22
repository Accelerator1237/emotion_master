package com.project.service;

import com.project.pojo.department;
import com.project.pojo.pageBean;
import com.project.vo.add_department;
import com.project.vo.info_department;

public interface departmentService {
    void add(add_department department);

    department findByDepartNo(Integer deptNo);

    void departmentDelete(Integer deptNo);

    void departmentChange(department department);

    pageBean<info_department> department_list(Integer pageNum, Integer pageSize, String departmentName, String phoneNumber, String manager);
}
