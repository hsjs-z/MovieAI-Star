package com.example.movie.Utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午2:51
 * 描述:写 OSS 工具类（上传、删除、获取地址）
 */
@Configuration
public class OssUtil {


    @Autowired
    private OSS ossClient;

    @Value("${alioss.bucket-name}")
    private String bucketName;

    @Value("${alioss.endpoint}")
    private String endpoint;

    /**
     * 文件上传
     * @param file 前端传的文件
     * @param folder 上传文件夹，比如 movie、poster、avatar
     * @return 文件完整访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 唯一文件名，防止重名覆盖
        String fileName = folder + "/" + UUID.randomUUID() + suffix;

        // 上传
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file.getInputStream());
        ossClient.putObject(request);

        // 拼接访问地址
        return "https://" + bucketName + "." + endpoint + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        // 截取桶后面的路径
        String prefix = "https://" + bucketName + "." + endpoint + "/";
        String objectName = fileUrl.replace(prefix, "");
        ossClient.deleteObject(bucketName, objectName);
    }
}
