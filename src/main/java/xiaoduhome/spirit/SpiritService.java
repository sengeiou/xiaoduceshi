package xiaoduhome.spirit;

import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import com.alibaba.da.coin.ide.spi.meta.Action;
import com.alibaba.da.coin.ide.spi.meta.AskedInfoMsg;
import com.alibaba.da.coin.ide.spi.meta.ExecuteCode;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.spirit.InitialData.*;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.audioMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.botAccount2ItsCache;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.childAudioMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.childRecordMap;
import static ai.qiwu.com.xiaoduhome.spirit.InitialData.recordMap;
import static ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService.LAST_OUTTIME_MARK;
import static ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService.OUTTIME_TYPE_0;

/**
 * 解析请求参数，构造向终端接口发送打数据打JSON，之后得到响应数据，提取构造返回给天猫的响应数据
 * @author 苗权威
 * @dateTime 2019/6/18
 */
@Service
@Slf4j
public class SpiritService {

//    @Value("${csvDic}")
    private String csvDic;

//    @Value("${csvChildDic}")
    private String csvChildDic;

    private static final String AB_REGEX = "\uD83C\uDD70(.*?)\uD83C\uDD71";

    private static final ConcurrentHashMap<String, TaskResult> LAST_OUTTIME_RESULT = new ConcurrentHashMap<>();

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
        if (StringUtils.isBlank(userId)) return buildTextTaskResult("不好意思好像出了什么问题，正在抓紧修复中", request.getIntentId());
        Long intentId = request.getIntentId();
        Byte waitType;
        if ((waitType=LAST_OUTTIME_MARK.remove(userId)) != null) {
            if (waitType < 2) {
                if (LAST_OUTTIME_RESULT.get(userId) == null) {
                    LAST_OUTTIME_MARK.put(userId, (byte) (waitType+1));
                    return buildTextTaskResult((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                            XiaoAiConstants.TTS.WAIT_WORD_2, intentId);
                }
                return LAST_OUTTIME_RESULT.remove(userId);
            }
        }
        String userWord = request.getUtterance();
//        log.info("天猫精灵用户说的话:"+userWord);
        try {
            Future<TaskResult> future = DplbotServiceUtil.getPOOL().submit(() -> requestTerminate(userId, userWord, type, intentId));
            return future.get(1700, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("天猫精灵出现问题:{}", ExceptionUtils.getStackTrace(e));
        } catch (TimeoutException e) {
            log.warn("超时");
            LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_0);
            return buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
        }
        return buildTextTaskResult("不好意思请再说一遍吧", intentId);
    }

