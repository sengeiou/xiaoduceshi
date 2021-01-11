package xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.Config;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.pojo.UserBehaviorData;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectDataResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.common.Constants.DPL_COMPONENT_ID.DEFAULT_ASIDE_IMG_URL;
import static ai.qiwu.com.xiaoduhome.common.Constants.UNIT_PRICE;

/**
 * @author 苗权威
 * @dateTime 20-1-15 下午8:24
 */
@Slf4j
@Component
public class ScheduleServiceNew implements InitializingBean {
    private static volatile List<ProjectData> allProjects = new ArrayList<>();
    private static volatile Map<String, ProjectData> botAccount2Project = new HashMap<>();
    private static volatile Map<String, ProjectData> name2Project = new HashMap<>();
    private static volatile Map<String, Map<String, Long>> channel2BotAccountList = new HashMap<>();
    private static volatile Map<String, Map<String,String>> ASIDE_IMG = new HashMap<>();

    public static volatile List<String> serverAddresses = new ArrayList<>();

    public static final String FLOWER_UNIT_PRICE = Config.getProperty("unitPrice", UNIT_PRICE);

//    static volatile Long curTime = System.currentTimeMillis();

    public static final List<String> CATEGORY = Arrays.asList("古风", "言情", "灾难", "冒险", "武侠", "穿越", "答题", "经营");
    public static final String[] CATEGORY_CHARAC={"gufeng","yanqing","zainan","maoxian","wuxia", "chuanyue", "dati", "jingying"};

    public static volatile Map<String, List<ProjectData>> category2Bots = new HashMap<>();

    public static volatile Map<String, List<String>> jiaoyouChannel2Bots = new HashMap<>();

    private static volatile Map<String, String> channelId2RecommendBotAcc = new HashMap<>();

    public static volatile String XIAO_AI_ENCRY_HOST;

    public static volatile boolean SERVER_CHANGE = false;

    public static boolean THE_PROCESS_IS_MASTER = false;

    ////////////////////
    public static volatile String BASE_HOST_URL;
    public static volatile String OLD_BASE_HOST_URL;
    public static volatile String BASE_AUDIO_URL;
    public static volatile String OLD_BASE_AUDIO_URL;
    public static volatile String CDN_HOST_URL;
    public static volatile String OLD_CDN_HOST_URL;

    private static volatile String HOST_UTL;
    private static volatile String CENTRAL_HOST;

    public static final ObjectMapper objectMapper;

    public static final Set<String> LOAD_AUTH_CALLBACK_URL;

    static {
        HOST_UTL = Config.getProperty("requestHost");
        CENTRAL_HOST = Config.getProperty("centralHost");

        LOAD_AUTH_CALLBACK_URL = new HashSet<>();
        LOAD_AUTH_CALLBACK_URL.add("https://xiaodu.baidu.com/saiya/auth/3a0610f8af67a3c92d803a80a1c8976c");
        LOAD_AUTH_CALLBACK_URL.add("https://xiaodu-dbp.baidu.com/saiya/auth/3a0610f8af67a3c92d803a80a1c8976c");
        LOAD_AUTH_CALLBACK_URL.add("https://xiaodu.baidu.com/saiya/auth/4fbc7b9112e3a43dc7dbf43fd556929e");
        LOAD_AUTH_CALLBACK_URL.add("https://xiaodu-dbp.baidu.com/saiya/auth/4fbc7b9112e3a43dc7dbf43fd556929e");

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getAllProjects();
        getOtherServerAddress();
        getJiaoYouChannelRecordBots();

        BASE_HOST_URL = Config.getProperty("baseHostUrl");
        BASE_AUDIO_URL = Config.getProperty("baseAudioUrl");
        CDN_HOST_URL = Config.getProperty("cdnHostUrl");
        OLD_BASE_HOST_URL = BASE_HOST_URL;
        OLD_BASE_AUDIO_URL = BASE_AUDIO_URL;
        OLD_CDN_HOST_URL = CDN_HOST_URL;

        XIAO_AI_ENCRY_HOST = Config.getProperty("host");
        THE_PROCESS_IS_MASTER = "master".equals(Config.getProperty("masterSlave", "slave"));

        log.info("BASE_HOST_URL:{},CDN_HOST_URL:{}, xiaoai encry host:{}, masterSlave:{}",BASE_HOST_URL,CDN_HOST_URL,
                XIAO_AI_ENCRY_HOST,THE_PROCESS_IS_MASTER);
        // buildLunchNewDocument
    }

