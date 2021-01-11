package xiaoduhome.common.config;

import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 苗权威
 * @dateTime 19-11-18 下午5:51
 */
//@Configuration
public class WebSocketConfig {

//    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
