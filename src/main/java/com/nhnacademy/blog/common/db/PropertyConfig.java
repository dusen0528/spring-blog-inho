package com.nhnacademy.blog.common.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

@Slf4j
@Configuration
@SuppressWarnings("squid:S1118")
public class PropertyConfig {

    /** spring.profiles.active=test 기본설정
     * - spring.profiles.active는 Spring 프레임워크에서 애플리케이션의 활성화된 프로파일을 설정하는 데 사용되는 속성입니다.
     * 이 속성은 애플리케이션이 실행될 때 어떤 프로파일을 사용할지를 지정하며, 이를 통해 다양한 환경(예: 개발, 테스트, 프로덕션)별로 다른 설정을 적용할 수 있습니다.
     *
     *  - PropertySourcesPlaceholderConfigurer는 Spring 프레임워크에서 프로퍼티 파일을 로드하고,
     * 이를 통해 애플리케이션 컨텍스트에서 사용되는 프로퍼티 플레이스홀더 값을 대체하는 데 사용되는 빈입니다.
     * 주로 @PropertySource 애노테이션과 함께 사용되어 외부 프로퍼티 파일의 값을 Spring 애플리케이션 컨텍스트에 주입하는 역할을 합니다.
     *
     * @param environment Spring의 Environment 객체로 활성화된 프로파일 정보를 제공합니다.
     * @return PropertySourcesPlaceholderConfigurer 객체를 반환합니다.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer testProperties(Environment environment) {
        // 활성화된 프로파일 정보 로그 출력
        log.debug("profiles:{}", Arrays.stream(environment.getActiveProfiles()).toList());

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        String property;

        // 활성화된 프로파일에 따라 로드할 프로퍼티 파일 결정
        if(Arrays.stream(environment.getActiveProfiles()).toList().contains("test")){
            property = "application-test.properties";
        } else if(Arrays.stream(environment.getActiveProfiles()).toList().contains("prod")){
            property = "application-prod.properties";
        } else {
            property = "application-test.properties";  // 기본값 설정
        }

        // 선택된 프로퍼티 파일 설정
        configurer.setLocation(new ClassPathResource(property));

        return configurer;
    }
}