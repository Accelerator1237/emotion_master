package com.project.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.project.pojo.*;
import com.project.service.departmentService;
import com.project.service.employeeBasicService;
import com.project.service.employeeJobInfoService;
import com.project.untils.JwtUtil;
import com.project.untils.UploadUtil;
import com.project.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("employeeBasic")
public class employeeBasicController {
    @Autowired(required = false)
    private employeeBasicService employeeBasicService;

    @Autowired
    private employeeJobInfoService employeeJobInfoService;

    @Autowired
    private departmentService departmentService;

    //注册账号，需要员工的姓名，账号密码，手机号码，生日以及性别等即可，同时这也是实现录入员工信息功能的一环，部门可以通过上传员工信息，并且统一密码为123456来进行录入员工信息
    @PutMapping("/register")
    public Result register(String employeeName, String employeePassword, String employeePhoneNumber, String employeeAvatar, String employeeBirthday, int employeeGender) {
        employeeBasicService.register(employeeName, employeePassword, employeePhoneNumber, employeeAvatar, employeeBirthday, employeeGender);
        return Result.success();
    }

    @PutMapping("/add")
    public Result employeeAdd(@RequestBody add_employee employee) {
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(employee.getEmployeeBirthday());

        String phonenumber = employee.getEmployeePhoneNumber();
        employeeBasic employee_basic = employeeBasicService.findByEmployeePhoneNumber(phonenumber);
        if (employee_basic != null) {
            return Result.error("员工已存在（该电话号码重复）");
        }

        if (employee.getEmployeeRole() == null) {
            return Result.error("员工身份不能为空");
        } else if (employee.getEmployeeJob() == null) {
            return Result.error("员工职位不能为空");
        } else if (employee.getEmployeeGender() > 2) {
            return Result.error("员工性别不合法");
        } else if (!matcher.matches()) {
            return Result.error("日期格式不合法");
        } else {
            employeeBasicService.employeeAdd(employee);
            return Result.success();
        }
    }

    //通过员工的id查询员工的个人信息
    @GetMapping("/info")
    public Result<employeeBasic> employeeBasicInfo(String employeeId) {
        //调用service层的函数，通过employeeId来查找得到员工的信息保存到员工基础信息类当中
        employeeBasic employeeBasic = employeeBasicService.findByEmployeeId(employeeId);
        //如果找到的员工基础信息类结果为空，则找不到该用户
        if (employeeBasic == null) {
            return Result.error("用户不存在");
        }
        //不为空则返回员工的所有基础信息
        else {
            return Result.success(employeeBasic);
        }
    }

    //通过输入员工手机号码和密码进行登录操作
    @PostMapping("/login")
    public Result employeeLogin(@RequestBody emp_login empLogin) {
        employeeBasic employeeBasic = employeeBasicService.findByEmployeePhoneNumber(empLogin.getEmployeePhoneNumber());

        //如果查询得到的员工类为空，则用户不存在
        if (employeeBasic == null) {
            return Result.error("用户不存在");
        }
        //如果查询到的员工类的账号或者密码不匹配，则输出账号或密码不正确
        else if (!Objects.equals(empLogin.getEmployeePhoneNumber(), employeeBasic.getEmployeePhoneNumber()) || !Objects.equals(empLogin.getEmployeePassword(), employeeBasic.getEmployeePassword())) {
            return Result.error("账号或密码不正确");
        }
        //账号密码都正确返回正确的结果
        else {
            String employeeId = employeeBasic.getEmployeeId();
            employeeJobInfo employeeJobInfo = employeeJobInfoService.employeeJobFindById(employeeId);
            //创建哈希表，保存token还有用户数据
            Map<String, Object> claims = new HashMap<>();
            claims.put("employeeRole", employeeJobInfo.getEmployeeRole());
            claims.put("employeeId", employeeBasic.getEmployeeId());
            String token = JwtUtil.genToken(claims);
            token Token = new token();
            Token.setToken(token);
//            token="token:"+token;
            //将生成的token包装返回给前端界面
            return Result.success(Token);
        }
    }

    //实现分页查询功能，根据前端传过来的页数和最大数量，来实现对查询的数据的分页
    @GetMapping("/list")
    public Result<pageBean<employee>> list(Integer pageNum, Integer pageSize,
                                           @RequestParam(required = false) String role,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String phoneNumber,
                                           @RequestParam(required = false) Integer gender,
                                           @RequestParam(required = false) Integer departmentNo,
                                           @RequestParam(required = false) String beginBirthday,
                                           @RequestParam(required = false) String endBirthday) {
        if (pageNum != null && pageSize != null) {
            pageBean<employee> employeeBasicpageBean = employeeBasicService.list(pageNum, pageSize, role, name, phoneNumber, gender, departmentNo, beginBirthday, endBirthday);
            return Result.success(employeeBasicpageBean);
        }
        return Result.error("pageNum为空");
    }

    //实现修改密码功能
    @PostMapping("/reset_password")
    public Result empPasswordReset(String employeeId) {
        //通过查找相关员工id的员工信息，将新密码输入之后，直接修改密码为输入的新密码
        employeeBasic employeeBasic = employeeBasicService.findByEmployeeId(employeeId);
        if (employeeBasic == null) {
            return Result.error("用户不存在");
        } else {
            employeeBasicService.employeePasswordReset(employeeId);
            return Result.success();
        }
    }

/*    //修改员工基础信息功能，因为密码已经提供单独接口，所以不在这里修改密码，同时性别和id第一次注册已经固定，不提供修改功能
    @PutMapping("/employeeBasic_update")
    public Result employeeBasicUpdate(@RequestBody employeeBasic employeeBasic,@RequestHeader("Authorization") String token){
        Map<String,Object> claims=JwtUtil.parseTokenToEmployeeId(token);
        String claim= String.valueOf(claims.get("employeeId"));
        employeeBasic employeeBasic1=employeeBasicService.findByEmployeeId(claim);
        if(employeeBasic1==null){
            return Result.error("用户不存在");
        }
        else{
            employeeBasicService.employeeBasicUpdate(employeeBasic,claim);
            return Result.success();
        }
    }*/

