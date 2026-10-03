package com.example.movie.controller.admin;

import com.example.movie.Utils.OssUtil;
import com.example.movie.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 作者: 枷
 * 时间:2026/8/2 下午1:32
 * 描述:上传文件 - 参数名file
 */
@RestController
@Slf4j
@Tag(name = "上传文件")
public class UploadController {
    @Autowired
    private OssUtil ossUtil;
    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> uploadPoster(@RequestParam MultipartFile file) throws IOException {
        // 上传到 poster 文件夹
        String url = ossUtil.uploadFile(file, "poster");
        return Result.success(url);
    }
}
