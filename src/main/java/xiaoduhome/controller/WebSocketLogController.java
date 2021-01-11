package xiaoduhome.controller;

import ai.qiwu.com.xiaoduhome.service.chatsLog.ChatLogs;
import ai.qiwu.com.xiaoduhome.service.chatsLog.LoginData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 苗权威
 * @dateTime 19-11-18 下午6:37
 */
@Controller
@Slf4j
public class WebSocketLogController {

    private final ChatLogs chatLogs;
    private static ConcurrentHashMap<String, LoginData> loadState = new ConcurrentHashMap<>();

    @Autowired
    public WebSocketLogController(ChatLogs chatLogs) {
        this.chatLogs = chatLogs;
    }

    @GetMapping("/audio/index")
    public String logIndexPage(ModelMap modelMap) {
        return "index.html";
    }

    @GetMapping("/audio/Log1.html")
    public String logduPage(ModelMap modelMap) {
        return "Log1.html";
    }

    @GetMapping("/audio/Log2.html")
    public String logai1Page(ModelMap modelMap) {
        return "Log2.html";
    }

    @GetMapping("/audio/Log3.html")
    public String logai2Page(ModelMap modelMap) {
        return "Log3.html";
    }

    @GetMapping("/audio/Log4.html")
    public String logdu2Page(ModelMap modelMap) {
        return "Log4.html";
    }

//    @GetMapping("/chat/log")
//    public @ResponseBody String logTemp(String token, @RequestParam(required = false) Integer row,
//                                        @RequestParam(required = false) String count,
//                                        @RequestParam(required = false) String type) {
//        if (StringUtils.isBlank(token)) return "超时重新登录";
//        LoginData data;
//        if ((data = loadState.get(token)) == null) return "超时重新登录";
//        long current = System.currentTimeMillis();
//        if (current-data.getTime() > 7200000) {
//            loadState.remove(token);
//            return "超时重新登录";
//        }
//        if (row == null || row == 0) row = 1000;
//        String s = chatLogs.look(row, count, type);
//        if (s == null) s = "出错，请刷新重试";
//        return s;
//    }

    @GetMapping("/chat/log")
    public @ResponseBody String logTemp(String token, @RequestParam(required = false) Integer row,
                                        @RequestParam(required = false) String count,
                                        @RequestParam(required = false) String type) {
        if (StringUtils.isBlank(token)) return "超时重新登录";
        LoginData data;
        if ((data = loadState.get(token)) == null) return "超时重新登录";
        long current = System.currentTimeMillis();
        if (current-data.getTime() > 7200000) {
            loadState.remove(token);
            return "超时重新登录";
        }
        if (row == null || row == 0) row = 1000;
        String s = chatLogs.look(row, count, type);
        if (s == null) s = "出错，请刷新重试";
        return s;
    }

    @GetMapping("/chat/choose")
    public String logTemp(String token, ModelMap modelMap) {
        if (StringUtils.isBlank(token)) return "LoadFailed.html";
        LoginData data;
        if ((data = loadState.get(token)) == null) return "LoadFailed.html";
        long current = System.currentTimeMillis();
        if (current-data.getTime() > 7200000) {
            loadState.remove(token);
            return "LoadFailed.html";
        }
        modelMap.put("token", token);
        return "ChatLogChoose.html";
    }

    @GetMapping("/chat/login/check")
    public @ResponseBody String lookLogSignIn(String userName, String passwd) {
        log.info(userName+":"+passwd);
        long current = System.currentTimeMillis();
        loadState.entrySet().removeIf(entry -> current-entry.getValue().getTime() > 7200000);
        if ("qiwu".equals(userName) && "qiwuai2020".equals(passwd)) {
            LoginData data;
            for (Map.Entry<String, LoginData> entry: loadState.entrySet()) {
                if (userName.equals(entry.getValue().getUsername())) {
                    entry.getValue().setTime(current);
                    return entry.getKey();
                }
            }
            String token = RandomStringUtils.randomAlphabetic(8);
            data = new LoginData();
            data.setUsername(userName);
            data.setToken(token);
            data.setTime(current);
            loadState.put(token, data);
            return token;
        }
        return "0";
    }

    @GetMapping("/chat/login")
    public String login(ModelMap modelMap) {
        return "LookLogLogin.html";
    }

    // eae846c7d1ca51474ebc7230ace47bf2.txt
    @GetMapping("/aligenie/{name}")
    public String spirit(@PathVariable String name, ModelMap modelMap) {
        return name;
    }
}
