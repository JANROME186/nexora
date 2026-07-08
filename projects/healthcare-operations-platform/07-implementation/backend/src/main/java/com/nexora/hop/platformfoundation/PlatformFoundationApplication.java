package com.nexora.hop.platformfoundation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@Modulithic
@SpringBootApplication
public class PlatformFoundationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformFoundationApplication.class, args);
    }
}
