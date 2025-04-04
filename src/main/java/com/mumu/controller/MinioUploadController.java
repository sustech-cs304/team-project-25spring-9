package com.mumu.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/upload")
public class MinioUploadController {
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.userPath}")
    private String userImgPath;

    @ApiOperation(value = "上传图片", notes = "仅测试使用", tags = "测试类")
    @GetMapping("/userimg")
    public Object upload(MultipartFile file) {
        minioUtilS.upload_old(file, userImgPath, String.format("%s.jpeg", StpUtil.getLoginId()));
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "上传文件", notes = "仅测试使用", tags = "测试类")
    @GetMapping("/up")
    public Object upload(MultipartFile[] file) {
        minioUtilS.upload(file);
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "压缩并上传文件", notes = "仅测试使用", tags = "测试类")
    @PostMapping("/uploadAndCompressFile")
    public Object uploadAndCompressFile(MultipartFile file) {
        minioUtilS.upload(file, userImgPath, "压缩并上传文件的测试.jpeg");
        return AjaxJson.getSuccess();
    }
}

