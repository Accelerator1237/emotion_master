package com.project.controller;

import com.project.pojo.Result;
import com.project.pojo.department;
import com.project.pojo.employeeBasic;
import com.project.vo.add_department;
import com.project.vo.info_department;
import com.project.pojo.pageBean;
import com.project.service.departmentService;
import com.project.service.employeeBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("department")
public class departmentController {
    @Autowired(required = false)
    private departmentService departmentService;
    @Autowired
    private employeeBasicService employeeBasicService;

    @PutMapping("/dept_add")
    public Result departmentAdd(@RequestBody add_department department){
        department department1=departmentService.findByDepartNo(department.getHigherDeptNo());

        employeeBasic employeeBasic=employeeBasicService.findByEmployeeId(department.getDeptManagerId());
        if(department1==null && department.getHigherDeptNo()!=null) {
            return Result.error("高级部门不存在");
        }
        else if(employeeBasic==null){
            return Result.error("经理id不存在");
        }
        else{
            departmentService.add(department);
            return Result.success();
        }

    }

    //通过部门序号查询部门信息
    @GetMapping("/dept_info")
    public Result<department> departmentInfo(Integer deptNo){
        department department = departmentService.findByDepartNo(deptNo);
        //返回为空，部门不存在
        if(department == null)
        {
            return Result.error("部门不存在");
        }
        //反之，返回部门信息
        else {
            return Result.success(department);
        }
    }

    @DeleteMapping("/dept_delete")
    public Result departmentDelete(Integer deptNo){
        department department = departmentService.findByDepartNo(deptNo);
        if(department == null)
        {
            return Result.error("部门不存在");
        }
        else{
            departmentService.departmentDelete(deptNo);
            return Result.success();
        }
    }

    //更改部门
    @PostMapping("/dept_change")
    public Result departmentChange(@RequestBody department department){
        employeeBasic employeeBasic=employeeBasicService.findByEmployeeId(String.valueOf(department.getDeptManagerId()));
        if(departmentService.findByDepartNo(department.getDeptNo()) == null)
        {
            return Result.error("部门不存在");
        }
        else if(employeeBasic==null){
            return Result.error("经理员工不存在");
        }
        else{
            departmentService.departmentChange(department);
            return Result.success();
        }
    }

    @GetMapping("list")
    public Result<pageBean<info_department>> list(Integer pageNum,Integer pageSize,
                                            @RequestParam(required = false) String departmentNo,
                                            @RequestParam(required = false) String phoneNumber,
                                            @RequestParam(required = false) String manager){
        if(pageNum != null && pageSize != null){
            pageBean<info_department> departmentpageBean = departmentService.department_list(pageNum,pageSize,departmentNo,phoneNumber,manager);
            return Result.success(departmentpageBean);
        }
        return Result.error("pageNum为空");
    }

}
