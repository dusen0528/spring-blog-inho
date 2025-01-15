package com.nhnacademy.blog.web.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping(value = {"/","/index.do"})
    public String index() {
        return "index/index";
    }

}
