package com.itsu.itsutoken.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.util.ServletUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @Autowired
    private ItsuTokenProperties properties;

    @RequestMapping("#{itsuTokenProperties.webRegister.registerUrl == null ? 'registerToken' : itsuTokenProperties.webRegister.registerUrl}")
    public String idx() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:script/login.html");
        StringBuffer stringBuffer = new StringBuffer();
        try (InputStreamReader isr = new InputStreamReader(resource.getInputStream());
                BufferedReader br = new BufferedReader(isr)) {
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer.toString();
    }

    @PostMapping("/tokenLogin")
    public String login(String user, String password) {
        if (properties.getWebRegister().getUser().equals(user)
                && properties.getWebRegister().getPassword().equals(password)) {
            ServletUtil.getSession().setAttribute("login", "yes");
            return "redirect:registerToken.html";
        } else {
            ServletUtil.getResponse().setStatus(401);
            return "Authorization error";
        }
    }
}