    private void getAllProjects() {
        new Thread(() -> {
            try {
                String response = OkHttp3Utils.doGet(HOST_UTL+"audio_box/project");
                if (StringUtils.isNotBlank(response)) {
                    ProjectDataResult result = JSON.parseObject(response, ProjectDataResult.class);
                    if (new Integer(1).equals(result.getCode())) {
                        List<ProjectData> list = result.getData().getBots();
                        Map<String, Map<String, Long>> map = result.getData().getChannelAcc();
                        if (!CollectionUtils.isEmpty(list)) {
                            final Map<String, ProjectData> botAcc = new HashMap<>();
                            final Map<String, ProjectData> name = new HashMap<>();
                            list.forEach(projectData -> {
                                botAcc.put(projectData.getBotAccount(), projectData);
                                name.put(projectData.getName(), projectData);
                                List<ProjectData> bots;
                                for (String s1 : CATEGORY) {
                                    if (projectData.getLabels().contains(s1)) {
                                        bots = category2Bots.computeIfAbsent(s1, s -> new ArrayList<>());
                                        bots.add(projectData);
                                    }
                                }
                            });
                            botAccount2Project = botAcc;
                            name2Project = name;
                            allProjects = list;
                        }
                        if (!CollectionUtils.isEmpty(map)) {
                            channel2BotAccountList = map;
                        }
                        if (!CollectionUtils.isEmpty(result.getData().getHeadImg())) {
                            ASIDE_IMG = result.getData().getHeadImg();
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("ScheduleServiceNew#getAllProjects获取所有作品信息失败:"+e);
            }

            String response = null;
            int count = 1;
            Map<String, String> map = new HashMap<>();
            do {
                try {
                    response = OkHttp3Utils.doGet(CENTRAL_HOST+"/api/sdk/botConfig/all");
                    JSONArray array = JSONObject.parseArray(response);
                    if (array != null) {
                        for (Object o: array) {
                            JSONObject object = (JSONObject) o;
                            if (Boolean.FALSE.equals(object.getBoolean("auth"))) {
                                map.put(object.getString("appChannelId"), object.getString("recommendBotAccount"));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("getAllChannelRecommendBotAccount:{}",e);
                }
                if (StringUtils.isBlank(response)) {
                    try {
                        Thread.sleep(count*5);
                    } catch (Exception e) {}
                    log.warn("第"+count+"次获取channelId失败");
                }
                count++;
            } while (StringUtils.isBlank(response) && count < 5);
            if (map.size() != 0) {
                channelId2RecommendBotAcc = map;
            }
            log.info("channelId2RecommendBotAcc:"+channelId2RecommendBotAcc);
            log.info("botAccount2Project:"+botAccount2Project.size());
            log.info("name2Project:"+name2Project.size());
            log.info("allProjects:"+allProjects.size());
            log.info("channel2BotAccountList:"+channel2BotAccountList.size());
            log.info("ASIDE_IMG:"+ASIDE_IMG.size());
        }).start();
    }

//    @Scheduled(cron = "0 */2 * * * ?")
//    public void updateTheCurTime() {
////        log.info("updateTheCurTime start");
//        curTime = System.currentTimeMillis();
//    }

    private void getOtherServerAddress() {
        String servers = Config.getProperty("serverAddress");
        if (StringUtils.isNotBlank(servers)) {
            serverAddresses  = Arrays.asList(servers.split(","));
            log.info("serverAddress:"+serverAddresses);
        }
    }

//    @Scheduled(initialDelay = 3600000, fixedRate = 3600000)
//    public void clearUserBehaviorData() {
//        log.info("clearUserBehaviorData start");
//        List<String> needClear = new ArrayList<>();
//        for (String userId: DplbotServiceUtil.USER_HISTORY.keySet()) {
//            UserBehaviorData data = DplbotServiceUtil.USER_HISTORY.get(userId);
//            if (data != null) {
//                if (data.getMark() == 1) data.setMark(0);
//                else {
//                    needClear.add(userId);
//                }
//            }
//        }
//        for (String userId: needClear) {
//            log.info("clearUserBehaviorData:"+needClear);
//            DplbotServiceUtil.USER_HISTORY.remove(userId);
//        }
//    }

    @Scheduled(cron = "0 0 11,16,22 * * ?")
    public void getJiaoYouChannelRecordBots() {
        DplbotServiceUtil.POOL.submit(() -> {
            log.info("getJiaoYouChannelRecordBots start");
            String response;
            JSONObject jsonObject;
            int count = 1;
            boolean success = false;
            do {
                try {
                    response = OkHttp3Utils.doGet(HOST_UTL+"api/data/audio/list/jiaoyou");
                    jsonObject = JSON.parseObject(response).getJSONObject("data");
                    Map<String, List<String>> map = jsonObject.toJavaObject(Map.class);
                    if (map != null && map.size() != 0) {
                        List<String> jiaoyouList = map.get("jiaoyou-audio-adult-test");
                        if (!CollectionUtils.isEmpty(jiaoyouList)) {
                            jiaoyouList.add("遇见伽利略");
                            jiaoyouList.add("魔法世界大冒险");
                        }
                        List<String> xiaoaiJiaoyouList = map.get("xiaoai-jiaoyou-audio-adult-test");
                        if (!CollectionUtils.isEmpty(xiaoaiJiaoyouList)) {
                            xiaoaiJiaoyouList.add("遇见伽利略");
                            xiaoaiJiaoyouList.add("魔法世界大冒险");
                        }
                        jiaoyouChannel2Bots = map;
                        success = true;
                    } else {
                        log.info("getChannelAllBots failed count="+count);
                        Thread.sleep(count*5);
                    }
                } catch (Exception e) {
                    log.warn("getChannelAllBots: "+e);
                }
                count++;
            } while (!success && count < 5);
            log.info("getChannelAllBots:"+jiaoyouChannel2Bots);
        });
    }

    public static String getWorkNameByBotAccount(String botAccount) {
        if (StringUtils.isNotBlank(botAccount)) {
            ProjectData data = botAccount2Project.get(botAccount);
            if (data != null) {
                if (StringUtils.isNotBlank(data.getShowedWorkName())) {
                    return data.getShowedWorkName();
                }
                return data.getName();
            }
        }
        return null;
    }

    public static String getBotAccountByWorkname(String workName) {
        if (StringUtils.isNotBlank(workName)) {
            ProjectData data = name2Project.get(workName);
            if (data != null) {
                return data.getBotAccount();
            }
        }
        return null;
    }

    public static ProjectData getProjectByName(String name) {
        return name2Project.get(name);
    }

    public static ProjectData getProjectByBotAccount(String botAccount) {
        ProjectData data = botAccount2Project.get(botAccount);
        if (StringUtils.isNotBlank(data.getShowedWorkName())) {
            return name2Project.get(data.getShowedWorkName());
        }
        return data;
    }

    public static List<ProjectData> getTheChannelAllProjectData(String channel) {
        return channel2BotAccountList.get(channel).keySet().stream().map(s -> botAccount2Project.get(s)).collect(Collectors.toList());
    }

    public static List<ProjectData> getTheChannelProjectsByWatch(String channel) {
        TreeMap<Long, String> order = new TreeMap<>(Comparator.reverseOrder());
        channel2BotAccountList.get(channel).forEach((key, value) -> order.put(value, key));
        List<ProjectData> datas = new ArrayList<>();
        for (String botAcc: order.values()) {
            datas.add(botAccount2Project.get(botAcc));
        }
        return datas;
    }

    public static String getAsideImgUrl(String botId, String name) {
        String url;
        Map<String,String> imgs = ASIDE_IMG.get(botId);
        if (imgs == null) {
            url = DEFAULT_ASIDE_IMG_URL;
        } else {
            url = imgs.get(name);
        }
        if (url == null) url = DEFAULT_ASIDE_IMG_URL;
        return url;
    }

    public static String getTheChannelBotAccount(String channel) {
        return channelId2RecommendBotAcc.get(channel);
//        return null;
    }

    public static void main(String[] args) throws IOException {
        String res = OkHttp3Utils.doGet("http://hw-gz19.heyqiwu.cn:18082/api/data/audio/list/jiaoyou");
        System.out.println(res);
    }
}
