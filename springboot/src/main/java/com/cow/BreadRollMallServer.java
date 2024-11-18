package com.cow;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.cow.dao")
public class BreadRollMallServer {
    private static final Logger log = LoggerFactory.getLogger(BreadRollMallServer.class);
    public static void main(String[] args){
        SpringApplication.run(BreadRollMallServer.class, args);
    }
}

