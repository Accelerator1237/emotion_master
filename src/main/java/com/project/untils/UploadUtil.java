package com.project.untils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.project.config.AlibabaCloudConfig;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class UploadUtil {
    public static final String ALI_DOMAIN = "https://emotion-master.oss-cn-shenzhen.aliyuncs.com/";

    public static String uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = "." + FilenameUtils.getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = "Avatar/" + uuid + ext;

        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";

        String accessKeyId = AlibabaCloudConfig.getAccessKeyId();
        String accessKeySecret = AlibabaCloudConfig.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject("emotion-master", fileName, file.getInputStream());
        ossClient.shutdown();

        return ALI_DOMAIN + fileName;
    }
}
