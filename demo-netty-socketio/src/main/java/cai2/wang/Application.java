package cai2.wang;

import cai2.wang.common.annotation.EnableSocketio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangpeixu
 * @date 2024/11/1 17:08
 */
@EnableSocketio
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}