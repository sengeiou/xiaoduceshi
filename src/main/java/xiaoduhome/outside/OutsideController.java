package xiaoduhome.outside;

import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.outside.model.AudioInfo;
import ai.qiwu.com.xiaoduhome.service.DplBotServiceBoth;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import ai.qiwu.com.xiaoduhome.xiaoai.model.request.XiaoAiRequest;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.XiaoAiResponse;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.Response;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ai.qiwu.com.xiaoduhome.outside.OutSideCache.DOES_DEV_ENV;
import static ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew.XIAO_AI_ENCRY_HOST;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;

/**
 * painter
 * 20-6-28 下午3:56
 */
@RestController
@RequestMapping("/outside")
@Slf4j
public class OutsideController {

    private final OutsideXiaoAiService outsideXiaoAiService;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("#{'${colony.servers}'.split(',')}")
    private List<String> servers;

    @Autowired
    public OutsideController(OutsideXiaoAiService outsideXiaoAiService, StringRedisTemplate stringRedisTemplate) {
        this.outsideXiaoAiService = outsideXiaoAiService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/data/cache")
    public void cacheAudioInfoChange(@RequestBody AudioInfo audioInfo) {
        if (audioInfo.getInfoId() != null) {
            OutSideCache.cacheAudioInfo.put(audioInfo.getInfoId(), audioInfo);
            if (!CollectionUtils.isEmpty(servers)) {
                for (String server: servers) {
                    OkHttp3Utils.postAsync(server+"outside/notify/data/cache", JSON.toJSONString(audioInfo),
                            "异步通知对外开放作品数据变更信息,"+server);
                }
            }
        }
    }

    @PostMapping("/notify/data/cache")
    public void notifyOtherServer(@RequestBody AudioInfo audioInfo) {
        if (audioInfo.getInfoId() != null) {
            OutSideCache.cacheAudioInfo.put(audioInfo.getInfoId(), audioInfo);
        }
    }

    @PostMapping("/xiaoai/{id}")
    public XiaoAiResponse xiaoaiOutSide(@PathVariable Long id, @RequestBody XiaoAiRequest request, HttpServletRequest servletRequest) {
        log.info("--------------分界线-------------------");
        AudioInfo info = OutSideCache.cacheAudioInfo.get(id);
        if (info == null) info = OutSideCache.fillTheParticularBotInfo(id);
        if (info == null) return buildTextResponse("没有找到该技能的信息，请在创作平台更新技能信息");
        try {
            if (!encry(info.getXiaoaiSecret(), info.getXiaoaiKeyId(), servletRequest.getRequestURI(), servletRequest)) {
                log.error("验证没通过:"+id);
                return null;
            }
            return outsideXiaoAiService.getResponse(request, info, DOES_DEV_ENV);
        } catch (Exception e) {
            log.error("xiaoaiOutSide: "+e);
        }
        return buildTextResponse(SORRY_REPEAT);
    }

    @RequestMapping(value = "/novel", method = RequestMethod.HEAD)
    public void novelHead(HttpServletResponse response) {
        response.setStatus(200);
    }

    @PostMapping("/xiaodu/{id}")
    public String xiaoduOutSide(@PathVariable Long id, HttpServletRequest request) {
        try {
            log.info("--------------分界线-------------------");
            AudioInfo info = OutSideCache.cacheAudioInfo.get(id);
            if (info == null) info = OutSideCache.fillTheParticularBotInfo(id);
            if (info == null) {
                log.warn("没有找到该技能的信息:"+id);
                return null;
            }
            OutSideXiaoDuService service = new OutSideXiaoDuService(request, stringRedisTemplate, info);
            service.enableVerify();
            return service.run();
        } catch (Exception e) {
            log.error("xiaoduOutSide:"+e);
        }
        return null;
    }

    private XiaoAiResponse buildResponse() {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(false);

        Response responseInfo = new Response();
        // 这个字段很重要，他会在播放音频或是tts结束后，开麦收音，因为默认麦是关闭的，
        // 若不主动打开则小爱在一次会话后退出技能。
        responseInfo.setOpen_mic(true);
        response.setResponse(responseInfo);
        return response;
    }

    private XiaoAiResponse buildTextResponse(String text) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);

        return response;
    }

    private static boolean encry(String secret, String keyId, String urlPath, HttpServletRequest request) {
        String host = XIAO_AI_ENCRY_HOST;
        String xiaomiDate = request.getHeader("x-xiaomi-date");
        String md5 = request.getHeader("content-md5");
        if (StringUtils.isBlank(md5)) md5 = "";
        String contentType = request.getHeader("content-type");
        String authorization = request.getHeader("authorization");
        if (StringUtils.isAnyBlank(xiaomiDate, contentType, authorization)) return false;

        String algorithmForMac = "HmacSHA256";

        String method = "POST";
        String param = "";
        String source = new StringBuilder().append(method).append('\n').append(urlPath).append('\n').append(param).append('\n')
                .append(xiaomiDate).append('\n').append(host).append('\n')
                .append(contentType).append('\n').append(md5).append('\n').toString();
        try {
            Mac mac = Mac.getInstance(algorithmForMac);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(secret), algorithmForMac);
            mac.init(secretKeySpec);
            String signature = new StringBuilder("MIAI-HmacSHA256-V1 ").append(keyId).append("::")
                    .append(Hex.encodeHexString(mac.doFinal(source.getBytes(StandardCharsets.UTF_8)))).toString();
            return signature.equals(authorization);
        } catch (Exception e) {
            log.error("outside encry:"+e);
        }
        return false;
    }
}
