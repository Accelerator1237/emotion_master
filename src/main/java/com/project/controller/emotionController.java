package com.project.controller;

import com.project.pojo.*;
import com.project.service.reportService;
import com.project.untils.JwtUtil;
import com.project.vo.emotion_report;
import com.project.vo.emotion_response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

//@CrossOrigin(origins = "*")
// 声明这是一个 REST 控制器
@RestController
// 定义类请求路径
@RequestMapping("/emotion")
public class emotionController {

    @Autowired(required = false)
    private com.project.service.departmentService departmentService;

    @Autowired
    private com.project.service.employeeBasicService employeeBasicService;

    // 自动装配 reportService，如果没有匹配的bean，则不装配
    @Autowired(required = false)
    private  reportService reportService;

//    public emotionController(emotionDetectionService emotionDetectionService) {
//        this.reportService = emotionDetectionService;
//    }

    private final RestTemplate restTemplate = new RestTemplate();

    // 人脸对比方法
    private boolean compareFace(MultipartFile image, String encoding) throws IOException {
        String url = "http://8.134.248.200:5001/recognize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource resource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        body.add("file", resource);
        body.add("encoding", encoding);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Boolean.parseBoolean(response.getBody().get("match").toString());
        } else {
            return false;
        }
    }


    // 处理 POST 请求，用于上传图片并检测表情
    @PostMapping("/detect")
    public Result<report> uploadAndDetect(@RequestHeader("Authorization") String token,
                                          @RequestParam("image") MultipartFile image) {
        try {
            // 解析Token获取employeeId
            Map<String, Object> claims = JwtUtil.parseTokenToEmployeeId(token);
            String employeeId = String.valueOf(claims.get("employeeId"));

            // 从数据库获取用户的avatarEncoding
            String avatarEncoding = employeeBasicService.getAvatarEncoding(employeeId);
            if (avatarEncoding == null) {
                return Result.error("用户没有保存的头像编码");
            }

            // 调用人脸对比微服务
            boolean isFaceMatch = compareFace(image, avatarEncoding);
            if (!isFaceMatch) {
                return Result.error("人脸比对不成功");
            }

            // 调用情绪识别微服务
            String resultString = reportService.detectEmotion(image);
            System.out.println(resultString);

            // 使用正则表达式匹配 JSON 字符串中的数据
            Pattern pattern = Pattern.compile("([0-9]*\\.?[0-9]+)");
            Matcher matcher = pattern.matcher(resultString);

            // 初始化响应对象和报告对象
            emotion_response response = new emotion_response();
            report report = new report();
            int index = 0;
            int maxKey = 0;
            String suggestion = "";

            // 定义情绪类型
            String[] keys = {"anger", "contempt", "disgust", "fear", "happy", "neutral", "sad", "surprise"};

            // 根据表情类型提供的建议数组
            String[][] suggestions = new String[8][10];
            suggestions[0] = new String[]{
                    "几乎没有愤怒情绪，可能表现出非常平静和放松的状态。",
                    "极轻微的愤怒情绪，可能是由于某些微小的不适或不满引起的，但并不明显。",
                    "轻微的愤怒情绪，可能显示出些许烦躁或不耐烦的迹象，但整体情绪仍然相对平和。",
                    "稍微明显的愤怒情绪，可能表示对某些事情感到不满或愤怒，但还没有达到明显的愤怒状态。",
                    "中等程度的愤怒情绪，可能显示出明显的烦躁或愤怒，但仍能保持相对控制。",
                    "较强烈的愤怒情绪，可能表现出明显的不满或愤怒，可能需要一些时间来缓解情绪。",
                    "相当明显的愤怒情绪，可能伴随着口头上的表达或身体语言上的暗示，需要较多的情绪调节。",
                    "很强烈的愤怒情绪，可能表现出愤怒的言辞或行为，需要较长时间来恢复平静。",
                    "极度强烈的愤怒情绪，可能难以控制，可能会导致冲动的行为或言语。",
                    "极端愤怒，可能会导致暴力行为或失去理智的状态。"
            };

            suggestions[1] = new String[]{
                    "几乎没有蔑视情绪，可能表现出礼貌和尊重。",
                    "极轻微的蔑视情绪，可能由于轻微的不满或不尊重引起，但并不明显。",
                    "轻微的蔑视情绪，可能表现出些许不耐烦或不满，但整体仍然保持相对礼貌。",
                    "稍微明显的蔑视情绪，可能表现出对某些人或事物的轻微不屑或不尊重。",
                    "中等程度的蔑视情绪，可能显示出明显的不满或不尊重，但还能保持一定的礼貌。",
                    "较强烈的蔑视情绪，可能表现出明显的不耐烦或轻视，可能需要一些时间来缓解情绪。",
                    "相当明显的蔑视情绪，可能伴随着一些不礼貌或轻蔑的言辞或行为。",
                    "很强烈的蔑视情绪，可能表现出明显的轻蔑或傲慢，需要较长时间来平复情绪。",
                    "极度强烈的蔑视情绪，可能难以控制，可能会导致不礼貌或冲动的行为。",
                    "极端蔑视，可能会表现出极度不尊重或轻视的态度，可能导致冲动或冲突。"
            };

            suggestions[2] = new String[]{
                    "几乎没有厌恶情绪，可能表现出舒适和愉悦的状态。",
                    "极轻微的厌恶情绪，可能由于某些微小的不适引起，但并不明显。",
                    "轻微的厌恶情绪，可能显示出对某些事物的轻微不满或不适。",
                    "稍微明显的厌恶情绪，可能表现出对某些事物的不适或反感，但还不至于影响整体情绪。",
                    "中等程度的厌恶情绪，可能显示出明显的反感或不满，但仍能保持相对平静。",
                    "较强烈的厌恶情绪，可能表现出明显的不适或恶心感，可能需要一些时间来缓解情绪。",
                    "相当明显的厌恶情绪，可能伴随着身体语言上的暗示或不适，需要一定的情绪调节。",
                    "很强烈的厌恶情绪，可能表现出明显的恶心或反感，需要较长时间来恢复平静。",
                    "极度强烈的厌恶情绪，可能难以控制，可能会导致冲动的行为或言语。",
                    "极端厌恶，可能会导致恶心或身体上的不适，可能会有强烈的冲动或拒绝。"
            };
            suggestions[3] = new String[]{
                    "几乎没有恐惧情绪，可能表现出安全和放松的状态。",
                    "极轻微的恐惧情绪，可能由于轻微的不安或担忧引起，但并不明显。",
                    "轻微的恐惧情绪，可能显示出对某些事物的轻微不安或担忧。",
                    "稍微明显的恐惧情绪，可能表现出对某些事物的不安或担忧，但还不至于影响整体情绪。",
                    "中等程度的恐惧情绪，可能显示出明显的焦虑或不安，但仍能保持相对平静。",
                    "较强烈的恐惧情绪，可能表现出明显的紧张或不安，可能需要一些时间来缓解情绪。",
                    "相当明显的恐惧情绪，可能伴随着身体语言上的暗示或紧张感，需要一定的情绪调节。",
                    "很强烈的恐惧情绪，可能表现出明显的恐慌或不安，需要较长时间来恢复平静。",
                    "极度强烈的恐惧情绪，可能难以控制，可能会导致冲动的行为或言语。",
                    "极端恐惧，可能会导致恐慌或逃避的行为，可能会有强烈的生存本能反应。"
            };
            suggestions[4] = new String[]{
                    "几乎没有快乐情绪，可能表现出沮丧或低落的状态。",
                    "极轻微的快乐情绪，可能由于某些微小的愉悦或喜悦引起，但并不明显。",
                    "轻微的快乐情绪，可能显示出些许愉悦或满足感，但整体情绪仍然相对平淡。",
                    "稍微明显的快乐情绪，可能表示对某些事物的一些喜悦或满足，但还不至于引起明显的情绪波动。",
                    "中等程度的快乐情绪，可能显示出明显的愉悦或满足感，但仍保持相对平静。",
                    "较强烈的快乐情绪，可能表现出明显的愉悦或兴奋，可能需要一些时间来缓解情绪。",
                    "相当明显的快乐情绪，可能伴随着笑容或积极的身体语言，需要一定的情绪调节。",
                    "很强烈的快乐情绪，可能表现出明显的兴奋或喜悦，需要较长时间来平复情绪。",
                    "极度强烈的快乐情绪，可能难以控制，可能会导致欢笑或兴奋的行为。",
                    "极端快乐，可能会导致极度兴奋或喜悦，可能会有过度的情绪表达或行为。"
            };
            suggestions[5] = new String[]{
                    "几乎没有中性情绪，可能表现出明显的情绪，可能是快乐、悲伤、愤怒等。",
                    "极轻微的中性情绪，可能存在一些微弱的情绪迹象，但并不明显。",
                    "轻微的中性情绪，可能显示出些许的情感平稳，但仍有一些细微的情绪波动。",
                    "稍微明显的中性情绪，可能表现出相对平静和放松的状态，但仍有些许情感波动。",
                    "中等程度的中性情绪，可能显示出一定程度上的情感平稳，既不明显快乐也不明显悲伤。",
                    "较强烈的中性情绪，可能表现出相对稳定的情绪状态，没有明显的情感波动。",
                    "相当明显的中性情绪，可能伴随着较强的情感稳定性，很少有情绪波动。",
                    "很强烈的中性情绪，可能表现出非常平静和冷静的状态，几乎没有任何情绪表达。",
                    "极度强烈的中性情绪，可能表现出极度平静和稳定的状态，几乎没有任何情感波动。",
                    "极端中性，可能表现出极度平淡和冷漠的状态，没有任何情绪表达或波动。"
            };
            suggestions[6] = new String[]{
                    "几乎没有悲伤情绪，可能表现出轻松或平静的状态。",
                    "极轻微的悲伤情绪，可能由于某些微小的失落或不快引起，但并不明显。",
                    "轻微的悲伤情绪，可能显示出些许沮丧或失落感，但整体情绪仍然相对平和。",
                    "稍微明显的悲伤情绪，可能表现出对某些事物的失望或沮丧，但还不至于影响整体情绪。",
                    "中等程度的悲伤情绪，可能显示出明显的失落或忧伤，但仍能保持相对平静。",
                    "较强烈的悲伤情绪，可能表现出明显的悲伤或忧郁，可能需要一些时间来缓解情绪。",
                    "相当明显的悲伤情绪，可能伴随着哀伤的表情或身体语言，需要一定的情绪调节。",
                    "很强烈的悲伤情绪，可能表现出明显的绝望或沮丧，需要较长时间来恢复平静。",
                    "极度强烈的悲伤情绪，可能难以控制，可能会导致哭泣或消极的情绪表达。",
                    "极端悲伤，可能会导致极度的绝望或沉重的心情，可能需要专业的支持来应对。"
            };
            suggestions[7] = new String[]{
                    "几乎没有惊讶情绪，可能表现出平静和自在的状态。",
                    "极轻微的惊讶情绪，可能由于一些微小的惊喜或意外引起，但并不明显。",
                    "轻微的惊讶情绪，可能显示出些许意外或惊讶的表情，但整体情绪仍然相对平和。",
                    "稍微明显的惊讶情绪，可能表现出对某些事物的一些意外或惊讶，但还不至于引起明显的情绪波动。",
                    "中等程度的惊讶情绪，可能显示出明显的惊讶或意外，但仍能保持相对平静。",
                    "较强烈的惊讶情绪，可能表现出明显的惊讶或震惊，可能需要一些时间来缓解情绪。",
                    "相当明显的惊讶情绪，可能伴随着眼睛睁大或嘴巴张大的表情，需要一定的情绪调节。",
                    "很强烈的惊讶情绪，可能表现出明显的惊讶或惊慌，需要较长时间来恢复平静。",
                    "极度强烈的惊讶情绪，可能难以控制，可能会导致眼睛睁大或身体的不自然动作。",
                    "极端惊讶，可能会导致极度的震惊或惊慌，可能需要一段时间才能够恢复过来。"
            };

            double max = -1;
            // 遍历结果字符串，提取并处理数据
            while (matcher.find()) {
                double value = Double.parseDouble(matcher.group(1));
                if(max < value){
                    max = value;
                    if(max<70)
                        maxKey = 1;
                    else if(max >= 70 && max < 90)
                        maxKey=2;
                    else
                        maxKey=3;
                }


                // 根据 key 设置相应的属性值
                switch (keys[index]) {
                    case "anger":
                        response.setAnger(value);
                        suggestion = suggestion + "愤怒 : " + suggestions[0][(int)(value / 10)] + "\n";
                        break;
                    case "contempt":
                        response.setContempt(value);
                        suggestion = suggestion + "蔑视 : " + suggestions[1][(int)(value / 10)] + "\n";
                        break;
                    case "disgust":
                        response.setDisgust(value);
                        suggestion = suggestion + "厌恶 : " + suggestions[2][(int)(value / 10)] + "\n";
                        break;
                    case "fear":
                        response.setFear(value);
                        suggestion = suggestion + "恐惧 : " + suggestions[3][(int)(value / 10)] + "\n";
                        break;
                    case "happy":
                        response.setHappy(value);
                        suggestion = suggestion + "快乐 : " + suggestions[4][(int)(value / 10)] + "\n";
                        break;
                    case "neutral":
                        response.setNeutral(value);
                        suggestion = suggestion + "中性 : " + suggestions[5][(int)(value / 10)] + "\n";
                        break;
                    case "sad":
                        response.setSad(value);
                        suggestion = suggestion + "悲伤 : " + suggestions[6][(int)(value / 10)] + "\n";
                        break;
                    case "surprise":
                        response.setSurprise(value);
                        suggestion = suggestion + "惊讶 : " + suggestions[7][(int)(value / 10)] + "\n";
                        break;
                    default:
                        break;
                }
                index++;
            }
            System.out.println(suggestion);


            // 记录结果和建议
            report.setEmotionData(String.valueOf(response));
            report.setWarningType(maxKey);
            report.setSuggestion(suggestion);

            System.out.println(1);
////            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////            String currentDate = dateFormat.format(new Date());
//            LocalDate currentDate = LocalDate.now();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDate = formatter.format(date);
            report.setRecordTime(formattedDate);

            report.setEmployeeId(employeeId);
            System.out.println(6);


            // 存储报告
            reportService.addReport(report);
            System.out.println(7);
//            System.out.println(response);
            // 返回成功结果
            return Result.success(report);
        } catch (Exception e) {
            // 捕获异常，返回错误结果
            e.printStackTrace();
            return Result.error("识别失败");
        }
    }

    @GetMapping("/emo_list")
    private Result<pageBean<emotion_report>> list(Integer pageNum,Integer pageSize,
                                                  @RequestParam(required = false) String employeeId,
                                                  @RequestParam(required = false) String recordTime,
                                                  @RequestParam(required = false) String warningType){
        if(pageNum!=null && pageSize!=null){
            pageBean<emotion_report> reportpageBean=reportService.list(pageNum,pageSize,employeeId,recordTime,warningType);
            return Result.success(reportpageBean);
        }
        return Result.error("pageNum为空 or pageSize为空");
    }



    @GetMapping("/statistics")
    private Result<data_center> Data(){

        data_center data = new data_center();

        data = reportService.calculateEmotionStatistics();

        //当日时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        //当日检查的人数
        data.setInspectedNum(reportService.count_num(formattedDate));


        return Result.success(data);

    }

//获取部门情绪情况
    @GetMapping("/statistics_dept")
    private Result<deptEmotion> GetDeptEmotion(@RequestParam("deptNo") int deptNo)
    {
        if(departmentService.findByDepartNo(deptNo) == null)
        {
            return Result.error("部门不存在");
        }
        else{
            deptEmotion deptEmotion = reportService.getDeptEmotion(deptNo);
            return Result.success(deptEmotion);
        }

    }

//    @PostMapping("")

}
