package ai.qiwu.com.xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.BaseHolder;
import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.common.temple.CollectHistoryDocument;
import ai.qiwu.com.xiaoduhome.common.temple.CollectHistoryUtils;
import ai.qiwu.com.xiaoduhome.entity.primary.AppFavoriteTB;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuCollectTB;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuHistoryBotIdTime;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuHistoryTB;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderTB;
import ai.qiwu.com.xiaoduhome.pojo.*;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.pageData.HistoryData;
import ai.qiwu.com.xiaoduhome.repository.primary.AppFavoriteTBRepository;
import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuCollectRepository;
import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuHistoryTBRepository;
import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuOrderTbRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.pay.event.ChargeEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.pay.event.Payload;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.pay.Charge;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.common.Constants.CO_HI_PAGE_SIZE;
import static ai.qiwu.com.xiaoduhome.common.Constants.XIAOWU_API.BEARER;
import static ai.qiwu.com.xiaoduhome.common.Constants.XIAOWU_API.USER_ID_BY_TOKEN;

/**
 * @author 苗权威
 * @dateTime 19-9-23 上午10:26
 */
@Slf4j
public class DplbotServiceUtil {
    private static RedisTemplate<String, UserBehaviorData> redisTemplate;
    private static final int CORE = Runtime.getRuntime().availableProcessors();

