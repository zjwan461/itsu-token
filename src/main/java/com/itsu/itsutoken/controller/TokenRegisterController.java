package com.itsu.itsutoken.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "itsu-token.web-register", havingValue = "true", matchIfMissing = false)
public class TokenRegisterController {

}