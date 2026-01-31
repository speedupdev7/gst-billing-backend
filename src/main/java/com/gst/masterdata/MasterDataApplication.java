package com.gst.masterdata;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
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
