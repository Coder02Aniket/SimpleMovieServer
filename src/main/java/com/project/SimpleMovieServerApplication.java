package com.project;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleMovieServerApplication {

    public static void main(String[] args) {
        ApplicationContext context =  SpringApplication.run(SimpleMovieServerApplication.class, args);

    }

    @Bean
    public TomcatServletWebServerFactory containerFactory(){
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(
            connector -> ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1)
        );
        return factory;
    }

}
