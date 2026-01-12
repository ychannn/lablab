package org.ychan.lablab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.ychan.lablab.mapper")
public class LablabApplication {

    public static void main(String[] args) {
        SpringApplication.run(LablabApplication.class, args);
        System.out.println("****************************启动成功！！！*********************************************");
    }

}
