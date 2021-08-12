package kr.or.uknowhat.api.framework.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Value("${hkmc.swagger.host:}")
    private String swaggerHost;

    @Value("${hkmc.swagger.pathMapping:}")
    private String swaggerPathMapping;

    @Value("${hkmc.swagger.basePackage:}")
    private String swaggerbasePackage;

    @Bean
    public Docket api() {
        if (StringUtils.isEmpty(swaggerbasePackage)) {
            swaggerbasePackage = "kr.or.uknowhat.api";
        }
        System.out.println("SwaggerConfig swaggerbasePackage is : "+swaggerbasePackage);
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerbasePackage))
                .paths(PathSelectors.ant("/api/v1/**"))
                .build();

        if (!StringUtils.isEmpty(swaggerHost) || !StringUtils.isEmpty(swaggerPathMapping)) {
            String hostPath = "";
            if (!StringUtils.isEmpty(swaggerHost)) {
                hostPath = swaggerHost;
            }
            if (!StringUtils.isEmpty(swaggerPathMapping)) {
                hostPath += swaggerPathMapping;
            }
            docket.host(hostPath);
        }
        return docket;
    }

}
