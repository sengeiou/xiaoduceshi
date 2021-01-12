package ai.qiwu.com.xiaoduhome.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author 苗权威
 * @dateTime 19-7-30 下午2:40
 */
@Slf4j
public class Config {
    private static Properties properties;

    static {
        String filename = "xiaodu.properties";
        properties = new Properties();
        try {
            String path = System.getProperty("java.class.path");
            path = path.substring(0, path.lastIndexOf(File.separator)+1) + filename;
            log.warn("配置文件:{}",path);
//            path = "/home/yf01/文档/xiaodu.properties";
            properties.load(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
//            properties.load(new InputStreamReader(Config.class.getClassLoader().getResourceAsStream(filename), StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("获取配置文件对象出错:{}", e.toString());
            System.exit(1);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) return null;
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) return defaultValue;
        return value.trim();
    }
}
