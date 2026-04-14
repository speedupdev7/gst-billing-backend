package com.gst.masterdata;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "com.gst")
@EntityScan(basePackages = "com.gst")
@EnableJpaRepositories(basePackages = "com.gst")
public class MasterDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterDataApplication.class, args);
    }

    @PostConstruct
    public void checkTz() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        System.out.println("JVM TZ = " + TimeZone.getDefault().getID());
    }


}
