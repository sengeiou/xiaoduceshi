package ai.qiwu.com.xiaoduhome.controller;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.pojo.NoticeReceiveData;
import ai.qiwu.com.xiaoduhome.pojo.data.BaseHostData;
import ai.qiwu.com.xiaoduhome.service.DplBotService;
import ai.qiwu.com.xiaoduhome.service.DplBotServiceBoth;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @author 苗权威
 * @dateTime 19-7-26 下午9:27
 */
@RestController
@RequestMapping("/xiaoduhomeceshi")
@Slf4j
public class XiaoDuHomeController {

    public static volatile String WELCOME_NOVEL = "欢迎来到晓悟智能小说。向您推荐新上线的妖刀异闻录，您可以说打开妖刀异闻录。或者说，推荐其它作品。";

    public static volatile String WELCOME_SMART = "欢迎来到晓悟智能故事。我们新上线了魔法世界大冒险，你可以说打开魔法世界大冒险。或者说，推荐其它作品。";

    public static volatile String WELCOME_FORMAT = "欢迎来到交游天下。与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的%s，您可以说打开%s。或者说，推荐其它作品。";
//    public static volatile String WELCOME_FORMAT = "欢迎来到交游天下。与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的野蛮女友，您可以说打开野蛮女友。或者说，推荐其它作品。";

    private int test = 0;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public XiaoDuHomeController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RequestMapping(value = "/chat", method = RequestMethod.HEAD)
    public void chatHead(HttpServletResponse response) {
//        if (test++ < 5) {
//            try {
//                Enumeration enumeration = request.getHeaderNames();
//                while (enumeration.hasMoreElements()) {
//                    String name = (String) enumeration.nextElement();
//                    log.info(name+":"+request.getHeader(name));
//                }
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
//                StringBuilder builder = new StringBuilder();
//                String temp;
//                while((temp = bufferedReader.readLine()) != null) {
//                    builder.append(temp);
//                }
//                log.info("message:"+builder.toString());
//            } catch (Exception e) {
//                log.warn("探活 failed:"+e);
//            }
//        }
        response.setStatus(200);
    }

    @PostMapping("/chat") //交游天下
    public String chat() {
        HttpServletRequest request = null;
        try {
            //log.info("--------------分界线-------------------");
            DplBotServiceBoth service = new DplBotServiceBoth((request=Utils.getHttpServletRequest()), 5, stringRedisTemplate);
//            Dpl2Service service = new Dpl2Service((request=Utils.getHttpServletRequest()), stringRedisTemplate);
//            log.info("请求信息中的attributes:{}", service.getRequest().getSession().getAttributes());
//            log.info(service.getStrRequest());
            //service.disableVerify();
            service.enableVerify();
//            String str = service.run();
//            log.info("响应信息:{}", str);
//            return str;
            return service.run();
        } catch (MismatchedInputException ex) {
            log.info("交游探活:"+ ExceptionUtils.getStackTrace(ex));
            if (test++ < 5) {
                try {
                    Enumeration enumeration = request.getHeaderNames();
                    while (enumeration.hasMoreElements()) {
                        String name = (String) enumeration.nextElement();
                        log.info(name+":"+request.getHeader(name));
                    }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder builder = new StringBuilder();
                    String temp;
                    while((temp = bufferedReader.readLine()) != null) {
                        builder.append(temp);
                    }
                    log.info("message:"+builder.toString());
                } catch (Exception e) {
                    log.warn("探活 failed:"+e);
                }
            }
        } catch (Exception e) {
            log.error("交游:"+e);
            //log.error("出现错误 "+e.toString());
        }
        return null;
    }

    @RequestMapping(value = "/smart", method = RequestMethod.HEAD)
    public void smartHead(HttpServletResponse response) {
        response.setStatus(200);
    }

    @PostMapping("/smart") // 智能故事
    public String smart() {
        try {
            //log.info("--------------分界线-------------------");
//            DplBotService service = new DplBotService(Utils.getHttpServletRequest(), 1);
            DplBotServiceBoth service = new DplBotServiceBoth(Utils.getHttpServletRequest(), 1, stringRedisTemplate);
//            log.info("请求信息中的attributes:{}", service.getRequest().getSession().getAttributes());
//            log.info(service.getStrRequest());
            //service.disableVerify();
            service.enableVerify();
//            String str = service.run();
//            log.info("响应信息:{}", str);
//            return str;
            return service.run();
        } catch (MismatchedInputException ex) {
        } catch (Exception e) {
            log.error("smart:"+e);
            //log.error("出现错误 "+e.toString());
        }
        return null;
    }

