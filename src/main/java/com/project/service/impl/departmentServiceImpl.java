package com.project.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.pojo.pageBean;
import com.project.service.departmentService;
import com.project.vo.add_department;
import com.project.vo.info_department;
import org.springframework.beans.factory.annotation.Autowired;
import com.project.mapper.departmentMapper;
import com.project.pojo.department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class departmentServiceImpl implements departmentService {
    @Autowired(required = false)
    private departmentMapper departmentMapper;

    @Override
    public void add(add_department department){
        departmentMapper.add(department);
        String deptNo = departmentMapper.findDeptNo(department.getDeptName(), department.getDeptManagerId());
        departmentMapper.deleteEmpJobInfo(department.getDeptManagerId());
        departmentMapper.addEmpJobInfo(department.getDeptManagerId(), deptNo);
    }

    @Override
    public department findByDepartNo(Integer deptNo) {
        return departmentMapper.findByDepartment(deptNo);
    }

    @Override
    public void departmentDelete(Integer deptNo) {
        departmentMapper.departmentDelete(deptNo);
    }

    @Override
    public void departmentChange(department department) {
        departmentMapper.departmentChange(department);
    }

    @Override
    public pageBean<info_department> department_list(Integer pageNum, Integer pageSize, String departmentNo, String phoneNumber, String manager) {

//        int num;
//        num = departmentMapper.count(departmentName);//怎么注入进item，数量在SQL里没问题

        pageBean<info_department> departmentpageBean = new pageBean<>();

        PageHelper.startPage(pageNum,pageSize);

        List<info_department> departments = departmentMapper.department_list(departmentNo, phoneNumber, manager);


        Page<info_department> p=(Page<info_department>) departments;

        departmentpageBean.setTotal(p.getTotal());
        departmentpageBean.setItems(p.getResult());

        return departmentpageBean;
    }
}
