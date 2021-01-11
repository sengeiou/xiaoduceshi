package xiaoduhome.spirit;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import ai.qiwu.com.xiaoduhome.xiaoai.common.RequestTerminal;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import com.alibaba.da.coin.ide.spi.meta.Action;
import com.alibaba.da.coin.ide.spi.meta.AskedInfoMsg;
import com.alibaba.da.coin.ide.spi.meta.ExecuteCode;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL_DEFAULT;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.XIAOWU_SKILL_NOVEL;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.XIAOWU_SKILL_SMART;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.WELCOME_NOVEL;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.WELCOME_SMART;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.*;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.audioMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.botAccount2ItsCache;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.childAudioMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.childRecordMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.recordMap;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;

/**
 * @author 苗权威
 * 20-1-9 下午5:04
 */
@Service
@Slf4j
public class SpiritRedisService {
//    @Value("${csvDic}")
    private String csvDic;

//    @Value("${csvChildDic}")
    private String csvChildDic;

//    public static volatile Integer mark = 1;
//    public static volatile Integer manyId = 1;

    private static final String AB_REGEX = "\uD83C\uDD70(.*?)\uD83C\uDD71";

    private final StringRedisTemplate stringRedisTemplate;

    public static final String PREFIX_REDIS = "audio_box_";

    public static final String PREFIX_REDIS_IDLE = "audio_idle_";

    public static final String PREFIX_REDIS_DIRECT = "audio_direct_";

    private static final String PREFIX_TMALL = "TMALL_";

    private static final HashMap<String, String> ADULT_HEADER;
    private static final HashMap<String, String> CHILD_HEADER;

    static {
        ADULT_HEADER = new HashMap<>();
        ADULT_HEADER.put("Content-Type", "application/json;charset=utf-8");
        ADULT_HEADER.put("App-Channel-Id", "tianmao-jiaoyou-audio-adult-test");
        CHILD_HEADER = new HashMap<>();
        CHILD_HEADER.put("Content-Type", "application/json;charset=utf-8");
        CHILD_HEADER.put("App-Channel-Id", "tianmao-jiaoyou-audio-child-test");
    }

    public SpiritRedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取返回给天猫的数据
     * @param request 请求参数
     * @return 返回数据封装在开发包中的TaskResult
     */
    public TaskResult getResponse(TaskQuery request, int type) {
        return getNeededParam(request, type);
    }

