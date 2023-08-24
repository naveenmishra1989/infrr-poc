package com.infrr.rest.infrrpoc;

import com.infrr.rest.infrrpoc.config.FileStorageProperties;
import com.infrr.rest.infrrpoc.service.FileStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Log4j2
@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class InfrrPocApplication {

    @Autowired
    private FileStorageService fileStorageService;

    public static void main (String[] args) {
        SpringApplication.run(InfrrPocApplication.class, args);
    }
}

