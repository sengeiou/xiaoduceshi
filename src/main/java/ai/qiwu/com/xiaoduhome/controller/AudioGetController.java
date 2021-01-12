package ai.qiwu.com.xiaoduhome.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author 苗权威
 * @dateTime 19-12-13 下午4:32
 */
@RestController
@Slf4j
public class AudioGetController {

//    @RequestMapping("/audio/get")
//    public void audioGet(String id, String mark, HttpServletResponse response) {
//        String type = id.substring(0, id.indexOf("_"));
//        AudioBoxData data = null;
//        switch (type) {
//            case "XIAODU": data = DplBotService.XIAODU_MP3_PATH.get(id); break;
//            case "XIAOAI": data = XiaoAiServiceTemp.XIAOAI_MP3_PATH.get(id); break;
//        }
//        if (data != null && mark.equals(data.getMark())) {
//            buildMp3Response(response, data.getPath());
//        } else {
//            try {
//                Thread.sleep(2000);
//            } catch (Exception e){}
//            switch (type) {
//                case "XIAODU": data = DplBotService.XIAODU_MP3_PATH.get(id); break;
//                case "XIAOAI": data = XiaoAiServiceTemp.XIAOAI_MP3_PATH.get(id); break;
//            }
//            if (data != null && mark.equals(data.getMark())) {
//                buildMp3Response(response, data.getPath());
//            } else {
//                log.error("没有获取到音频");
//            }
//        }
//    }

//    @GetMapping("/audio/name/change")
//    public void changeName(Integer ) {
//
//    }

    private void buildMp3Response(HttpServletResponse response, String fileName) {
        response.setStatus(200);
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())){
            response.reset();
            response.setContentType("audio/mp3");
            response.setHeader("Content-Type", "audio/mp3");
            byte[] buffer = new byte[1024];
            int ln;
            while ((ln = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, ln);
            }
            out.flush();
        } catch (ClientAbortException e) {

        } catch (IOException e) {
            log.error("buildMp3Response:写入音频流到HttpServletResponse失败:{}", e.toString());
        }
    }
}
