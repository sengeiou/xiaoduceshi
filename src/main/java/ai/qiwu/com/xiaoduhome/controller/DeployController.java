package ai.qiwu.com.xiaoduhome.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务部署
 * @author hjd
 */
@RestController
@RequestMapping("/xiaoduceshi")
@Slf4j
public class DeployController {
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public DeployController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @PostMapping("/chat") //交游天下
    public String chat() {
        return "nuaa";
    }
}