    //根据前端上传的token数据，解析token并且返回token中有关用户id的信息
    @GetMapping("/info_bytoken")
    public Result<employee_Basic> employeeBasicInfoByToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> claims = JwtUtil.parseTokenToEmployeeId(token);
        String claim = String.valueOf(claims.get("employeeId"));
        employee_Basic employee_Basic = employeeBasicService.findByToken(claim);
        return Result.success(employee_Basic);
    }

    @DeleteMapping("/delete_employee")
    public Result employeeDelete(String employeeId) {
        employeeBasic employeeBasic = employeeBasicService.findByEmployeeId(employeeId);
        if (employeeBasic == null) {
            return Result.error("用户不存在");
        } else {
            employeeBasicService.employeeDelete(employeeId);
            return Result.success();
        }
    }

    @PostMapping("/update_employee")
    public Result employeeUpdate(@RequestBody update_employee updateEmployee) {
        employeeBasic employeeBasic = employeeBasicService.findByEmployeeId(updateEmployee.getEmployeeId());
        department department = departmentService.findByDepartNo(updateEmployee.getEmployeeDepartmentNo());
        if (employeeBasic == null) {
            return Result.error("用户不存在");
        } else if (department == null) {
            return Result.error("部门不存在");
        } else if (updateEmployee.getEmployeeRole() == null) {
            return Result.error("员工身份不能为空");
        } else if (updateEmployee.getEmployeeJob() == null) {
            return Result.error("员工职位不能为空");
        }
//        else if(!Objects.equals(updateEmployee.getEmployeeId(), employeeBasic.getEmployeeId())){
//           return Result.error("不允许更改员工id");
//        }
        else {
            employeeBasicService.Update(updateEmployee);
            return Result.success();
        }
    }

    @PostMapping("/app_update_employee")
    public Result AppemployeeUpdate(@RequestHeader("Authorization") String token, @RequestBody app_update_employee app_update_employee) {
        Map<String, Object> claims = JwtUtil.parseTokenToEmployeeId(token);
        String claim = String.valueOf(claims.get("employeeId"));
        employeeBasicService.app_update(app_update_employee, claim);
        return Result.success();
    }

    @PostMapping("/import_emp")
    public Result import_emp(MultipartFile file) throws IOException {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<add_employee> emplist = reader.readAll(add_employee.class);
        for (int i = 0; i < emplist.size(); i++) {
            add_employee addEmployee = emplist.get(i);
            String phonenumber = addEmployee.getEmployeePhoneNumber();
            employeeBasic employee_basic = employeeBasicService.findByEmployeePhoneNumber(phonenumber);
            if (employee_basic != null) {
                return Result.error("员工" + addEmployee.getEmployeeName() + "手机号重复");
            }
        }
        employeeBasicService.import_emp(emplist);
        return Result.success();
    }

//    @PostMapping("/upload_Avatar")
//    public Result upload_Avatar(@RequestHeader("Authorization") String token,MultipartFile file)throws IOException{
//        String url= UploadUtil.uploadImage(file);
//        Map<String,Object> claims= JwtUtil.parseTokenToEmployeeId(token);
//        String claim= String.valueOf(claims.get("employeeId"));
//        employeeBasicService.add_Avatar(url,claim);
//        return Result.success();
//    }

    @PostMapping("/upload_Avatar")
    public ResponseEntity<Result> upload_Avatar(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file) throws IOException {
        // 上传图片到存储服务器，获取图片URL
        String url = UploadUtil.uploadImage(file);

        // 解析Token获取employeeId
        Map<String, Object> claims = JwtUtil.parseTokenToEmployeeId(token);
        String employeeId = String.valueOf(claims.get("employeeId"));

        // 调用微服务进行人脸编码
        String encodingUrl = "http://8.134.248.200:5001/encode";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(encodingUrl, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("encoding")) {
                String encoding = response.getBody().get("encoding").toString();
                // 将头像URL和编码结果保存到数据库
                employeeBasicService.add_AvatarAndEncoding(url, encoding, employeeId);
                return ResponseEntity.ok(Result.success());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error("请上传含有正面完整人脸的图片"));
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error("请上传含有正面完整人脸的图片"));
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getResponseBodyAsString();
                if (errorMessage.contains("No face found")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error("请上传含有正面完整人脸的图片"));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(errorMessage));
                }
            } else {
                return ResponseEntity.status(e.getStatusCode()).body(Result.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/change_password")
    public Result password_change(@RequestHeader("Authorization") String token, String old_password, String new_password) {
        Map<String, Object> claims = JwtUtil.parseTokenToEmployeeId(token);
        String claim = String.valueOf(claims.get("employeeId"));
        String password = employeeBasicService.getpassword(claim);
        if (!Objects.equals(old_password, password)) {
            return Result.error("原密码错误");
        } else {
            employeeBasicService.password_change(new_password, claim);
            return Result.success();
        }
    }



}
