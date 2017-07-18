package dev.cjo.starter.ms.syndtarget.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Doc generated at http://localhost:9091/swagger-ui.html#/
 * @author sijocherian
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dev.cjo.starter.ms.syndtarget"))
                .paths(PathSelectors.any())
                .build();
    }

/*    private ApiInfo metaData() {
        ApiInfo apiInfo = new ApiInfo(
                "Test Microservices");

        return apiInfo;

    }*/
}
