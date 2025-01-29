package com.nhnacademy.blog.web.blog;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog/{blog-fid}")
public class BlogIndexController {

    @GetMapping(value = {"/","/index.do"})
    public String index(Model model, @PathVariable("blog-fid") String blogFid) {
        return "blog/index";
    }
}
