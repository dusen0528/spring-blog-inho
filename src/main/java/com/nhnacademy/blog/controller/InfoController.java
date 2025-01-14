package com.nhnacademy.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * TODO#8 - spring mvc에서는 controller는 @Controller annotation 선언하면 됩니다.
 * - servlet에서 처럼 command interface를 구현하듯이 별도의 인터페이스를 구현할 필요 없습니다.
 * - @Controller annotation을 선언 합니다.
 */
@Controller
public class InfoController {
    /* TODO#9 - spring mvc에서는 servlet에서 직접 구현해서 사용했던 @RequestMapping annotation을 제공 합니다.
          @RequestMapping annotation을 선언하세요.
          - method = RequestMethod.GET, value = "/info.do"
     */
    @RequestMapping(method = RequestMethod.GET, value = "/info.do")
    public String info() {
        /**
         * TODO#10 - viewName을 반환 합니다.
         * - thymeleaf를 사용하고 있음으로 "info"를 리턴 하면 /resources/templates/info.html 맵핑 됩니다.
         * - servlet 기반으로 쇼핑몰 개발시 viewResolver 구현했던것을 처럼 .. application.properties에 설정한 prefix, suffix 기반으로 맵핑 됩니다.
         * - http://localhost:8080/info.do 연결되는지 테스트 합니다.
         * - http://localhost:8080/info.html <-- 직접  info.html을 호출하면 어떻게 되는지 테스트 합니다.
         */
        return "info";
    }

}
