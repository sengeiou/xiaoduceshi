package xiaoduhome.common.config;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 文件上传控制，设置编码格式，设置最大文件大小
 * @author 苗权威
 * @dateTime 19-9-7 下午6:35
 */
@Component("multipartResolver")
public class MultipartResolver extends CommonsMultipartResolver {
    /**
     * 设置编码格式,UTF-8
     * @param defaultEncoding
     */
    @Override
    public void setDefaultEncoding(String defaultEncoding) {
        super.setDefaultEncoding("UTF-8");
    }

    /**
     * 设置最大文件大小,500M
     * @param maxUploadSizePerFile
     */
    @Override
    public void setMaxUploadSizePerFile(long maxUploadSizePerFile) {
        super.setMaxUploadSizePerFile(524288000);
    }
}
