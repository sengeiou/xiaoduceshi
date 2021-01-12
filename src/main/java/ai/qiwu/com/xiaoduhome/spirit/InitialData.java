package ai.qiwu.com.xiaoduhome.spirit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-12-11 下午4:15
 */
//@Component
@Slf4j
public class InitialData implements InitializingBean {
//    @Value("${csvDic}")
    private String csvDic;

//    @Value("${csvChildDic}")
    private String csvChildDic;

    static volatile Map<String, Map<String, Map<String, String>>> recordMap;
    static volatile Map<String, Map<String, Map<String, String>>> childRecordMap;
    static volatile Map<String, Map<String, Map<String, Map<String, String>>>> audioMap;
    static volatile Map<String, Map<String, Map<String, Map<String, String>>>> childAudioMap;
    static volatile Map<String, Boolean> botAccount2ItsCache;

    static {
        recordMap = new HashMap<>();
        audioMap = new HashMap<>();
        childRecordMap = new HashMap<>();
        childAudioMap = new HashMap<>();
        botAccount2ItsCache = new HashMap<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
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
                        if (recordMap.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, String>> npcName2nameId = recordMap.computeIfAbsent(botAccount, s -> new HashMap<>());
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
                        botAccount2ItsCache.put(botAccount, true);
                    } else {
                        if (audioMap.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, Map<String, String>>> person2toneMd5Id = audioMap.computeIfAbsent(botAccount, s -> new HashMap<>());
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
                        botAccount2ItsCache.put(botAccount, false);
                    }
                    reader.close();
                }
            } else {
                log.warn("没有数据文件");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

        try {
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
                        if (childRecordMap.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, String>> npcName2nameId = childRecordMap.computeIfAbsent(botAccount, s -> new HashMap<>());
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
                        botAccount2ItsCache.put(botAccount, true);
                    } else {
                        if (childAudioMap.containsKey(botAccount)) {
                            log.error("重复:{}",fileName);
                            continue;
                        }
                        Map<String, Map<String, Map<String, String>>> person2toneMd5Id = childAudioMap.computeIfAbsent(botAccount, s -> new HashMap<>());
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
                        botAccount2ItsCache.put(botAccount, false);
                    }
                    reader.close();
                }
            } else {
                log.warn("没有数据文件");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    static String getRecordId(String botAccount, String npcName, String text, int type) {
        Map<String, Map<String, String>> npcName2Md5Id;
        if (type == 4) {
            npcName2Md5Id = childRecordMap.get(botAccount);
        } else {
            npcName2Md5Id = recordMap.get(botAccount);
        }
        if (npcName2Md5Id != null) {
            Map<String, String> md52Id = npcName2Md5Id.get(npcName);
            if (md52Id != null) {
                return md52Id.get(buildMd5Name(text));
            }
        }
        return null;
    }

    static String getAudioId(String botAccount, String person, String mood, String text, int type) {
        Map<String, Map<String, Map<String, String>>> person2MoodMd5Id;
        if (type == 4) {
            person2MoodMd5Id = childAudioMap.get(botAccount);
        } else {
            person2MoodMd5Id = audioMap.get(botAccount);
        }
        if (person2MoodMd5Id != null) {
            Map<String, Map<String, String>> mood2Md5Id = person2MoodMd5Id.get(person);
            if (mood2Md5Id != null) {
                Map<String, String> md52Id = mood2Md5Id.get(mood);
                if (md52Id != null) {
                    return md52Id.get(buildMd5Name(text));
                }
            }
        }
        return null;
    }

    private static String buildMd5Name(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        System.out.println(new File("/home/yf01/下载/out.mp3").getName());
    }
}
