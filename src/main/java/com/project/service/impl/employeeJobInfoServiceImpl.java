package com.project.service.impl;

import com.project.pojo.employeeJobInfo;
import com.project.service.employeeJobInfoService;
import com.project.mapper.employeeJobInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class employeeJobInfoServiceImpl implements employeeJobInfoService {
    @Autowired(required = false)
    private employeeJobInfoMapper employeeJobInfoMapper;

    @Override
    public void employeeJobAdd(employeeJobInfo employeeJobInfo) {
        employeeJobInfoMapper.empJobAdd(employeeJobInfo);
    }

    @Override
    public employeeJobInfo employeeJobFindById(String employeeId) {
        employeeJobInfo employeeJobInfo=employeeJobInfoMapper.employeeJobFindById(employeeId);
        return employeeJobInfo;
    }
}
