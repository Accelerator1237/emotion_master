package com.project.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.mapper.departmentMapper;
import com.project.mapper.emotionMapper;
import com.project.pojo.data_center;
import com.project.pojo.deptEmotion;
import com.project.pojo.pageBean;
import com.project.pojo.report;
import com.project.service.reportService;
import com.project.vo.emotion_report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 表明这是一个服务组件
@Service
public class reportServiceImpl implements reportService {

    // 注入Spring的RestTemplate用于REST API调用
    private final RestTemplate restTemplate;
    public reportServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 自动装配 emotionMapper，用于数据库操作，若无对应bean则不装配
    @Autowired(required = false)
    private emotionMapper emotionMapper;


    // 实现 reportService 接口的 detectEmotion 方法
    @Override
    public String detectEmotion(MultipartFile imageFile) throws IOException {
        // 定义要调用的服务的URL
        String url = "http://8.134.248.200:5000/analyze-emotion";

        // 设置请求头，表明内容类型为多部分表单数据
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建请求体，添加图片文件
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource resource = new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return imageFile.getOriginalFilename(); // 获取文件名
            }
        };

        body.add("image", resource);

        // 创建 HTTP 实体，包括请求体和头部信息
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 使用 RestTemplate 发送 POST 请求，并接收响应
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }


    // 实现 reportService 接口的 addReport 方法
    @Override
    public void addReport(report report) {
        // 调用 emotionMapper 的 addReport 方法将报告添加到数据库
            emotionMapper.addReport(report);
    }

    @Override
    public pageBean<emotion_report> list(Integer pageNum, Integer pageSize, String employeeId, String recordTime, String warningType) {
        pageBean<emotion_report> emotionReportpageBean=new pageBean<>();
        PageHelper.startPage(pageNum,pageSize);

        List<emotion_report> reportList=emotionMapper.list(employeeId,recordTime,warningType);

        Page<emotion_report> p=(Page<emotion_report>) reportList;

        emotionReportpageBean.setTotal(p.getTotal());
        emotionReportpageBean.setItems(p.getResult());

        return emotionReportpageBean;
    }

    @Override
    public Integer count_num(String formattedDate) {
        return emotionMapper.countNum(formattedDate);
    }



    private data_center statistics(List<report> reports) {
        data_center stats = new data_center();
        Pattern pattern = Pattern.compile("(\\w+)=(\\d+\\.\\d+)");

        for (report report : reports) {
            String emotionData = report.getEmotionData();
            Matcher matcher = pattern.matcher(emotionData);
            double maxEmotionValue = -1;
            String maxEmotion = "";

            while (matcher.find()) {
                String emotion = matcher.group(1);
                double value = Double.parseDouble(matcher.group(2));

                if (value > maxEmotionValue) {
                    maxEmotionValue = value;
                    maxEmotion = emotion;
                }
            }

            switch (maxEmotion) {
                case "anger":
                    stats.setAnger(stats.getAnger() + 1);
                    break;
                case "contempt":
                    stats.setContempt(stats.getContempt() + 1);
                    break;
                case "disgust":
                    stats.setDisgust(stats.getDisgust() + 1);
                    break;
                case "fear":
                    stats.setFear(stats.getFear() + 1);
                    break;
                case "happy":
                    stats.setHappy(stats.getHappy() + 1);
                    break;
                case "neutral":
                    stats.setNeutral(stats.getNeutral() + 1);
                    break;
                case "sad":
                    stats.setSad(stats.getSad() + 1);
                    break;
                case "surprise":
                    stats.setSurprise(stats.getSurprise() + 1);
                    break;
                default:
                    break;
            }
        }
        return stats;
    }

    @Override
    public data_center calculateEmotionStatistics() {
        //获取最新的报告
        List<report> reports = emotionMapper.findLatestReports();
        //方法statistics,计算每种情绪
        return statistics(reports);

    }


    @Override
    public deptEmotion getDeptEmotion(int deptNo) {
        List<report> reports = emotionMapper.findLatestReportsBydeptNo(deptNo);

        // 获取部门名称
        String departmentName = emotionMapper.findDepartmentNameByDeptNo(deptNo);

        data_center stats = statistics(reports);

        deptEmotion deptEmotion = new deptEmotion();

        deptEmotion.setDeptName(departmentName);
        deptEmotion.setData_center(stats);

        return deptEmotion;
    }


}
