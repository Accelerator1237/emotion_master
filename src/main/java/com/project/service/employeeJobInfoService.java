package com.project.service;

import com.project.pojo.employeeJobInfo;

public interface employeeJobInfoService {
    void employeeJobAdd(employeeJobInfo employeeJobInfo);

    employeeJobInfo employeeJobFindById(String employeeId);
}
