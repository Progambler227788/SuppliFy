package com.supplify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**","/templates/**")
                .addResourceLocations(
                		"classpath:/static/",
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/images/",
                        "file:src/main/resources/static/uploads/"
                    )
                    .setCachePeriod(3600)
                    .resourceChain(true);
        
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/templates/**")
                .resourceChain(false); // No caching for templates
        
        registry.addResourceHandler("/sellerDocumentImages/**")
        .addResourceLocations(
            "file:./src/main/resources/static/sellerDocumentImages/",
            "file:./target/classes/static/sellerDocumentImages/",
            "classpath:/static/sellerDocumentImages/"
        );


    }
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // Allow all origins
                .withSockJS();
    }
}
