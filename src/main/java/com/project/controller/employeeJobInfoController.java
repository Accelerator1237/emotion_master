package com.project.controller;

import com.project.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.project.service.employeeJobInfoService;
import com.project.pojo.employeeJobInfo;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("employeeJobInfo")
public class employeeJobInfoController {
    @Autowired(required = false)
    private employeeJobInfoService employeeJobInfoService;

    @PutMapping("empJob_add")
    public Result employeeJobAdd(employeeJobInfo employeeJobInfo){
        employeeJobInfoService.employeeJobAdd(employeeJobInfo);
        return Result.success();
    }

    @GetMapping("empJob_info")
    public Result<employeeJobInfo> employeeJobInfoResult(String employeeId){
        employeeJobInfo employeeJobInfo=employeeJobInfoService.employeeJobFindById(employeeId);
        return Result.success(employeeJobInfo);
    }
}
