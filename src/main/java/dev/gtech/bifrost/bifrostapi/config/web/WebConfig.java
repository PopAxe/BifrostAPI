package dev.gtech.bifrost.bifrostapi.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {

    @Autowired
    public void configureDispatcherServlet(DispatcherServlet dispatcherServlet) {
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }
}