    public static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(CORE+1, 1500,
            3000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CORE), new ThreadPoolExecutor.CallerRunsPolicy());

    static final ConcurrentHashMap<String, UserBehaviorData> USER_HISTORY = new ConcurrentHashMap<>();

    private static void getUserFavorite(String userId, String usUserId) {
        AppFavoriteTBRepository favoriteTBRepository  = BaseHolder.getBean("appFavoriteTBRepository");
        try {
            UserBehaviorData data = USER_HISTORY.computeIfAbsent(userId, s -> new UserBehaviorData());
            List<AppFavoriteTB> favoriteBots = favoriteTBRepository.getByUserId(usUserId);
            for(AppFavoriteTB bot : favoriteBots) {
                data.getUserBotId2Collected().put(bot.getBotId(), bot.getCreateTime().getTime());
            }
            log.info("UserBotId2Collected:"+data.getUserBotId2Collected());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RedisTemplate<String, UserBehaviorData> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = BaseHolder.getBean("userBehaviorTemplate");
        }
        return redisTemplate;
    }

    public static String getUsUserId(String userId, String accessToken, String channelId) {
        log.info("getUsUserId: userid:{}",userId);
        UserBehaviorData data = getRedisTemplate().opsForValue().get(userId);
        String jytId;
        if (data == null) {
            log.info("getUsUserId: data is null");
            jytId = getUserIdByToken(accessToken, channelId, userId);
            onLunchFillUserBehaviorData(userId, channelId, jytId, true);
            return jytId;
        } else if (StringUtils.isBlank(data.getJytUserId())) {
            jytId = getUserIdByToken(accessToken, channelId, userId);
            data.setJytUserId(jytId);
            getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);
        }
        return data.getJytUserId();
    }

    public static String getUserIdByToken(String accessToken, String channelId, String userId) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("App-Channel-Id", "xiaodu-speaker-api-test");
            headers.put("Authorization", BEARER + accessToken);
            String str = OkHttp3Utils.doGet(USER_ID_BY_TOKEN, new HashMap<>(), headers);
            log.info("getUserIdByToken:{}",str);
            UserInfoByTokenResult info = JSON.parseObject(str, UserInfoByTokenResult.class);
            if (info.getRetcode() == 0) {
                JYTUserResponse payload = JSON.parseObject(info.getPayload(), JYTUserResponse.class);
                String jytUserId = payload.getInfoId();
                log.info("通过Token获取的用户在app端的id:{}", jytUserId);
//                UserBehaviorData data = USER_HISTORY.computeIfAbsent(userId, s -> new UserBehaviorData());
//                data.setJytUserId(jytUserId);
//                noticeOtherServer(4, jytUserId, null, null, userId, jytUserId, channelId, true);
                return jytUserId;
            }
        } catch (Exception e) {
            log.error("获取郊游天下用户id出错:{}", ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

//    static void onLunchGetUserHistory(final String userId) {
//        POOL.submit(() -> {
//            XiaoDuHistoryTBRepository repository = BaseHolder.getBean("xiaoDuHistoryTBRepository");
//            UserBehaviorData data = USER_HISTORY.computeIfAbsent(userId, s -> new UserBehaviorData());
//            HashMap<String,Long> userBotId2History = data.getUserBotId2History();
//            for (XiaoDuHistoryTB historyTB: repository.getByXiaoduUserId(userId)) {
//                userBotId2History.put(historyTB.getBotId(), historyTB.getLastTime());
//            }
//            //log.info("用户历史记录:{}",USER_HISTORY);
//        });
//    }

    public static void onLunchFillUserBehaviorData(final String userId, final String channelId, final String jytUserId, final Boolean alreadyLoaded) {
        POOL.submit(() -> {
            onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, alreadyLoaded);
        });
    }

    public static UserBehaviorData onLunchFillUserBehaviorDataInMaster(final String userId, final String channelId,
                                                                final String jytUserId, final Boolean alreadyLoaded) {
//        Map<String, String> params = new HashMap<>();
//        params.put("userId", userId);
//        params.put("channelId", channelId);
//        params.put("jytUserId", jytUserId);
//        boolean success = false;
//        if (!alreadyLoaded || StringUtils.isNotBlank(jytUserId)) {
//            try {
//                String response = OkHttp3Utils.doGet(HOST_UTL+"audio_box/behavior", params);
//                if (StringUtils.isNotBlank(response)) {
//                    JSONObject result = JSON.parseObject(response);
//                    if (NumberUtils.INTEGER_ONE.equals(result.getInteger("code"))) {
//                        result = result.getJSONObject("data");
//                        Map<String, Long> botAccount2HistoryTime = JSON.parseObject(result.getJSONObject("history").toJSONString(), new TypeReference<HashMap<String,Long>>(){});
//                        Map<String, Long> botAccount2Flowers = JSON.parseObject(result.getJSONObject("flowers").toJSONString(),  new TypeReference<HashMap<String,Long>>(){});
//                        Map<String, Long> botAccount2FavoriteTime = JSON.parseObject(result.getJSONObject("favorite").toJSONString(),  new TypeReference<HashMap<String,Long>>(){});
//                        UserBehaviorData data = new UserBehaviorData();
//                        if (!CollectionUtils.isEmpty(botAccount2HistoryTime)) data.setUserBotId2History(botAccount2HistoryTime);
//                        if (!CollectionUtils.isEmpty(botAccount2FavoriteTime)) data.setUserBotId2Collected(botAccount2FavoriteTime);
//                        if (!CollectionUtils.isEmpty(botAccount2Flowers)) data.setUserBotId2Flowers(botAccount2Flowers);
//                        data.setJytUserId(jytUserId);
////                        data.setChannel(channelId);
//                        log.info("userId:{},UserBehaviorData:{}",userId,data);
//                        success = true;
////                        USER_HISTORY.put(userId, data);
//                        getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);
////                        log.info("USER_HISTORY:"+USER_HISTORY);
//                        return data;
//                    }
//                }
//            } catch (Exception e) {
//                log.warn("onLunchFillUserBehaviorDataInMaster，获取用户信息失败:"+userId);
//            }
//        } else {
//            log.warn("用户已登陆但jytUserId却为null");
//        }
//        if (!success) {
//            log.warn("未从gameditor处获取到信息,查询数据库信息");
//        }
        XiaoDuHistoryTBRepository historyTBRepository = BaseHolder.getBean("xiaoDuHistoryTBRepository");
        XiaoDuCollectRepository collectRepository = BaseHolder.getBean("xiaoDuCollectRepository");
        XiaoDuOrderTbRepository orderTbRepository = BaseHolder.getBean("xiaoDuOrderTbRepository");
        List<XiaoDuHistoryTB> historyTBS = historyTBRepository.getByXiaoduUserIdAndChannelId(userId, channelId);
        List<XiaoDuCollectTB> collectTBS = collectRepository.getByXiaoduUserIdAndChannelId(userId, channelId);
        List<XiaoDuOrderTB> orderTBS = orderTbRepository.getByXiaoduUserIdAndChannelIdAndStatus(userId, channelId, true);
        UserBehaviorData data = new UserBehaviorData();
        String jytId = jytUserId;
        if (!CollectionUtils.isEmpty(historyTBS)) {
            Map<String, Long> map = new HashMap<>();
            for (XiaoDuHistoryTB xiaoDuHistoryTB: historyTBS) {
                if (StringUtils.isBlank(jytId)) jytId = xiaoDuHistoryTB.getJytUserId();
                String botAccount = ScheduleServiceNew.getBotAccountByWorkname(xiaoDuHistoryTB.getWorkName());
                if (StringUtils.isNotBlank(botAccount)) map.put(botAccount, xiaoDuHistoryTB.getUpdateTime().getTime());
                else log.warn("onLunchFillUserBehaviorDataInMaster: 历史数据库存储信息与缓存不一致");
            }
            data.setUserBotId2History(map);
        }
        if (alreadyLoaded) {
            if (!CollectionUtils.isEmpty(collectTBS)) {
                Map<String, Long> map = new HashMap<>();
                for (XiaoDuCollectTB xiaoDuCollectTB: collectTBS) {
                    if (StringUtils.isBlank(jytId)) jytId = xiaoDuCollectTB.getJytUserId();
                    String botAccount = ScheduleServiceNew.getBotAccountByWorkname(xiaoDuCollectTB.getWorkName());
                    if (StringUtils.isNotBlank(botAccount)) map.put(botAccount, xiaoDuCollectTB.getCreateTime().getTime());
                    else log.warn("onLunchFillUserBehaviorDataInMaster: 收藏数据库存储信息与缓存不一致");
                }
                data.setUserBotId2Collected(map);
            }
            if (!CollectionUtils.isEmpty(orderTBS)) {
                Map<String, Long> map = new HashMap<>();
                for (XiaoDuOrderTB xiaoDuOrderTB: orderTBS) {
                    if (StringUtils.isBlank(jytId)) jytId = xiaoDuOrderTB.getAppUserId();
                    String botAccount = ScheduleServiceNew.getBotAccountByWorkname(xiaoDuOrderTB.getWorkName());
                    if (StringUtils.isNotBlank(botAccount)) map.put(botAccount, xiaoDuOrderTB.getNumber());
                    else log.warn("onLunchFillUserBehaviorDataInMaster: 送花数据库存储信息与缓存不一致");
                }
                data.setUserBotId2Flowers(map);
            }
            data.setJytUserId(jytId);
        }
//            data.setChannel(channelId);
        log.info("UserBehaviorData:"+data);
        getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);
        return data;
//        return null;
    }

    public static ThreadPoolExecutor getPOOL() {
        return POOL;
    }

    public static String getDPLTemple(String path) {
        String temple = null;
        try {
            temple = getJsonFromName(path);
        } catch (IOException e) {
            log.error("没有获取到json字符串:{}", e.toString());
        }
        return temple;
    }

    private static String getJsonFromName(String filename) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] buffer = new char[512];
        int ln;
        StringBuilder builder = new StringBuilder();
        while ((ln = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, ln);
        }
        return builder.toString();
    }

    public static boolean doesUserCollectThisBot(String botID, String accessToken, String userId, String channelId, String jytUserId) {
        boolean collected = false;
        if (accessToken != null) {
            UserBehaviorData userData = getRedisTemplate().opsForValue().get(userId);
            if (userData == null) {
                log.info("doesUserCollectThisBot: data is null");
                if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
                userData = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, true);
                if (userData == null) return false;
            }
            collected = userData.getUserBotId2Collected().containsKey(botID);
        }
        return collected;
    }

    static String getAsideText(String text) {
        int i1 = text.indexOf("【");
        int i2 = text.indexOf("@");
        return text.substring(i1+1, i2);
    }

    public static Long getUserSendFlowerNum(String botAccount, String userID, String channelId, String accessToken, String jytUserId) {
        if (accessToken != null) {
            UserBehaviorData data = getRedisTemplate().opsForValue().get(userID);
            if (data == null) {
                log.warn("getUserSendFlowerNum: data is null");
                if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userID);
                data = onLunchFillUserBehaviorDataInMaster(userID, channelId, jytUserId, true);
                if (data == null) return 0L;
            }
            Long count = data.getUserBotId2Flowers().get(botAccount);
            if (count == null) count = 0L;
            return count;
        }
        return 0L;
    }

    public static boolean didUserCollecThisProduct(String botAccount, String userID,
                                            String jytUserId, String channelId) {
        UserBehaviorData data = getRedisTemplate().opsForValue().get(userID);
        if (data == null) {
            log.info("didUserCollecThisProduct: data is null");
            data = onLunchFillUserBehaviorDataInMaster(userID, channelId, jytUserId, true);
        }
        if (data == null) return false;
        return data.getUserBotId2Collected().containsKey(botAccount);
    }

    public static boolean userUnCollectTheBot(final String userId, final String botAccount, String channelId,
                                    String jytUserId, String accessToken) {
        if (StringUtils.isAnyBlank(channelId, botAccount, userId)) return false;
        UserBehaviorData behaviorData = getRedisTemplate().opsForValue().get(userId);
        if (behaviorData == null) {
            log.info("userUnCollectTheBot: data is null");
            if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
            behaviorData = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, true);
        }
        if (behaviorData != null) {
            if (!behaviorData.getUserBotId2Collected().containsKey(botAccount)) return true;
            behaviorData.getUserBotId2Collected().remove(botAccount);
            if (StringUtils.isBlank(jytUserId)) {
                jytUserId = behaviorData.getJytUserId();
                if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
                if (StringUtils.isBlank(jytUserId)) return false;
            }
//            updateTheUserCollectDataImmediately(jytUserId, channelId, botAccount, false);
//            noticeOtherServer(3, botAccount, null, false, userId, jytUserId, channelId, true);
            final String jytId = jytUserId;
            POOL.submit(() -> updateCollectTable(false, userId, channelId, botAccount, jytId));
            getRedisTemplate().opsForValue().set(userId, behaviorData, 1, TimeUnit.HOURS);
            return true;
        }
        return false;
    }

    private static void updateCollectTable(boolean collect, String userId, String channelId, String botAccount, String jytUserId) {
        XiaoDuCollectRepository repository = BaseHolder.getBean("xiaoDuCollectRepository");
        String workName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
        XiaoDuCollectTB tb = repository.getByXiaoduUserIdAndChannelIdAndWorkName(userId, channelId, workName);
        if (tb == null && collect) {
            tb = new XiaoDuCollectTB();
            tb.setChannelId(channelId);
            tb.setJytUserId(jytUserId);
            tb.setWorkName(workName);
            tb.setXiaoduUserId(userId);
            repository.save(tb);
        } else if (tb != null && !collect) {
            repository.deleteById(tb.getId());
        }
    }

    static void noticeOtherServer(int type, String botAccount, Long timeOrCount, Boolean collect,
                                  String userId, String jytUserId, String channel, Boolean loaded) {
        // 1:history  2:flower  3:collect  4:jytUserId  5:update all
        NoticeReceiveData data = new NoticeReceiveData();
        data.setBotAccount(botAccount);
        data.setCollect(collect);
        data.setTimeOrCount(timeOrCount);
        data.setType(type);
        data.setXiaoduUserId(userId);
        data.setJytUserId(jytUserId);
        data.setChannel(channel);
        data.setLoaded(loaded);
        String dataStr = JSON.toJSONString(data);
        log.info("NoticeReceiveData:"+dataStr);
        for (String host: ScheduleServiceNew.serverAddresses) {
            OkHttp3Utils.doPostAsy(host+"xiaoduhome/notice", dataStr);
        }
    }

    public static boolean userCollectTheBot(String userId, String botAccount, String channelId, String jytUserId, String accessToken) {
        if (StringUtils.isAnyBlank(botAccount, channelId, userId)) return false;
        UserBehaviorData behaviorData = getRedisTemplate().opsForValue().get(userId);
        if (behaviorData == null) {
            log.info("userCollectTheBot: data is null");
            if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
            behaviorData = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, true);
        }
        if (behaviorData != null) {
            if (behaviorData.getUserBotId2Collected().containsKey(botAccount)) return true;
            behaviorData.getUserBotId2Collected().put(botAccount, System.currentTimeMillis());
            if (StringUtils.isBlank(jytUserId)) {
                jytUserId = behaviorData.getJytUserId();
                if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
                if (StringUtils.isBlank(jytUserId)) return false;
            }
//            updateTheUserCollectDataImmediately(jytUserId, channelId, botAccount, true);
//            noticeOtherServer(3, botAccount, ScheduleServiceNew.curTime, true, userId, jytUserId, channelId, true);
            final String jytId = jytUserId;
            POOL.submit(() -> updateCollectTable(true, userId, channelId, botAccount, jytId));
            getRedisTemplate().opsForValue().set(userId, behaviorData, 1, TimeUnit.HOURS);
            return true;
        }
        return false;
    }

    static Charge sendOneFlowerByCharge(String userId, String jytUserId, String accessToken, String trace, String channelId) {
        String price = ScheduleServiceNew.FLOWER_UNIT_PRICE;
        int unitPrice = Integer.parseInt(price);
        String amount = String.valueOf(unitPrice/100.0);
        String orderId = new StringBuilder(userId).append("_").append(System.currentTimeMillis()).toString();
        if (jytUserId == null) {
            jytUserId = getUsUserId(userId, accessToken, channelId);
            if (jytUserId == null) return null;
        }
        String botId;
        if (trace != null && trace.length() > 2) {
            boolean inPlay;
            if ((inPlay=StringUtils.endsWith(trace, File.separator))) {
                botId = trace.substring(2, trace.length()-1);
            }else {
                botId = trace.substring(2);
            }
            XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
            XiaoDuOrderTB orderTB = buildBeforePayPojo(botId, jytUserId, userId, unitPrice, orderId);
            if (orderTB ==null) return null;
            XiaoDuOrderTB response = repository.save(orderTB);

            if (response.getId() != null) {
                String desc = new StringBuilder("感谢您赏赐给该作品的").append(1).append("朵鲜花").toString();
                String name = ScheduleServiceNew.getWorkNameByBotAccount(botId);
                if (name != null) desc = new StringBuilder("感谢您赏赐给").append(name)
                        .append("的").append(1).append("朵鲜花").toString();
                Charge charge = new Charge(amount, orderId, "鲜花", desc);
                charge.setToken(new StringBuilder(botId).append(File.separator).append(1).append(File.separator).append(inPlay ? 1 : 0).toString());
                return charge;
            } else {
                log.error("订单没有插入到xioadu_order_tb");
            }
        }else {
            log.error("无法获取到botID");
        }
        return null;
    }

    private static XiaoDuOrderTB buildBeforePayPojo(String botAccount, String appUserId, String userId, int unitPrice,
                                              String orderId) {
        XiaoDuOrderTB xiaoDuOrderTB = new XiaoDuOrderTB();
        String workName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
        if (StringUtils.isBlank(workName)) return null;
        xiaoDuOrderTB.setWorkName(workName);
        xiaoDuOrderTB.setAppUserId(appUserId);
        xiaoDuOrderTB.setXiaoduUserId(userId);
        xiaoDuOrderTB.setUnitPrice(unitPrice);
        xiaoDuOrderTB.setNumber(1L);
        xiaoDuOrderTB.setIsBuy(false);
        xiaoDuOrderTB.setStatus(false);
        xiaoDuOrderTB.setChOrderId(orderId);
        xiaoDuOrderTB.setDevice(1);
        xiaoDuOrderTB.setTimeStamp(System.currentTimeMillis());
        return xiaoDuOrderTB;
    }

    public static boolean doesUserPlayed(String botAccount, String userId, String channelId, Boolean loaded) {
        UserBehaviorData data = getRedisTemplate().opsForValue().get(userId);
        if (data == null) {
            log.info("doesUserPlayed: data is null");
            data = onLunchFillUserBehaviorDataInMaster(userId, channelId, null, loaded);
        }
        return data != null && data.getUserBotId2History().containsKey(botAccount);
    }

    public static long updateUserFlower(String userId, int flowerNum, String channelId,
                                        String jytUserId, String accessToken, String botAccount) {
        UserBehaviorData data = getRedisTemplate().opsForValue().get(userId);
        if (data == null) {
            log.info("updateUserFlower: data is null");
            if (StringUtils.isBlank(jytUserId)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
            data = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, true);
            if (data == null) {
                log.error("没有获取到用户数据信息，导致更新用户送花缓存数据失败");
                return 0;
            }
        }
        Long before = data.getUserBotId2Flowers().get(botAccount);
        if (before == null) {
            before = 0L;
        }
        data.getUserBotId2Flowers().put(botAccount, before+flowerNum);
        getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);
        return before+flowerNum;
    }

    public static void updateTheUserHistory(final String userId,final String botAccount, final String accessToken,
                                     final String channelId, final String jytUserId) {
        POOL.submit(() -> {
            UserBehaviorData data = getRedisTemplate().opsForValue().get(userId);
            if (data == null) {
                log.info("updateTheUserHistory: data is null");
                String appUserId = jytUserId;
                boolean loaded = StringUtils.isNotBlank(accessToken);
                if (StringUtils.isBlank(appUserId)) {
                    if (loaded) {
                        appUserId = getUserIdByToken(accessToken, channelId, userId);
                    }
                }
                data = onLunchFillUserBehaviorDataInMaster(userId, channelId, appUserId, loaded);
                if (data == null) return;
            }
            ProjectData actualBot = ScheduleServiceNew.getProjectByBotAccount(botAccount);
            data.getUserBotId2History().put(actualBot.getBotAccount(), System.currentTimeMillis());
            getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);

            XiaoDuHistoryTBRepository repository = BaseHolder.getBean("xiaoDuHistoryTBRepository");
            XiaoDuHistoryTB tb = repository.getByXiaoduUserIdAndChannelIdAndWorkName(userId, channelId, actualBot.getName());
            if (tb == null) {
                tb = new XiaoDuHistoryTB();
                tb.setChannelId(channelId);
                tb.setJytUserId(jytUserId);
                tb.setXiaoduUserId(userId);
                tb.setWorkName(actualBot.getName());
            }
            repository.save(tb);
        });
    }