    private TaskResult requestTerminate(String userId, String userWord, Integer type, Long intentId) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
//        requestPojo.setAccess(XiaoAiConstants.DEVICE_NAME);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        //log.info("向终端发出请求的参数信息:{}", requestJsonStr);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        String channelId;
        switch (type) {
            case 2:
            case 4: channelId = "tianmao-jiaoyou-audio-child-test";break;
            case 3:
            case 5: channelId= "tianmao-jiaoyou-audio-adult-test";break;
            default: channelId = "tianmao-jiaoyou-audio-test";
        }
        headers.put("App-Channel-Id", channelId);
        String resStr= OkHttp3Utils.doPostJsonStr(XiaoAiConstants.QI_WU_TERMINATE_DIDI, requestJsonStr, headers);;
        if (resStr == null) {
            return null;
        }
//        log.info("天猫精灵晓悟返回:"+resStr);
        TaskResult taskResult = null;
        try {
            final SpiritTerminateResponse response = JSON.parseObject(resStr, SpiritTerminateResponse.class);
//            Boolean recommand = response.getAipioneerUsername().equals(channelId2RecommendBotAcc.get(channelId));
            if (CollectionUtils.isEmpty(response.getDialogs())) {
                taskResult = buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
                LAST_OUTTIME_RESULT.put(userId, taskResult);
                return taskResult;
            }
            List<String> list = dealResponse(response);
            if (CollectionUtils.isEmpty(list)) {
//                DplbotServiceUtil.getPool().submit(() -> {
//                    List<Dialog> noAudioDialogs = dealNoAudio(response);
//                    if (!CollectionUtils.isEmpty(noAudioDialogs)) {
//                        OkHttp3Utils.postAsync("http://didi-gz2.jiaoyou365.com:8188/spirit/audio/no", JSON.toJSONString(noAudioDialogs));
//                    }
//                });
                StringBuilder builder = new StringBuilder();
                response.getDialogs().forEach(dialog -> builder.append(buildText(dialog.getText())));
                if (builder.length() != 0) taskResult = buildTextTaskResult(builder.toString(), intentId);
            } else {
                taskResult = buildAudioTaskResult(list, intentId);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (taskResult == null) {
            taskResult = buildTextTaskResult("不好意思晓悟没有听清您刚才说的话,请再重复一遍吧", intentId);
        }
        LAST_OUTTIME_RESULT.put(userId, taskResult);
        return taskResult;
    }

    private List<Dialog> dealNoAudio(SpiritTerminateResponse response) {
        return response.getDialogs().stream().filter(dialog -> {
            String botAccount = dialog.getBotAccount();
            Boolean record = botAccount2ItsCache.get(botAccount);
            if (record == null) return true;
            else if (record) {
                return InitialData.getRecordId(botAccount, dialog.getNpcName(), dialog.getText(), 5) == null;
            } else {
                return InitialData.getAudioId(botAccount, dialog.getPerson(), dialog.getTone(), dialog.getText(), 5) == null;
            }
        }).collect(Collectors.toList());
    }

    private List<String> dealResponse(SpiritTerminateResponse response) {
        List<String> idList = new ArrayList<>();
        List<Dialog> dialogs = response.getDialogs();
        String botAccount, npcName, id, person, mood;
        for (Dialog dialog: dialogs) {
            botAccount = dialog.getBotAccount();
            Boolean record = botAccount2ItsCache.get(botAccount);
            if (record == null) return null;
            else if (record) {
                npcName = dialog.getNpcName();
                Collection<String> ls = dealWithSep(dialog, true, botAccount, npcName, null);
                if (CollectionUtils.isEmpty(ls)) {
                    id = InitialData.getRecordId(botAccount, npcName, dialog.getText(), 5);
                    if (StringUtils.isNotBlank(id)) idList.add(id);
                    else return null;
                } else {
                    idList.addAll(ls);
                }
            } else {
                person = dialog.getPerson();
                mood = dialog.getTone();
                Collection<String> ls = dealWithSep(dialog, false, botAccount, person, mood);
                if (CollectionUtils.isEmpty(ls)) {
                    id = InitialData.getAudioId(botAccount, person, mood, dialog.getText(), 5);
                    if (StringUtils.isNotBlank(id)) idList.add(id);
                    else return null;
                } else {
                    idList.addAll(ls);
                }
            }
        }
        return idList;
    }

    private Collection<String> dealWithSep(Dialog dialog, Boolean record, String botAccount,
                                           String porn, String mood) {
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
                    id = InitialData.getRecordId(botAccount, porn, notVariableText, 5);
                } else {
                    id = InitialData.getAudioId(botAccount, porn, mood, notVariableText, 5);
                }
                if (StringUtils.isBlank(id)) return null;
                combineMp3Text2FilePath.put(notVariableText, id);
            }
            for (String variableText: variableTextList) {
                String id;
                if (record) {
                    id = InitialData.getRecordId(botAccount, porn, variableText, 5);
                } else {
                    id = InitialData.getAudioId(botAccount, porn, mood, variableText, 5);
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

    private TaskResult buildAudioTaskResult(List<String> ids, Long intentId) {
        TaskResult result = new TaskResult();
        result.setReply("叮咚");
        result.setResultType(ResultType.ASK_INF);
        result.setExecuteCode(ExecuteCode.SUCCESS);

        result.setAskedInfos(Collections.singletonList(new AskedInfoMsg("any", intentId)));

        List<Action> actions = ids.stream().map(s -> {
            Action action = new Action("audioPlayGenieSource");
            action.addProperty("audioGenieId", s);
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
            builder.append(actor).append("说:").append(word[i].replaceAll("\\p{P}", "")).append("。");
            i++;
        }
        if (builder.length() == 0) builder.append(text);
        return builder.toString();
    }

    private String getUserId(TaskQuery query) {
        log.warn("requestData:{}", query.getRequestData());
        String userId = query.getRequestData().get("userOpenId");
        if (StringUtils.isBlank(userId)) {
            userId = query.getRequestData().get("deviceOpenId");
        }
        if (StringUtils.isBlank(userId)) log.error("天猫精灵无法获取到唯一标识的id");
        return userId;
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
//        System.out.println(buildText("【小悟@正常】推荐如下作品：《小狗小白》、《妖刀异闻录》、《星球问答》 。你可以说：“打开某作品”"));
//        System.out.println(buildText("不好意思请在说一遍吧"));
//        System.out.println(buildText("【齐齐@开心】小朋友真聪明。他们循声来到一颗大榕树后的一座木屋旁，<br>见到音乐家捧着一个像笛子一样的乐器正在入神地吹奏着。后来，音乐家很高兴接纳了他们，并在木屋旁边给他们安了新家，<br>四个小伙伴和善良的音乐家开心地住在水心湖边，再也不用担惊受怕了。每天和音乐家一起弹奏唱跳，认识他们的人都亲切地称他们为“水心湖的音乐家”。【齐齐@正常】好了，小朋友，今天的故事就到这里了，谢谢你的耐心听讲，再见！【晓悟@正常】已退出当前作品。【小悟@正常】推荐如下作品：《小神童闯关》、《遇见伽利略》、《会数学的杰瑞》 。你可以说：“打开某作品”"));
//        System.out.println(buildText("【皮皮@正常】不好，我们在这里待得太久了，时空机要把我们带到下个地方去了【旁白@正常】随着时光穿梭机的强制转移，你和皮皮来到了动物园，动物园里有老虎，狮子，大象，还有几只大熊猫<br>【皮皮@正常】<br>我们好像被转移到动物园了。。。哇小朋友，你看！那里有熊猫耶，你喜欢熊猫吗？ 。"));
    }
}
