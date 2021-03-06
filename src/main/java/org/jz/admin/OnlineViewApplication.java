package org.jz.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class OnlineViewApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineViewApplication.class, args);
    }


}
