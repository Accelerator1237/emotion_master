package com.project.mapper;

import com.project.pojo.department;
import com.project.vo.add_department;
import com.project.vo.info_department;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface departmentMapper {
    @Insert("insert into department(deptName, deptManagerId,higherDeptNo)" +
    "values (#{deptName}, #{deptManagerId},#{higherDeptNo})")
    void add(add_department department);

    @Select("select * from department where deptNo=#{deptNo}")
    department findByDepartment(Integer deptNo);

    @Delete("delete from department where deptNo=#{deptNo}")
    void departmentDelete(Integer deptNo);

    @Update("update department set  deptManagerId=#{deptManagerId} where deptNo=#{deptNo}")
    void departmentChange(department department);

    List<info_department> department_list(String departmentName, String phoneNumber, String manager);

//    int count(String departmentName);
}
