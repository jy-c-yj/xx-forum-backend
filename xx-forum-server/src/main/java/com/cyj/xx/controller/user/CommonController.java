package com.cyj.xx.controller.user;


import com.cyj.xx.result.Result;
import com.cyj.xx.utils.AliOssUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil ossUtil;


    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传开始{}", file.getOriginalFilename());
        try {
            //获取原始文件名
            String s = file.getOriginalFilename();
            //截取文件后缀
            String suffix = s.substring(s.lastIndexOf("."));
            //拼接文件名
            String newFileName = UUID.randomUUID().toString()+suffix;
            //上传
            String url = ossUtil.Upload(file.getBytes(), newFileName);

            return Result.success(url);

        } catch (Exception e) {

            log.info("文件上传异常{}", e.getMessage());

        }

        return Result.error("文件上传失败");

    }
}
