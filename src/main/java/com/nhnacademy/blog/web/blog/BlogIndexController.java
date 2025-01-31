package com.nhnacademy.blog.web.blog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO#8 구현 1순위 - 사용자의 블로그를 구현 합니다.
 */
@Controller
@RequestMapping("/blog/{blog-fid}")
public class BlogIndexController {

    @GetMapping(value = {"","/","/index.do"})
    public String index(Model model, @PathVariable("blog-fid") String blogFid) {
        return "blog/index";
    }
}