    @RequestMapping(value = "/novel", method = RequestMethod.HEAD)
    public void novelHead(HttpServletResponse response) {
        response.setStatus(200);
    }

    @PostMapping("/novel") // 智能小说
    public String novel() {
        try {
//            log.info("--------------分界线-------------------");
            DplBotServiceBoth service = new DplBotServiceBoth(Utils.getHttpServletRequest(), 2, stringRedisTemplate);
//            Dpl2Service service = new Dpl2Service(Utils.getHttpServletRequest(), stringRedisTemplate);
//            log.info("请求信息中的attributes:{}", service.getRequest().getSession().getAttributes());
//            log.info(service.getStrRequest());
            //service.disableVerify();
            service.enableVerify();
            //            log.info("响应信息:{}", str);
            return service.run();
        } catch (MismatchedInputException ex) {
        } catch (Exception e) {
            log.error("novel:"+e);
            //log.error("出现错误 "+e.toString());
        }
        return null;
    }

    @PostMapping("/change/welcome/word")
    public void changeWelcomeWord(String pwd, String type, String word) {
        if (StringUtils.startsWith(pwd, "mqw") && pwd.length() == 8) {
            switch (type) {
                case "adult" : WELCOME_NOVEL = word;
                case "child" : WELCOME_SMART = word;
                case "all"   : WELCOME_FORMAT = word;
            }
        }
        log.warn("/change/welcome/word:adult:{},child:{},all:{}", WELCOME_NOVEL, WELCOME_SMART, WELCOME_FORMAT);
    }

    @PostMapping("/change/host")
    public void changeBaseHost(@RequestBody BaseHostData data) {
        if (StringUtils.startsWith(data.getSecretKey(), "mqw")
                && StringUtils.endsWith(data.getSecretKey(),"#") && data.getSecretKey().length() == 8) {
            if (ScheduleServiceNew.THE_PROCESS_IS_MASTER) {
                for (String slaveProcessUrl : ScheduleServiceNew.serverAddresses) {
                    OkHttp3Utils.serverChangeCallOtherSalve(slaveProcessUrl+"xiaoduhome/change/host", JSON.toJSONString(data));
                }
            }
            ScheduleServiceNew.OLD_BASE_HOST_URL = ScheduleServiceNew.BASE_HOST_URL;
            ScheduleServiceNew.OLD_BASE_AUDIO_URL = ScheduleServiceNew.BASE_AUDIO_URL;
            ScheduleServiceNew.OLD_CDN_HOST_URL = ScheduleServiceNew.CDN_HOST_URL;
            ScheduleServiceNew.BASE_HOST_URL = data.getHost();
            ScheduleServiceNew.BASE_AUDIO_URL = data.getAudio();
            ScheduleServiceNew.CDN_HOST_URL = data.getCdn();

            stringRedisTemplate.opsForValue().setIfAbsent(Constants.SERVER_CHANGE_KEY, "1", 4, TimeUnit.MINUTES);
            ScheduleServiceNew.SERVER_CHANGE = true;
        }
        log.info("BASE_HOST_URL:"+ScheduleServiceNew.BASE_HOST_URL);
        log.info("BASE_AUDIO_URL:"+ScheduleServiceNew.BASE_AUDIO_URL);
        log.info("CDN_HOST_URL:"+ScheduleServiceNew.CDN_HOST_URL);

        log.info("OLD_BASE_HOST_URL:"+ScheduleServiceNew.OLD_BASE_HOST_URL);
        log.info("OLD_BASE_AUDIO_URL:"+ScheduleServiceNew.OLD_BASE_AUDIO_URL);
        log.info("OLD_CDN_HOST_URL:"+ScheduleServiceNew.OLD_CDN_HOST_URL);
    }