//    static void updateTheUserCollectData(final String userId, String channelId) {
//        UserBehaviorData data = USER_HISTORY.remove(userId);
//        if (data != null) {
//            try {
//                UpdateUserCollectData collectData = new UpdateUserCollectData();
//                collectData.setChannel(channelId);
//                collectData.setUserId(URLEncoder.encode(userId, "UTF-8"));
//                collectData.setCollectInfo(data.getUserBotId2Collected());
//                OkHttp3Utils.doPutAsy(HOST_UTL+"audio_box/update", JSON.toJSONString(collectData));
//            } catch (Exception e) {
//                log.warn("更新历史记录信息出错:"+userId);
//            }
//        }
//    }

//    private static void updateTheUserCollectDataImmediately(String userId, String channelId, String botAccount, boolean collect) {
//        try {
//            String name = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
//            CollectInfo info = new CollectInfo();
//            info.setUserId(userId);
//            info.setChannelId(channelId);
//            info.setWorkName(name);
//            if (collect) {
//                OkHttp3Utils.doPostAsy(HOST_UTL+"audio_box/collect", JSON.toJSONString(info));
//            } else {
//                OkHttp3Utils.doDelAsy(HOST_UTL+"audio_box/collect", JSON.toJSONString(info));
//            }
//        } catch (Exception e) {
//            log.warn("更新历史记录信息出错:"+userId);
//        }
//    }

    public static Boolean dealChargeCallback(ChargeEvent chargeEvent, final String userId, final String accessToken,
                                     final String jytUserId, String workName, String channelId) {
        Payload payload = chargeEvent.getPayload();
        final String chOrderId = chargeEvent.getSellerOrderId();
        String[] tokens = chargeEvent.getToken().split(File.separator);
        final String botAccount = tokens[0];
        if ("SUCCESS".equals(payload.getPurchaseResult())) {
            // createTimestamp字段与baiduOrderReferenceId字段与之后百度方进行结算使用,
            // 现在createTimestamp由于百度方的bug需等到九月份才能获取到
            final String baiduOrderReferenceId = payload.getBaiduOrderReferenceId();
            final String createTimestamp = payload.getAuthorizationDetails().getCreationTimestamp();
            final String num = tokens[1];

            POOL.submit(() -> {
                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
//                Long id = 0L;
                try {
//                    XiaoDuOrderTB tb = repository.getByChOrderId(chOrderId);
//                    id = tb.getId();
                    repository.updateAterSeccessPayed(baiduOrderReferenceId,chOrderId, createTimestamp);
                } catch (Exception e) {
//                    StringWriter writer = new StringWriter();
//                    e.printStackTrace(new PrintWriter(writer,true));
                    log.error("更新用户{}的支付状态出错:{},订单信息:订单号:{}.baiduOrderId:{},createTime:{}",
                            e.toString(),userId, chOrderId, baiduOrderReferenceId, createTimestamp);
                }

                try {
                    String appUserId = jytUserId;
                    if (StringUtils.isBlank(appUserId)) appUserId = getUsUserId(userId,accessToken, channelId);
                    if (StringUtils.isBlank(appUserId)) {
                        log.error("像app_flower_tb中添加数据失败因为无法获取到用户的id,数据信息:botAccount:{},num:{},xiaoduUserId:{}",
                                botAccount,num,userId);
                        return;
                    }
                    if ("null".equals(appUserId)) {
                        log.error("小度无屏在向app-flower_tb插入数据时没有获取到此用户登录的userID,数据信息:botAccount:{},num:{},xiaoduUserId:{}",
                                botAccount,num,userId);
                    }
//                    SendFlowerVO vo = new SendFlowerVO();
//                    vo.setBotAccount(botAccount);
//                    vo.setChannelId(channelId);
//                    vo.setName(workName);
//                    vo.setNum(Long.parseLong(num));
//                    vo.setUnitPrice(Double.parseDouble(Config.getProperty("unitPrice", Constants.UNIT_PRICE))/100);
//                    vo.setOrderId(id);
//                    vo.setUserId(appUserId);
//                    OkHttp3Utils.doPostJsonStr(HOST_UTL+"audio_box/flower", JSON.toJSONString(vo));
                } catch (Exception e) {
                    log.error("像app_flower_tb中添加数据失败,{},数据信息:botAccount:{},num:{},xiaoduUserId:{}",
                            e.toString(),botAccount,num,userId);
                }
            });
            return true;
        }else {
            log.info("支付发生错误,"+payload.getMessage());
            POOL.submit(() -> {
                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
                repository.deleteByWorkNameAndChOrderId(ScheduleServiceNew.getWorkNameByBotAccount(botAccount), chOrderId);
            });
            return false;
        }
    }

    static void updateTheUserBehaviorDataMark(final String userId, final String botAccount,
                                              final String accessToken, final String jytUserId, final String channel) {
        POOL.submit(() -> {
            if (StringUtils.isAnyBlank(botAccount, userId)) return;
//            UserBehaviorData data = USER_HISTORY.get(userId);
            UserBehaviorData data = getRedisTemplate().opsForValue().get(userId);
            if (data != null) {
//                data.setMark(1);
                data.getUserBotId2History().put(botAccount, System.currentTimeMillis());
//                noticeOtherServer(1, botAccount, ScheduleServiceNew.curTime, false, userId, jytUserId, channel, accessToken!= null);
            } else {
                log.info("updateTheUserBehaviorDataMark: data is null");
                data = onLunchFillUserBehaviorDataInMaster(userId, channel, jytUserId, accessToken != null);
                if (data != null) {
                    data.getUserBotId2History().put(botAccount, System.currentTimeMillis());
//                    noticeOtherServer(1, botAccount, ScheduleServiceNew.curTime, false, userId, jytUserId, channel, accessToken!= null);
                }
            }
            if (data != null) {
                getRedisTemplate().opsForValue().set(userId, data, 1, TimeUnit.HOURS);
            }
            XiaoDuHistoryTBRepository repository = BaseHolder.getBean("xiaoDuHistoryTBRepository");
            String botName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
            XiaoDuHistoryTB tb = repository.getByXiaoduUserIdAndChannelIdAndWorkName(userId, channel, botName);
            if (tb == null) {
                tb = new XiaoDuHistoryTB();
                tb.setChannelId(channel);
                tb.setJytUserId(jytUserId);
                tb.setXiaoduUserId(userId);
                tb.setWorkName(botName);
            }
            repository.save(tb);
        });
    }

    static CollectHistoryDocument getCollectPage(String userId, String accessToken, String jytId, int start, String channelId) {
        log.info("getCollectPage:start->"+start);
        UserBehaviorData behaviorData = USER_HISTORY.get(userId);
        int pageNum;
        if (behaviorData == null) {
            log.info("getCollectPage: data is null");
            if (StringUtils.isBlank(jytId)) jytId = getUserIdByToken(accessToken,channelId, userId);
            behaviorData = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytId, true);
            if (behaviorData == null) return null;
        }
        Map<String, Long> userBotId2Collected = behaviorData.getUserBotId2Collected();
        int bound = userBotId2Collected.size();
        if (start >= bound) {
            if (bound == 0) {
                CollectHistoryDocument emptyMark = new CollectHistoryDocument();
                emptyMark.setIdToken("1");
                return emptyMark;
            }
            return null;
        }
        pageNum = bound/CO_HI_PAGE_SIZE + (bound%CO_HI_PAGE_SIZE == 0 ? 0 : 1);
        log.info("getCollectPage:pageNum->"+pageNum);
