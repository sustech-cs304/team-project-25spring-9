package com.mumu.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/download")
public class MinioDownloadController {
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.userPath}")
    private String userImgPath;

    @ApiOperation(value = "下载图片", notes = "仅测试使用", tags = "测试类")
    @GetMapping("/userimg")
    public Object download(){
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.jpeg", StpUtil.getLoginId()),userImgPath));
    }

}