    /**
     * 解析获取需要的参数信息:用户的话，用户的id，意图id
     * @param request 请求参数
     * @return 返回数据封装在开发包中的TaskResult
     */
    private TaskResult getNeededParam(TaskQuery request, int type) {
        String userId = getUserId(request);
        String redisId = PREFIX_REDIS+userId;
        String userWord = request.getUtterance();
        if (StringUtils.isBlank(userId)) return buildTextTaskResult("不好意思好像出了什么问题，正在抓紧修复中", request.getIntentId());
        if (StringUtils.isBlank(userWord)) return buildTextTaskResult(Constants.ErrorMsg.SORRY_UNCATCH, request.getIntentId());
        Long intentId = request.getIntentId();

        if (userWord.contains("晓悟智能小说")) {
            if (ScheduleServiceNew.SERVER_CHANGE) stringRedisTemplate.delete(Constants.CAN_NOT_MOVE_USER_KEY+userId);
            RequestTerminal.tmallLunchCancelRequest(userId, type);
            return buildTextTaskResult(WELCOME_NOVEL, intentId);
        } else if (userWord.contains("晓悟智能故事")) {
            if (ScheduleServiceNew.SERVER_CHANGE) stringRedisTemplate.delete(Constants.CAN_NOT_MOVE_USER_KEY+userId);
            RequestTerminal.tmallLunchCancelRequest(userId, type);
            return buildTextTaskResult(WELCOME_SMART, intentId);
        }

        String baseHostUrl = ScheduleServiceNew.BASE_HOST_URL;
        String audioUrl = ScheduleServiceNew.BASE_AUDIO_URL;
        String cdnUrl = ScheduleServiceNew.CDN_HOST_URL;
        if (ScheduleServiceNew.SERVER_CHANGE) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.SERVER_CHANGE_KEY))) {
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.CAN_NOT_MOVE_USER_KEY+userId))) {
                    stringRedisTemplate.expire(Constants.CAN_NOT_MOVE_USER_KEY+userId, 4, TimeUnit.MINUTES);
                } else {
                    stringRedisTemplate.opsForValue().set(Constants.CAN_NOT_MOVE_USER_KEY+userId, "1", 4, TimeUnit.MINUTES);
                }
                baseHostUrl = ScheduleServiceNew.OLD_BASE_HOST_URL;
                audioUrl = ScheduleServiceNew.OLD_BASE_AUDIO_URL;
                cdnUrl = ScheduleServiceNew.OLD_CDN_HOST_URL;
            } else if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.CAN_NOT_MOVE_USER_KEY+userId))){
                stringRedisTemplate.expire(Constants.CAN_NOT_MOVE_USER_KEY+userId, 4, TimeUnit.MINUTES);
                baseHostUrl = ScheduleServiceNew.OLD_BASE_HOST_URL;
                audioUrl = ScheduleServiceNew.OLD_BASE_AUDIO_URL;
                cdnUrl = ScheduleServiceNew.OLD_CDN_HOST_URL;
            }
        }

        try {
//            long start = System.currentTimeMillis();
//            log.info("redis get cost:"+(System.currentTimeMillis()-start));
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                String data = stringRedisTemplate.opsForValue().get(redisId);
                String redisIdleId = PREFIX_REDIS_IDLE+userId;
                if ("out".equals(data)) {
                    String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                    boolean firstIdle;
                    if ((firstIdle = StringUtils.isBlank(idleNum)) || "1".equals(idleNum)) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e){}
                        data = stringRedisTemplate.opsForValue().get(redisId);
                        if (StringUtils.isNotBlank(data) && !"out".equals(data)) {
                            stringRedisTemplate.delete(redisIdleId);
                            stringRedisTemplate.delete(redisId);
                            return buildAudioTaskResult(Arrays.asList(data.split(",")), intentId);
                        }
                        if (firstIdle) idleNum = "1";
                        else idleNum = "2";
                        stringRedisTemplate.opsForValue().set(redisIdleId, idleNum, 15, TimeUnit.SECONDS);
                        return buildTextTaskResult((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                XiaoAiConstants.TTS.WAIT_WORD_2, intentId);
                    } else {
                        stringRedisTemplate.delete(redisIdleId);
                        stringRedisTemplate.delete(redisId);
                    }
                } else if (data != null){
                    stringRedisTemplate.delete(redisId);
                    stringRedisTemplate.delete(redisIdleId);
                    return buildAudioUrlResult(intentId, data);
//                    String botAccount = getBotAccountFromPath(data);
//                    return buildAudioUrlResult(intentId, changeAudioUrlTemp(data, botAccount, cdnUrl));
                }
            }
        } catch (Exception e) {
            log.error("tmall redis error:"+e);
        }
//        log.info("天猫精灵用户说的话:"+userWord);
        try {
            final String finalBaseHost = baseHostUrl;
            final String finalAudioUrl = audioUrl;
            final String finalCdnUrl = cdnUrl;
            Future<TaskResult> future = DplbotServiceUtil.getPOOL().submit(() ->
                    requestTerminate(userId, userWord, type, intentId, finalBaseHost, finalAudioUrl, finalCdnUrl));
            return future.get(1700, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("天猫精灵出现问题:{}", ExceptionUtils.getStackTrace(e));
        } catch (TimeoutException e) {
            log.warn("超时");
            stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
            return buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
        }
        return buildTextTaskResult(Constants.ErrorMsg.SORRY_UNCATCH, intentId);
    }

    private String getBotAccountFromPath(String path) {
        try {
            int i = path.indexOf("audio");
            if (path.charAt(i+5) == '/') {
                return path.substring(i+6, path.indexOf('/',i+6));
            } else {
                return path.substring(i+8, path.indexOf('%',i+8));
            }
        } catch (Exception e) {
            log.warn("getBotAccountFromPath fail:path={},{}",path,e);
        }
        return null;
    }

    private TaskResult requestTerminate(String userId, String userWord, Integer type, Long intentId, String baseHostUrl,
                                        String audioUrl, String cdnUrl) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
