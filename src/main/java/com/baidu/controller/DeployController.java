package com.baidu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: huangganda
 * @Data: 2021-01-12 17:07
 */
@RestController
@Slf4j
@RequestMapping("/xiaoduceshi")
public class DeployController {
    private final StringRedisTemplate stringRedisTemplate;
    @Autowired
    public DeployController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RequestMapping(value = "/chat",method = RequestMethod.HEAD)
    public void chatHead(HttpServletResponse response){
        response.setStatus(200);
    }

    @PostMapping(value = "/chat")
    public String chat(){
        log.warn("stringRedisTemplate:{}",stringRedisTemplate);
        return "null";
    }
}
