package com.zhangbaowei.codissessiondemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by  zhangbaowei on 2018/8/15 8:25.
 */
@RestController
public class SessionController {
    @GetMapping("/s/get")
    public String Get(String name, HttpServletRequest rquest) {
        return (String) rquest.getSession().getAttribute(name);
    }


    @GetMapping("/s/set")
    public String Set(String name, String value, HttpServletRequest rquest) {
        rquest.getSession().setAttribute(name, value);
        return "OK";
    }
}