//        requestPojo.setAccess(XiaoAiConstants.DEVICE_NAME);
        requestPojo.setMsg(userWord);
//        log.info("天猫用户话:{}", userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        log.info("向终端发出请求的参数信息:{}", requestJsonStr);

        Map<String, String> headers;
        switch (type) {
            case 2:
            case 4: headers =CHILD_HEADER;break;
            case 3:
            case 5:
            default: headers =ADULT_HEADER;
        }

//        String resStr= OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL, requestJsonStr, headers);
        String resStr= OkHttp3Utils.doPostJsonStrForCentralWithUidParam(baseHostUrl, requestJsonStr, headers, userId);
        if (resStr == null) {
            return null;
        }
//        log.info("天猫精灵晓悟返回:"+resStr);
        TaskResult taskResult = null;
        try {
            JSONObject object = JSON.parseObject(resStr);
            try {
                JSONArray commands = object.getJSONArray("commands");
                if (commands != null && commands.size() != 0) {
                    String txt;
                    for (Object ob: commands) {
                        txt = ((JSONObject) ob).getString("text");
                        int i;
                        if (StringUtils.isNotBlank(txt) && (i=txt.indexOf("out")) != -1) {
//                            log.info("天猫，推荐bot退出：{},{}",userWord,userId);
                            String content = txt.substring(i+3, txt.indexOf("☚")).trim();
                            String byeWord = null;
                            String skillName = null;
                            switch (type) {
                                case 4: {
                                    byeWord = Constants.EndMsg.XIAOWU_END_BYE_STORY;
                                    skillName = XIAOWU_SKILL_SMART;
                                    break;
                                }
                                case 5: {
                                    byeWord = Constants.EndMsg.XIAOWU_END_BYE_NOVEL;
                                    skillName = XIAOWU_SKILL_NOVEL;
                                    break;
                                }
                            }
                            if (byeWord == null) byeWord = Constants.EndMsg.END_BYE_Adult;
                            if (skillName == null) skillName = SKILL_DEFAULT;
                            if (StringUtils.isBlank(content)) {
                                JSONArray dialogs = object.getJSONArray("dialogs");
                                if (dialogs != null && dialogs.size() != 0) {
                                    String str = ((JSONObject)dialogs.get(0)).getString("text");
                                    if (StringUtils.isNotBlank(str)) {
                                        str = str.replaceAll("\\{/.*?/}", "")+skillName;
                                        RequestTerminal.tmallLunchCancelRequest(userId, type);
                                        return buildTextEndResult(str, intentId);
                                    }
                                }
                                RequestTerminal.tmallLunchCancelRequest(userId, type);
                                return buildTextEndResult(byeWord, intentId);
                            } else {
                                content = content.replaceAll("\\{/.*?/}", "")+skillName;
                                RequestTerminal.tmallLunchCancelRequest(userId, type);
                                return buildTextEndResult(content, intentId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("TMALL 退出解析出错:"+e);
            }

//            final SpiritTerminateResponse response = JSON.parseObject(resStr, SpiritTerminateResponse.class);
//            if (CollectionUtils.isEmpty(response.getDialogs())) {
//                taskResult = buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
//                return taskResult;
//            }
//            if (botAccount2ItsCache.containsKey(response.getAipioneerUsername())) {
//                if (type == 4) {
//                    if (Boolean.TRUE.equals(botAccount2ItsCache.get(response.getAipioneerUsername()))) {
//                        if (recordMap.containsKey(response.getAipioneerUsername())) {
//                            RequestTerminal.tmallBotCancelRequest(userId, type);
//                            return buildTextTaskResult("该作品属于晓悟智能小说，请退出当前技能打开晓悟智能小说再进行体验", intentId);
//                        }
//                    } else {
//                        if (audioMap.containsKey(response.getAipioneerUsername())) {
//                            RequestTerminal.tmallBotCancelRequest(userId, type);
//                            return buildTextTaskResult("该作品属于晓悟智能小说，请退出当前技能打开晓悟智能小说再进行体验", intentId);
//                        }
//                    }
//                } else {
//                    if (Boolean.TRUE.equals(botAccount2ItsCache.get(response.getAipioneerUsername()))) {
//                        if (childRecordMap.containsKey(response.getAipioneerUsername())) {
//                            RequestTerminal.tmallBotCancelRequest(userId, type);
//                            return buildTextTaskResult("该作品属于晓悟智能故事，请退出当前技能打开晓悟智能故事再进行体验", intentId);
//                        }
//                    } else {
//                        if (childAudioMap.containsKey(response.getAipioneerUsername())) {
//                            RequestTerminal.tmallBotCancelRequest(userId, type);
//                            return buildTextTaskResult("该作品属于晓悟智能故事，请退出当前技能打开晓悟智能故事再进行体验", intentId);
//                        }
//                    }
//                }
//            }
            String path = OkHttp3Utils.doPostJsonStr(audioUrl+"/api/audio", resStr);
            if (StringUtils.isBlank(path) || "null".equals(path)) {
                log.error("没有获取到TTS返回的路径path");
                return buildTextTaskResult(SORRY_REPEAT,intentId);
            }
            String botAccount = object.getString("aipioneerUsername");
            if (StringUtils.isBlank(botAccount)) {
                log.warn("没有获取到aipioneerUsername");
                botAccount = "empty";
            }
            taskResult = buildAudioUrlResult(intentId, (path=changeAudioUrlTemp(path, botAccount, cdnUrl)));
//            log.info("天猫音频地址:"+path);
            stringRedisTemplate.opsForValue().setIfPresent(PREFIX_REDIS+userId, path, 20, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (taskResult == null) {
            taskResult = buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
        }
        return taskResult;
    }

    private TaskResult buildAudioUrlResult(Long intentId, String url) {
        TaskResult result = new TaskResult();
        result.setResultType(ResultType.ASK_INF);
        result.setExecuteCode(ExecuteCode.SUCCESS);

        result.setAskedInfos(Collections.singletonList(new AskedInfoMsg("any", intentId)));
        Action action = new Action("voiceBroadcastUrl");
        action.addProperty("audioUrl", url);
        action.addProperty("expectSpeech", "true");
        action.addProperty("wakeupType", "continuity");

        result.setActions(Collections.singletonList(action));

        return result;
    }

    private String changeAudioUrlTemp(String path, String botAccount, String cdnUrl) {
        if (path.startsWith("a")) {
            return new StringBuilder(cdnUrl).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path.substring(1)).append("&exist=1").toString();
        } else {
            return new StringBuilder(cdnUrl).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path).toString();
        }
    }

    private List<String> dealResponse(SpiritTerminateResponse response, String userId, int type) {
        List<String> idList = new ArrayList<>();
        List<Dialog> dialogs = response.getDialogs();
        String botAccount, npcName, id, person, mood;
        for (Dialog dialog: dialogs) {
            botAccount = dialog.getBotAccount();
            Boolean record = botAccount2ItsCache.get(botAccount);
            if (record == null) return null;
            else if (record) {
                npcName = dialog.getNpcName();
                Collection<String> ls = dealWithSep(dialog, true, botAccount, npcName, null, type);
                if (CollectionUtils.isEmpty(ls)) {
                    id = InitialData.getRecordId(botAccount, npcName, dialog.getText(), type);
                    if (StringUtils.isNotBlank(id)) idList.add(id);
                    else {
                        if (InitialData.botAccount2ItsCache.containsKey(botAccount)) log.warn("没有音频->record:{},{}",dialog,type);
                        return null;
                    }
                } else {
                    idList.addAll(ls);
                }
            } else {
                person = dialog.getPerson();
                mood = dialog.getTone();
                Collection<String> ls = dealWithSep(dialog, false, botAccount, person, mood, type);
                if (CollectionUtils.isEmpty(ls)) {
                    id = InitialData.getAudioId(botAccount, person, mood, dialog.getText(), type);
                    if (StringUtils.isNotBlank(id)) idList.add(id);
                    else {
                        if (InitialData.botAccount2ItsCache.containsKey(botAccount)) log.warn("没有音频->audio:"+dialog);
                        return null;
                    }
                } else {
                    idList.addAll(ls);
                }
            }
        }
        if (idList.size() > 0) {
            stringRedisTemplate.opsForValue().setIfPresent(PREFIX_REDIS+userId, StringUtils.join(idList, ","), 20, TimeUnit.SECONDS);
        }
        return idList;
    }

    private Collection<String> dealWithSep(Dialog dialog, Boolean record, String botAccount,
                                           String porn, String mood, int type) {
        String text = dialog.getText();
        List<String> variableTextList = new ArrayList<>();
        // 获取AB之间的变量,为空则忽略
        Pattern pattern = Pattern.compile(AB_REGEX);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String variableText = matcher.group(1);
            if (StringUtils.isNotBlank(variableText)) variableTextList.add(variableText);
        }
        if (variableTextList.size() != 0) {
            TreeMap<String, String> combineMp3Text2FilePath = new TreeMap<>(Comparator.comparingInt(text::indexOf));
            // 非变量部分
            String[] notVariableTextArray = pattern.split(text);

            int i = 0;
            for (String notVariableText: notVariableTextArray) {
                if (StringUtils.isBlank(notVariableText)) {
                    ++i;
                    continue;
                }
                if (i++ == notVariableTextArray.length-1) {
                    //log.info("888");
                    boolean notVariableLast = text.lastIndexOf(notVariableText)+notVariableText.length() == text.length();
                    //log.info("notVariableLast:{}", notVariableLast);
                    if (notVariableLast) {
                        String last = notVariableText;
                        last = last.replaceAll("\\p{P}", "");
                        if (StringUtils.isBlank(last)) {
                            //log.info("last is empty");
                            break;
                        }
                    }
                }
                String id;
                if (record) {
                    id = InitialData.getRecordId(botAccount, porn, notVariableText, type);
                } else {
                    id = InitialData.getAudioId(botAccount, porn, mood, notVariableText, type);
                }
                if (StringUtils.isBlank(id)) return null;
                combineMp3Text2FilePath.put(notVariableText, id);
            }
            for (String variableText: variableTextList) {
                String id;
                if (record) {
                    id = InitialData.getRecordId(botAccount, porn, variableText, type);
                } else {
                    id = InitialData.getAudioId(botAccount, porn, mood, variableText, type);
                }
                if (StringUtils.isBlank(id)) return null;
                combineMp3Text2FilePath.put(variableText, id);
            }
            return combineMp3Text2FilePath.values();
        }
        return null;
    }

    /**
     * 封装返回数据在TaskResult中
     * @param text TTS文本内容
     * @param intentId 意图id
     * @return 返回数据封装在开发包中的TaskResult
     */
    private TaskResult buildTextTaskResult(String text, Long intentId) {
        TaskResult result = new TaskResult();
        result.setReply(buildText(text));
        result.setResultType(ResultType.ASK_INF);
        result.setExecuteCode(ExecuteCode.SUCCESS);

        result.setAskedInfos(Collections.singletonList(new AskedInfoMsg("any", intentId)));

        return result;
    }

    private TaskResult buildTextEndResult(String text, Long intentId) {
        TaskResult result = new TaskResult();
        result.setReply(buildText(text));
        result.setResultType(ResultType.RESULT);
        result.setExecuteCode(ExecuteCode.SUCCESS);
        return result;
    }

    private TaskResult buildAudioTaskResult(List<String> ids, Long intentId) {
        TaskResult result = new TaskResult();
        result.setReply("叮咚");
        result.setResultType(ResultType.ASK_INF);
        result.setExecuteCode(ExecuteCode.SUCCESS);

        result.setAskedInfos(Collections.singletonList(new AskedInfoMsg("any", intentId)));

        List<Action> actions = ids.stream().filter(StringUtils::isNotBlank).map(s -> {
            Action action = new Action("audioPlayGenieSource");
            action.addProperty("audioGenieId", s);
//            action.addProperty("expectSpeech", "true");
//            action.addProperty("wakeupType", "continuity");
            return action;
        }).collect(Collectors.toList());
        result.setActions(actions);

        return result;
    }

    private String buildText(String text) {
        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
        StringBuilder builder = new StringBuilder();
        List<String> actors = DplbotServiceUtil.seperateActor(text, pattern);
        String[] word = DplbotServiceUtil.seperateActorWord(text, pattern);
        int i = 0;
        for (String actor: actors) {
//            builder.append(actor).append("说:").append(word[i].replaceAll("\\p{P}", "")).append("。");
            builder.append(actor).append("说:").append(word[i].replaceAll("[^\\u4e00-\\u9fa5]", "")).append("。");
            i++;
        }
        if (builder.length() == 0) builder.append(text);
        return builder.toString();
    }

    private String getUserId(TaskQuery query) {
//        log.warn("requestData:{}", query.getRequestData());
        String userId = query.getRequestData().get("userOpenId");
        if (StringUtils.isBlank(userId)) {
            userId = query.getRequestData().get("deviceOpenId");
        }
        if (StringUtils.isBlank(userId)) log.error("天猫精灵无法获取到唯一标识的id");
        return PREFIX_TMALL+userId;
    }

    public void updateSpiritCsv() {
        Map<String, Boolean> botAccount2ItsCacheTemp = new HashMap<>();
        try {
            Map<String, Map<String, Map<String, String>>> recordMapTemp = new HashMap<>();
            Map<String, Map<String, Map<String, Map<String, String>>>> audioMapTemp = new HashMap<>();
            File[] csvFiles = new File(csvDic).listFiles((dir, name) -> StringUtils.endsWith(name, ".csv"));
            if (csvFiles != null && csvFiles.length != 0) {
                CSVParser reader;
                for (File file: csvFiles) {
                    String fileName = file.getName();
                    int i = fileName.indexOf("_");
                    String botAccount = fileName.substring(0, i);
                    boolean record = Integer.valueOf(fileName.substring(i+1, i+2)) == 1;
                    reader = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(
                            new FileInputStream(file), Charset.forName("UTF-8"))));
                    if (record) {
                        if (recordMapTemp.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, String>> npcName2nameId = recordMapTemp.computeIfAbsent(botAccount, s -> new HashMap<>());
                        int j = 0;
                        // "person", "mood", "md5", "id", "upUrl"
                        for (CSVRecord line: reader) {
                            if (j == 0) {
                                j++;
                                continue;
                            }
                            String npcName = line.get(0);
                            String md5 = line.get(2);
                            md5 = md5.substring(0, md5.indexOf("."));
                            String id = line.get(3);
                            Map<String, String> mp3Name2Id = npcName2nameId.computeIfAbsent(npcName, s -> new HashMap<>());
                            mp3Name2Id.put(md5, id);
                        }
                        botAccount2ItsCacheTemp.put(botAccount, true);
                    } else {
                        if (audioMapTemp.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, Map<String, String>>> person2toneMd5Id = audioMapTemp.computeIfAbsent(botAccount, s -> new HashMap<>());
                        int j = 0;
                        for (CSVRecord line: reader) {
                            if (j == 0) {
                                j++;
                                continue;
                            }
                            String person = line.get(0);
                            String mood = line.get(1);
                            String md5 = line.get(2);
                            if (StringUtils.endsWith(md5, ".wav")) continue;
                            md5 = md5.substring(0, md5.indexOf("."));
                            String id = line.get(3);
                            Map<String, Map<String, String>> tone2Md5Id = person2toneMd5Id.computeIfAbsent(person, s -> new HashMap<>());
                            Map<String, String> md52Id = tone2Md5Id.computeIfAbsent(mood, s -> new HashMap<>());
                            md52Id.put(md5, id);
                        }
                        botAccount2ItsCacheTemp.put(botAccount, false);
                    }
                    reader.close();
                }
                if (recordMapTemp.size() != 0) {
                    log.info("recordMapTemp:"+recordMapTemp.size());
                    recordMap = recordMapTemp;
                }
                if (audioMapTemp.size() != 0) {
                    log.info("audioMapTemp:"+audioMapTemp.size());
                    audioMap = audioMapTemp;
                }
            } else {
                log.warn("没有数据文件");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

        try {
            Map<String, Map<String, Map<String, String>>> childRecordMapTemp = new HashMap<>();
            Map<String, Map<String, Map<String, Map<String, String>>>> childAudioMapTemp = new HashMap<>();
            File[] csvFiles = new File(csvChildDic).listFiles((dir, name) -> StringUtils.endsWith(name, ".csv"));
            if (csvFiles != null && csvFiles.length != 0) {
                CSVParser reader;
                for (File file: csvFiles) {
                    String fileName = file.getName();
                    int i = fileName.indexOf("_");
                    String botAccount = fileName.substring(0, i);
                    boolean record = Integer.valueOf(fileName.substring(i+1, i+2)) == 1;
                    reader = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(
                            new FileInputStream(file), Charset.forName("UTF-8"))));
                    if (record) {
                        if (childRecordMapTemp.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, String>> npcName2nameId = childRecordMapTemp.computeIfAbsent(botAccount, s -> new HashMap<>());
                        int j = 0;
                        // "person", "mood", "md5", "id", "upUrl"
                        for (CSVRecord line: reader) {
                            if (j == 0) {
                                j++;
                                continue;
                            }
                            String npcName = line.get(0);
                            String md5 = line.get(2);
                            md5 = md5.substring(0, md5.indexOf("."));
                            String id = line.get(3);
                            Map<String, String> mp3Name2Id = npcName2nameId.computeIfAbsent(npcName, s -> new HashMap<>());
                            mp3Name2Id.put(md5, id);
                        }
                        botAccount2ItsCacheTemp.put(botAccount, true);
                    } else {
                        if (childAudioMapTemp.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, Map<String, String>>> person2toneMd5Id = childAudioMapTemp.computeIfAbsent(botAccount, s -> new HashMap<>());
                        int j = 0;
                        for (CSVRecord line: reader) {
                            if (j == 0) {
                                j++;
                                continue;
                            }
                            String person = line.get(0);
                            String mood = line.get(1);
                            String md5 = line.get(2);
                            if (StringUtils.endsWith(md5, ".wav")) continue;
                            md5 = md5.substring(0, md5.indexOf("."));
                            String id = line.get(3);
                            Map<String, Map<String, String>> tone2Md5Id = person2toneMd5Id.computeIfAbsent(person, s -> new HashMap<>());
                            Map<String, String> md52Id = tone2Md5Id.computeIfAbsent(mood, s -> new HashMap<>());
                            md52Id.put(md5, id);
                        }
                        botAccount2ItsCacheTemp.put(botAccount, false);
                    }
                    reader.close();
                }
                if (childRecordMapTemp.size() != 0) {
                    log.info("childRecordMapTemp:"+childRecordMapTemp.size());
                    childRecordMap = childRecordMapTemp;
                }
                if (childAudioMapTemp.size() != 0) {
                    log.info("childAudioMapTemp:"+childAudioMapTemp.size());
                    childAudioMap = childAudioMapTemp;
                }
            } else {
                log.warn("没有数据文件");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (botAccount2ItsCacheTemp.size() != 0) {
            log.info("botAccount2ItsCacheTemp:"+botAccount2ItsCacheTemp.size());
            botAccount2ItsCache = botAccount2ItsCacheTemp;
        }
    }

    public static void main(String[] args) {
        String s = "你好啊solution，怎么样了{/test/},今天~天～气,<br>, 还不►◄错，】很【好@";
        s = s.replaceAll("[^\\u4e00-\\u9fa5]", "");
        System.out.println(s);
    }
}
