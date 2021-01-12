package ai.qiwu.com.xiaoduhome.service.chatsLog;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 苗权威
 * @dateTime 19-12-30 上午10:59
 */
@Service
@Slf4j
public class ChatLogs {
    private static final String PATH = "/root/programsCN/chats.log";

    public String look(Integer row, String typeCount, String type) {
        if (row == null || row == 0) row = 1000;
        try {
            LogResponse response = readLastRows(PATH, StandardCharsets.UTF_8, row);
            if (StringUtils.isNotBlank(response.getData())) {
                StringBuilder builder = new StringBuilder("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                        "    <title>Title</title>\n" +
                        "</head>\n" +
                        "<body>");
//                StringBuilder builder = new StringBuilder();
                if (StringUtils.isNotBlank(type)) {
                    if (StringUtils.isBlank(typeCount)) typeCount = "100";
                    int count = Integer.parseInt(typeCount);
                    String[] arrTest = response.getData().split("\n\n");
                    List<String> list = Arrays.stream(arrTest).filter(s1 -> s1.contains(type))
                            .map(s1 -> {
                                String[] arr = s1.split("\n");
                                return Arrays.stream(arr).map(s -> "<span>"+s+"</span><br>").collect(Collectors.joining(""));
//                                return StringUtils.join(arr, "");
                            }).collect(Collectors.toList());
                    Collections.reverse(list);
                    List<String> result = new ArrayList<>(list);
                    int recur = 0;
                    while (result.size() < count && response.getIndex() > 0 && recur < 200) {
                        response = readLastRowsWithIndex(PATH, StandardCharsets.UTF_8, response.getIndex(), 800);
                        arrTest = response.getData().split("\n\n");
                        list = Arrays.stream(arrTest).filter(s1 -> s1.contains(type)).map(s -> {
                            String[] arr = s.split("\n");
                            return Arrays.stream(arr).map(s1 -> "<span>"+ s1 +"</span><br>").collect(Collectors.joining(""));
                        }).collect(Collectors.toList());
                        if (list.size() != 0) {
                            Collections.reverse(list);
                            result.addAll(list);
                        }
                        recur++;
                    }
                    result.forEach(s -> builder.append("<span>").append(s).append("</span><br>"));
                } else {
                    String[] arr = response.getData().split("\n");
                    List<String> list = Arrays.asList(arr);
                    Collections.reverse(list);
                    list.forEach(s -> builder.append("<span>").append(s).append("</span><br>"));
//                    Arrays.stream(arr).forEach(s1 -> builder.append("<span>").append(s1).append("</span><br>"));
                }
                builder.append("</body>\n" +
                        "</html>");
                return builder.toString();
//                List<String> list = Arrays.stream(arr).map(s1 -> "<span>"+s1+"</span><br>").collect(Collectors.toList());
//                s = StringUtils.join(list, "");
            }
//            s = s.replaceAll("\\{/.*/}", "");
//            WebSocketLog.sendInfo(s, "chat");
        } catch (Exception e) {
            log.error("ChatLogs:"+e.toString());
        }
        return null;
    }

    private static LogResponse readLastRows(String filename, Charset charset, int rows) throws IOException {
        charset = charset == null ? Charset.defaultCharset() : charset;
        String lineSeparator = System.getProperty("line.separator");
        try (RandomAccessFile rf = new RandomAccessFile(filename, "r")) {
            // 每次读取的字节数要和系统换行符大小一致
            byte[] c = new byte[lineSeparator.getBytes().length];
            // 在获取到指定行数和读完文档之前,从文档末尾向前移动指针,遍历文档每一个字节
            for (long pointer = rf.length(), lineSeparatorNum = 0; pointer >= 0; rf.seek(pointer--)) {
                // 移动指针
                rf.seek(pointer);
                // 读取数据
                int readLength = rf.read(c);
                if (readLength != -1 && new String(c, 0, readLength).equals(lineSeparator) && ++lineSeparatorNum == rows) {
                    // 找到足够数量换行符则退出循环
                    break;
                }
            }
            LogResponse response = new LogResponse();
            long index = rf.getFilePointer();
            response.setIndex(index);
            byte[] tempbytes = new byte[(int) (rf.length() - index)];
            rf.readFully(tempbytes);
            response.setData(new String(tempbytes, charset));
            return response;
        }
    }

    private static LogResponse readLastRowsWithIndex(String filename, Charset charset, long start, int rows) throws IOException {
        charset = charset == null ? Charset.defaultCharset() : charset;
        String lineSeparator = System.getProperty("line.separator");
        try (RandomAccessFile rf = new RandomAccessFile(filename, "r")) {
            // 每次读取的字节数要和系统换行符大小一致
            byte[] c = new byte[lineSeparator.getBytes().length];
            // 在获取到指定行数和读完文档之前,从文档末尾向前移动指针,遍历文档每一个字节
            for (long pointer = start, lineSeparatorNum = 0; pointer >= 0; rf.seek(pointer--)) {
                // 移动指针
                rf.seek(pointer);
                // 读取数据
                int readLength = rf.read(c);
                if (readLength != -1 && new String(c, 0, readLength).equals(lineSeparator) && ++lineSeparatorNum == rows) {
                    // 找到足够数量换行符则退出循环
                    break;
                }
            }
            LogResponse response = new LogResponse();
            long index = rf.getFilePointer();
            response.setIndex(index);
            byte[] tempbytes = new byte[(int) (start-index)];
            rf.readFully(tempbytes);
            response.setData(new String(tempbytes, charset));
            return response;
        }
    }
}
