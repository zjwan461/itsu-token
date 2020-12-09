package com.itsu.itsutoken.controller;

import java.io.IOException;

import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.util.ServletUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.io.IoUtil;

@RestController
@ConditionalOnBean(value = ItsuTokenProperties.class)
public class BaseController {

    @Autowired
    private ItsuTokenProperties properties;

    @RequestMapping("#{itsuTokenProperties.webRegister.registerUrl}")
    public String registerPage() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:html/registerToken.html");
        return IoUtil.read(resource.getInputStream(), "UTF-8");
    }

    @PostMapping("/tokenLogin")
    public String login(String user, String password) {
        if (properties.getWebRegister().getUser().equals(user)
                && properties.getWebRegister().getPassword().equals(password)) {
            ServletUtil.login();
            return properties.getWebRegister().getRegisterUrl();
        } else {
            ServletUtil.getResponse().setStatus(401);
            return "Authorization error";
        }
    }

    @GetMapping("/login.html")
    public String toLogin() throws Exception {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:html/login.html");
        return IoUtil.read(resource.getInputStream(), "UTF-8");
    }

    @GetMapping("/tokenListUrl")
    public String listUrl() {
        return properties.getWebRegister().getTokenListUrl();
    }

    @GetMapping("/tokenRegisterUrl")
    public String listRegisterUrl() {
        return properties.getWebRegister().getRegisterUrl();
    }

    @GetMapping("#{itsuTokenProperties.webRegister.tokenListUrl}")
    public String tokenListPage() throws Exception {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:html/tokenList.html");
        return IoUtil.read(resource.getInputStream(), "UTF-8");
    }

}