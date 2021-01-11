package xiaoduhome.outside;

import ai.qiwu.com.xiaoduhome.common.Config;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.outside.model.AudioInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * painter
 * 20-7-2 上午10:46
 */
@Component
@Slf4j
public class OutSideCache implements InitializingBean {
    static volatile Map<Long, AudioInfo> cacheAudioInfo = new HashMap<>();

    private static final String AUDIO_INFO_INTERFACE;

    public static final String SINGLE_BOT_INTERFACE;

    static final boolean DOES_DEV_ENV;

    static {
        DOES_DEV_ENV = "dev".equals(Config.getProperty("programEnv"));
        SINGLE_BOT_INTERFACE = Config.getProperty("singleBotInterface");
        AUDIO_INFO_INTERFACE = Config.getProperty("audioInfoInterface");
        log.info("AUDIO_INFO_INTERFACE:"+AUDIO_INFO_INTERFACE);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        initFillCacheData();
    }

    private void initFillCacheData() {
        Map<Long, AudioInfo> map = null;
        int count = 1;
        do {
            try {
                String response = OkHttp3Utils.doGet(AUDIO_INFO_INTERFACE, null, null);
                List<AudioInfo> infos = JSON.parseObject(response, new TypeReference<List<AudioInfo>>(){});
                if (!CollectionUtils.isEmpty(infos)) {
                    map = new HashMap<>();
                    for (AudioInfo info: infos) {
                        map.put(info.getInfoId(), info);
                    }
                    cacheAudioInfo = map;
                } else {
                    log.warn("第{}次初始化获取音箱第三方信息,返回为空",count);
                }
            } catch (Exception e) {
                log.error("第{}次初始化获取音箱第三方信息失败:{}",count,e);
            }
            count++;
        } while (map != null && count <= 3);
    }

    static AudioInfo fillTheParticularBotInfo(Long infoId) {
        Map<String, String> params = new HashMap<>(1);
        params.put("infoId", String.valueOf(infoId));
        try {
            String response = OkHttp3Utils.doGet(AUDIO_INFO_INTERFACE, params, null);
            List<AudioInfo> infos = JSON.parseObject(response, new TypeReference<List<AudioInfo>>(){});
            if (!CollectionUtils.isEmpty(infos)) {
                AudioInfo info = infos.get(0);
                cacheAudioInfo.put(info.getInfoId(), info);
                return info;
            } else {
                log.warn("获取音箱第三方信息返回为空:infoId:{}",infoId);
            }
        } catch (Exception e) {
            log.error("获取音箱第三方信息失败:infoId:{},{}",infoId,e);
        }
        return null;
    }
}
