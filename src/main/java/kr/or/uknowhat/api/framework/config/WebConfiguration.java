package kr.or.uknowhat.api.framework.config;

import java.io.IOException;

//import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;

//import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfiguration implements WebMvcConfigurer{
	private static final String[] RESOURCE_LOCATIONS = {
	        //"classpath:/static/","classpath:/resources/" ,"classpath:/resources/static/"
			"classpath:/static/"
	        };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
        .addResourceHandler("/**")
        .addResourceLocations(RESOURCE_LOCATIONS)
        .setCachePeriod(3600)
        .resourceChain(true)
        .addResolver(new PathResourceResolver() { 
        	@Override 
        	protected Resource getResource(String resourcePath, Resource location) throws IOException {
        		Resource requestedResource = location.createRelative(resourcePath); 
        		return requestedResource.exists() && requestedResource.isReadable() ? 
        				requestedResource : new ClassPathResource("/static/index.html"); 
        		} 
        	});

    }
}