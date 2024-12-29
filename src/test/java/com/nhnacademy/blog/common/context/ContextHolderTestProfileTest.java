package com.nhnacademy.blog.common.context;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.common.db.DbProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;
//TODO#18 -  @ActiveProfiles, test환경 - application-test.properties 환경 구성
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Slf4j
class ContextHolderTestProfileTest {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Environment environment;

    @Test
    @DisplayName("load context")
    void getApplicationContext(){
        //TODO#19 - context load test

        DbProperties dbProperties = (DbProperties) applicationContext.getBean("dbProperties");
        log.debug("dbProperties:{}", dbProperties);

        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        log.debug("dataSource:{}", dataSource);

        Assertions.assertNotNull(dbProperties);
        Assertions.assertNotNull(dataSource);

        Assertions.assertAll(
                ()->Assertions.assertEquals("jdbc:h2:mem:blogdb", dbProperties.getUrl()),
                ()->Assertions.assertEquals("sa", dbProperties.getUsername()),
                ()->Assertions.assertEquals("1234", dbProperties.getPassword()),
                ()->Assertions.assertEquals(3, dbProperties.getInitialSize()),
                ()->Assertions.assertEquals(3, dbProperties.getMaxTotal()),
                ()->Assertions.assertEquals(3, dbProperties.getMaxIdle()),
                ()->Assertions.assertEquals(3, dbProperties.getMinIdle()),
                ()->Assertions.assertEquals(2, dbProperties.getMaxWait()),
                ()->Assertions.assertEquals("org.h2.Driver", dbProperties.getDriverClassName()),
                ()->Assertions.assertEquals("select 1", dbProperties.getValidationQuery()),
                ()->Assertions.assertTrue(dbProperties.isTestOnBorrow())
        );
    }

    @Test
    @DisplayName("profile : test")
    void profileTest(){
        //TODO#20 - active test profile
        boolean actual = Arrays.stream(environment.getActiveProfiles()).toList().contains("test");
        Assertions.assertTrue(actual);
    }

    /**
     * Spring의 Environment 객체는 다음과 같은 주요 역할을 수행합니다:
     * - 프로파일 관리: 활성화된 프로파일을 관리하고, 환경별로 다른 설정을 사용할 수 있도록 지원합니다.
     * - 환경 변수 및 시스템 프로퍼티 접근: 운영체제의 환경 변수와 Java 시스템 프로퍼티에 접근하여 동적으로 설정을 조정합니다.
     * - 프로퍼티 소스 관리: 여러 프로퍼티 소스를 관리하여 설정 정보를 중앙화하고, 필요에 따라 읽어올 수 있습니다.
     * = 프로퍼티 값 변환: 프로퍼티 값을 다양한 타입으로 변환하여 사용합니다.
     *
     * 즉 시스템 환경 변수들을 Environment를 통해서 접근할 수 있다.
     */
    @Test
    @DisplayName("environment test")
    void environmentTest(){
        //TODO#21 - 시스템 환경변수 출력
        for (PropertySource<?> propertySource : ((StandardEnvironment) environment).getPropertySources()) {
            if (propertySource.getSource() instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) propertySource.getSource();
                map.forEach((key, value) -> log.debug("key:{}= value:{}", key, value));
            }
        }
    }
}