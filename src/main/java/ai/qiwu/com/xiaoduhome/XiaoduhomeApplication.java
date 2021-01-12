package ai.qiwu.com.xiaoduhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XiaoduhomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoduhomeApplication.class, args);
    }

}
