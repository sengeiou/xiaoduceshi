package ai.qiwu.com.xiaoduhome.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author 苗权威
 * @dateTime 19-9-4 下午2:13
 */
@Controller
//@RequestMapping("/xiaoduhome/img")
public class UpLoadImgController {

//    @GetMapping("/test/terminate")
//    @ResponseBody
    public void testTerminate() {
        long start = System.currentTimeMillis();
        try {
            System.out.println("sleep start:"+start);
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sleep end:"+(System.currentTimeMillis()-start)/1000);
    }


//    @GetMapping("/up")
    public String uploadImage(ModelMap modelMap) {
        return "FileUpload";
    }

//    @PostMapping("/up")
//    @ResponseBody
    public void loadImage(MultipartFile file) {
        File imageFile = new File("/home/dc2-user/projects/audiobox/"+file.getOriginalFilename());
        try {
            if (!imageFile.getParentFile().exists()) {
                imageFile.mkdirs();
            }
            file.transferTo(imageFile.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @GetMapping("/down")
//    @ResponseBody
    public void download(String fileName, HttpServletResponse response) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())){
            response.reset();
            int length = inputStream.available();
            response.setHeader("Content-Length", String.valueOf(length));
            String loadName = fileName.substring(fileName.lastIndexOf("/")+1).trim();
            if (StringUtils.isBlank(loadName)) {
                loadName = String.valueOf(System.currentTimeMillis());
            }
            String value = String.format("attachment;filename=\"%s\"", loadName);
            response.setHeader("Content-Disposition", value);
            byte[] buffer = new byte[1024];
            int ln;
            while ((ln = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, ln);
            }
            out.flush();
        } catch (Exception e) {
        }
    }
}