    @PostMapping("/change/host/getInOldServerNum")
    public String getOldServerUserNum(@RequestBody BaseHostData data) {
        if (StringUtils.startsWith(data.getSecretKey(), "mqw")
                && StringUtils.endsWith(data.getSecretKey(),"#") && data.getSecretKey().length() == 8) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.SERVER_CHANGE_KEY))) {
                Cursor<byte[]> scan = stringRedisTemplate.getConnectionFactory().getConnection()
                        .scan(ScanOptions.scanOptions().match(Constants.CAN_NOT_MOVE_USER_KEY+"*").build());
                int staynUM = 0;
                while (scan.hasNext()) {
                    scan.next();
                    staynUM++;
                }
                String s = new StringBuilder("正在切换服务器，剩余时间(s):")
                        .append(stringRedisTemplate.getExpire(Constants.SERVER_CHANGE_KEY)).append(",停留在旧服务器人数：")
                        .append(staynUM).append(",新BASE_HOST地址:").append(ScheduleServiceNew.BASE_HOST_URL)
                        .append(",旧BASE_HOST地址:").append(ScheduleServiceNew.OLD_BASE_HOST_URL)
                        .append(",标志为值:").append(ScheduleServiceNew.SERVER_CHANGE).toString();

                log.info(s);
                return s;
            }
            Cursor<byte[]> scan = stringRedisTemplate.getConnectionFactory().getConnection()
                    .scan(ScanOptions.scanOptions().match(Constants.CAN_NOT_MOVE_USER_KEY+"*").build());
            if (!scan.hasNext()) {
                ScheduleServiceNew.SERVER_CHANGE = false;
                if (ScheduleServiceNew.THE_PROCESS_IS_MASTER) {
                    for (String slaveProcessUrl : ScheduleServiceNew.serverAddresses) {
                        OkHttp3Utils.serverChangeCallOtherSalve(slaveProcessUrl+"xiaoduhome/change/host/getInOldServerNum", JSON.toJSONString(data));
                    }
                }
                log.info("There is no one in old server,标志为值:"+ScheduleServiceNew.SERVER_CHANGE+",新BASE_HOST地址:"+ScheduleServiceNew.BASE_HOST_URL);
                return "There is no one in old server";
            } else {
                int staynUM = 0;
                while (scan.hasNext()) {
                    scan.next();
                    staynUM++;
                }
                log.info(staynUM+" users in oldServer,标志为值:"+ScheduleServiceNew.SERVER_CHANGE+",新BASE_HOST地址:"+ScheduleServiceNew.BASE_HOST_URL);
                return staynUM+" users in oldServer";
            }
        }
        return "secret wrong";
    }

    @GetMapping("/check/url/info")
    public void checkUrlInfo(@RequestParam String psd) {
        if (StringUtils.startsWith(psd, "mqw")
                && StringUtils.endsWith(psd,"#") && psd.length() == 8) {
            log.info("BASE_HOST_URL:"+ScheduleServiceNew.BASE_HOST_URL);
            log.info("BASE_AUDIO_URL:"+ScheduleServiceNew.BASE_AUDIO_URL);
            log.info("CDN_HOST_URL:"+ScheduleServiceNew.CDN_HOST_URL);
        }
    }

    @PostMapping("/notice")
    public void receiveOtherServerNotice(@RequestBody NoticeReceiveData data) {
        DplbotServiceUtil.receiveNotice(data);
    }

    @PostMapping("/story") //交互故事
    public String story(HttpServletRequest request) {
        try {
            //log.info("--------------分界线-------------------");
//            DplBotService service = new DplBotService(Utils.getHttpServletRequest(), 5);
            log.info("==================================");
            DplBotServiceBoth service = new DplBotServiceBoth(request, 5, stringRedisTemplate);
//            log.info(service.getStrRequest());
//            log.info("请求信息中的attributes:{}", service.getRequest().getSession().getAttributes());
            //service.disableVerify();
            service.enableVerify();
            String str = service.run();
//            log.info("响应信息:{}", str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            //log.error("出现错误 "+e.toString());
        }
        return null;
    }

    //    @PostMapping("/literature") // 互动文艺
    public String literature() {
        try {
            //log.info("--------------分界线-------------------");
            DplBotService service = new DplBotService(Utils.getHttpServletRequest(),4);
//            log.info("请求信息中的attributes:{}", service.getRequest().getSession().getAttributes());
//            log.info(service.getStrRequest());
            //service.disableVerify();
            service.enableVerify();
            String str = service.run();
//            log.info("响应信息:{}", str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            //log.error("出现错误 "+e.toString());
        }
        return null;
    }

//    @PostMapping("/up")
    public void loadImage(MultipartFile file, String path, String name, String secret) {
        if (!"mqw123.".equals(secret)) return;
        File imageFile = new File(path+name);
        try {
            if (!imageFile.getParentFile().exists()) {
                imageFile.mkdirs();
            }
            file.transferTo(imageFile.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @GetMapping("/historyCache")
    public void getUserHistoryData() {
        DplbotServiceUtil.printCache();
    }

    public static void main(String[] args) {
        String welcomeWord = WELCOME_NOVEL;
        try {
            welcomeWord = String.format(welcomeWord, "cecece","cecece");
        } catch (Exception e) {
            welcomeWord = "fail";
        }
        System.out.println(welcomeWord);
    }
}
