package com.cow;

import com.cow.LangchainDemoApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.cow.dao")
public class BreadRollMallServer {
    private static final Logger log = LoggerFactory.getLogger(BreadRollMallServer.class);

        public static void test(String[] args) {
        try {
            startApplication(LangchainDemoApplication.class, "spring.jmx.default-domain=Mall", "com.sun.management.jmxremote.port=8082");
            startApplication(BreadRollMallServer.class, "spring.jmx.default-domain=langchain", "com.sun.management.jmxremote.port=8003");
        } catch (Exception e) {
            log.error("Application startup failed", e);
        }
    }

    private static void startApplication(Class<?> applicationClass, String... properties) {
        log.info("Starting application: {}", applicationClass.getName());
        new SpringApplicationBuilder(applicationClass)
                .properties(properties)
                .run();
    }
    public static void main(String[] args){
        SpringApplication.run(LangchainDemoApplication.class, args);
    }
}

