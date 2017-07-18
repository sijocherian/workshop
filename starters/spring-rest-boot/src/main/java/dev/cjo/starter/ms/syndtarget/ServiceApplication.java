package dev.cjo.starter.ms.syndtarget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author sijocherian
 */
@SpringBootApplication
@ComponentScan(basePackages = { "dev.cjo.starter.ms.syndtarget" })
//@EnableDiscoveryClient

//@EnableAutoConfiguration
public class ServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceApplication.class, args);

    }


}