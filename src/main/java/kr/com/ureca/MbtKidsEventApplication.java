package kr.com.ureca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MbtKidsEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbtKidsEventApplication.class, args);
    }

}
