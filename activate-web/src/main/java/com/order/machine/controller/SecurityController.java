package com.order.machine.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miou
 * @date 2019-05-08
 */
@RestController
public class SecurityController {

    @PostMapping(value = "/test/anonymous")
    public void anonymous(){
        System.out.println("anonymous");
    }
}
