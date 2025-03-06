package com.nhnacademy.blog.web.index;

import com.nhnacademy.blog.topic.dto.TopicResponse;
import com.nhnacademy.blog.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * - TODO#9 구현 2순위 - 블로그의 Index 페이지를 구현 합니다.
 */

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {
    private final TopicService topicService;

    @ModelAttribute("rootTopics")
    public List<TopicResponse> rootTopics() {
        List<TopicResponse> rootTopics =  topicService.getRootTopics();
        return rootTopics;
    }

    @ModelAttribute("subTopics")
    public List<TopicResponse> subTopics(@RequestParam(value = "topic_id", required = false) Integer topicId) {
        if(Objects.isNull(topicId)) {
            return Collections.emptyList();
        }
        List<TopicResponse> subTopics =  topicService.getSubTopics(topicId);
        log.debug("subTopics: {}", subTopics);
        return subTopics;
    }

    @GetMapping(value = {"/","/index.do"})
    public String index(
            Model model,
            @RequestParam(value = "topic_id", required = false) Integer topicId,
            @RequestParam(value = "topic_pid", required = false) Integer topicPid
    ) {
        model.addAttribute("topicId", topicId);
        model.addAttribute("topicPid", topicPid);
        return "index/index";
    }


}