//        log.info("collect/history: pagesize:{},bound{}",pageNum,bound);
        TreeMap<Long, String> order = new TreeMap<>((o1, o2) -> (o1 < o2) ? 1 : ((o1.equals(o2)) ? 0 : -1));
        for (Map.Entry<String, Long> entry: userBotId2Collected.entrySet()) {
            order.put(entry.getValue(), entry.getKey());
        }
        List<String> favoriteProducts = new ArrayList<>();
        int i = 0, e = start+CO_HI_PAGE_SIZE >= bound ? bound : start+CO_HI_PAGE_SIZE;
        for (Map.Entry<Long, String> entry: order.entrySet()) {
            if (i == e) break;
            if (i++ >= start) {
                favoriteProducts.add(entry.getValue());
            }
        }

//        log.info("favoriteProducts length:{}, start:{}", favoriteProducts.size(), start);
        if (favoriteProducts.size() == 0) return null;
        ArrayList<HistoryData> projectInfoList = new ArrayList<>();
        int j = start+1;
        //log.info("id list:{}", favoriteProducts);
        final StringBuilder build = new StringBuilder();
        for (String botAccount :favoriteProducts) {
            build.append(botAccount).append(File.separator);
            ProjectData productDetail = ScheduleServiceNew.getProjectByBotAccount(botAccount);

            String intro = productDetail.getIntro();
            if (StringUtils.isBlank(intro)) intro = "作者太懒没写介绍";
            String botAccountId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(correctName(productDetail.getName())).toString();
            String name = productDetail.getName();
            HistoryData data = new HistoryData(botAccountId, j++, productDetail.getBannerImgUrl(), correctName(name), intro);
            data.setAuthorName(productDetail.getAuthorName());
            projectInfoList.add(data);
        }
        CollectHistoryDocument collectHistoryDocument = CollectHistoryUtils.getDocument(projectInfoList, true,
                start/CO_HI_PAGE_SIZE+1, pageNum);
        String idToken;
        if (build.length() != 0) {
            build.deleteCharAt(build.length()-1);
            idToken = Base64.getEncoder().encodeToString(build.toString().getBytes());
            collectHistoryDocument.setIdToken(idToken);
        }
        return collectHistoryDocument;
    }

    static CollectHistoryDocument getHistory(String userId, String accessToken,String jytUserId, int start, String channelId) {
        log.info("getHistory:start->"+start);
        List<XiaoDuHistoryBotIdTime> historyProducts = new ArrayList<>();
        UserBehaviorData behaviorData = USER_HISTORY.get(userId);
        int pageNum = 0;
        if (behaviorData == null) {
            log.info("getHistory: data is null");
            if (StringUtils.isBlank(jytUserId) && StringUtils.isNotBlank(accessToken)) jytUserId = getUserIdByToken(accessToken, channelId, userId);
            behaviorData = onLunchFillUserBehaviorDataInMaster(userId, channelId, jytUserId, accessToken != null);
        }
        if (behaviorData != null) {
            try {
                int bound = behaviorData.getUserBotId2History().size();
                if (start >= bound) {
                    if (bound == 0) {
                        CollectHistoryDocument emptyMark = new CollectHistoryDocument();
                        emptyMark.setIdToken("1");
                        return emptyMark;
                    }
                    return null;
                }
                pageNum = bound/CO_HI_PAGE_SIZE + (bound%CO_HI_PAGE_SIZE == 0 ? 0 : 1);
                log.info("getHistory:pageNum->"+pageNum);
                TreeMap<Long, String> order = new TreeMap<>((o1, o2) -> (o1 < o2) ? 1 : ((o1.equals(o2)) ? 0 : -1));
                for (Map.Entry<String, Long> entry: behaviorData.getUserBotId2History().entrySet()) {
                    order.put(entry.getValue(), entry.getKey());
                }
                int i = 0, end = start+CO_HI_PAGE_SIZE;
                for (Map.Entry<Long, String> entry: order.entrySet()) {
                    if (i == end) break;
                    if (i++ >= start) {
                        XiaoDuHistoryBotIdTime history = new XiaoDuHistoryBotIdTime();
                        history.setBotId(entry.getValue());
                        history.setLastTime(entry.getKey());
                        historyProducts.add(history);
                    }
                }
            } catch (Exception e){}
            if (historyProducts.size() == 0) return null;

            ArrayList<HistoryData> projectInfoList = new ArrayList<>();
            int i = start+1;
            final StringBuilder build = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            historyProducts.sort((o1, o2) -> (o1.getLastTime()<o2.getLastTime()) ? 1 : (o1.getLastTime().equals(o2.getLastTime())) ? 0 : -1);
            for (XiaoDuHistoryBotIdTime historyInfo: historyProducts) {
                String botAccount = historyInfo.getBotId();
                build.append(botAccount).append(File.separator);
                ProjectData productDetail = ScheduleServiceNew.getProjectByBotAccount(botAccount);

                String intro = productDetail.getIntro();
                if (StringUtils.isBlank(intro)) intro = "作者太懒没写介绍";
                String botAccountId = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(correctName(productDetail.getName())).toString();
                String lastTime = sdf.format(new java.sql.Date(historyInfo.getLastTime()));
                String name = productDetail.getName();
                HistoryData data = new HistoryData(botAccountId, i++, productDetail.getBannerImgUrl(), correctName(name), intro);
                data.setAuthorName(lastTime);
                projectInfoList.add(data);
            }
            CollectHistoryDocument document = CollectHistoryUtils.getDocument(projectInfoList, false,
                    start/CO_HI_PAGE_SIZE +1, pageNum);
            String idToken;
            if (build.length() != 0) {
                build.deleteCharAt(build.length()-1);
                idToken = Base64.getEncoder().encodeToString(build.toString().getBytes());
                document.setIdToken(idToken);
            }
            return document;
        }
        return null;
    }

    private static String correctName(String name) {
        int brace = name.indexOf("{");
        if (brace != -1) name = name.substring(0, brace);
        return name;
    }

    public static List<String> seperateActor(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        List<String> actorList = new ArrayList<>();
        while (matcher.find()) {
            String actor = matcher.group(1).trim();
            actor = actor.substring(0, actor.indexOf("@"));
            actorList.add(actor);
        }
        return actorList;
    }

    static List<ActorWithWord> getTheActorsWithWords(JSONArray array, String botAccount) {
        if (array != null && array.size() > 0) {
            JSONObject job;
            ActorWithWord aw;
            List<ActorWithWord> list= new ArrayList<>();
            int i = 1;
            for (Object ob: array) {
                job = (JSONObject)ob;
                aw = new ActorWithWord();
                aw.setActor(job.getString("npcName"));
                aw.setAsideImg(ScheduleServiceNew.getAsideImgUrl(botAccount, aw.getActor()));
                aw.setText(job.getString("text").replaceAll("\\{/.*?/}|\uD83C\uDD70|\uD83C\uDD71", ""));
                aw.setItemSeqId(i++);
                list.add(aw);
            }
            return list;
        }
        return null;
    }

    public static List<ActorWithWord> getTheActorsWithWords(ArrayNode array, String botAccount) {
        if (array != null && array.size() > 0) {
            ActorWithWord aw;
            List<ActorWithWord> list= new ArrayList<>();
            int i = 1;
            for (Iterator<JsonNode> it = array.elements(); it.hasNext(); ) {
                JsonNode ob = it.next();
                aw = new ActorWithWord();
                aw.setActor(ob.get("npcName").asText());
                aw.setAsideImg(ScheduleServiceNew.getAsideImgUrl(botAccount, aw.getActor()));
                aw.setText(ob.get("text").asText().replaceAll("\\{/.*?/}|\uD83C\uDD70|\uD83C\uDD71", ""));
                aw.setItemSeqId(i++);
                list.add(aw);
            }
            return list;
        }
        return null;
    }

    public static List<ActorWithWord> getTheActorsWithWords(List<DialogData> dialogs, final String botAccount) {
        if (!CollectionUtils.isEmpty(dialogs)) {
            AtomicInteger integer = new AtomicInteger(1);
            return dialogs.parallelStream().map(dialogData -> {
                ActorWithWord aw = new ActorWithWord();
                aw.setActor(dialogData.getNpcName());
                aw.setAsideImg(ScheduleServiceNew.getAsideImgUrl(botAccount, dialogData.getNpcName()));
                aw.setText(dialogData.getText().replaceAll("\\{/.*?/}|\uD83C\uDD70|\uD83C\uDD71|\\^", ""));
                aw.setItemSeqId(integer.getAndIncrement());
                return aw;
            }).collect(Collectors.toList());
        }
        return null;
    }

    public static String[] seperateActorWord(String text, Pattern pattern) {
        String[] actorWords = pattern.split(text);
        if (StringUtils.startsWith(text.trim(), "【")) {
            actorWords = ArrayUtils.remove(actorWords, 0);
        }
        return actorWords;
    }

    public static ThreadPoolExecutor getPool() {
        return POOL;
    }

    public static void receiveNotice(NoticeReceiveData data) {
        log.info("receiveNotice:"+data);
        int type = data.getType();
        if (type == 5) {
            DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(data.getXiaoduUserId(), data.getChannel(), data.getJytUserId(), data.getLoaded());
            return;
        }
        UserBehaviorData behaviorData = USER_HISTORY.get(data.getXiaoduUserId());
        if (behaviorData == null) {
            log.info("receiveNotice: data is null");
            behaviorData = DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(data.getXiaoduUserId(), data.getChannel(), data.getJytUserId(), data.getLoaded());
            if (behaviorData == null) {
                log.warn("receiveNotice fill data failed");
                return;
            }
        }
//        behaviorData.setMark(1);
        if (behaviorData.getJytUserId() == null) behaviorData.setJytUserId(data.getJytUserId());
        switch (type) {
            case 1 : behaviorData.getUserBotId2History().put(data.getBotAccount(), data.getTimeOrCount()); break;
            case 2 : behaviorData.getUserBotId2Flowers().put(data.getBotAccount(), data.getTimeOrCount()); break;
            case 3 : {
                if (Boolean.TRUE.equals(data.getCollect())) {
                    behaviorData.getUserBotId2Collected().put(data.getBotAccount(), data.getTimeOrCount());
                } else {
                    behaviorData.getUserBotId2Collected().remove(data.getBotAccount());
                }
                break;
            }
            }
    }

    public static void printCache() {
        log.info("history cache:"+USER_HISTORY);
    }

    public static Integer extractNum(String numStr) {
        numStr = numStr.replaceAll("[ \uD83C\uDD70\uD83C\uDD71]", "");
        int num = 1;
        try {
            num = Integer.parseInt(numStr);
            return num;
        } catch (Exception e) {
            int i;
            if ((i=numStr.indexOf("第")) != -1) {
                String str = numStr.substring(i+1, numStr.length()-1);
                try {
                    num = Integer.parseInt(str);
                } catch (Exception e1) {
                    num = Utils.changeHanToNum(str);
                }
            } else {
                num = Utils.changeHanToNum(numStr);
            }
        }
        return num;
    }

    public static void main(String[] args) throws IOException {
//        String accesstoken = "349616e0-f039-4ad6-81be-344c2d53dd47";
//        String accesstoken = "6fcf70d5-3611-4fc0-8a0b-491532526afc";
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("App-Channel-Id", "JIAOYOUTIANXIA");
//        headers.put("Authorization", BEARER + accesstoken);
//        String str = OkHttp3Utils.doGet(USER_ID_BY_TOKEN, new HashMap<>(), headers);
//        System.out.println(str);

        System.out.println("手机号验证码获取token时^只传了".replaceAll("\\{/.*?/}|\uD83C\uDD70|\uD83C\uDD71|\\^", ""));
    }
}
