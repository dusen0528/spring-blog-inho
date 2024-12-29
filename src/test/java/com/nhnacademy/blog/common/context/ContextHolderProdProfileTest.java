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

/**
 * TODO#12 - @ActiveProfiles, prod환경 - application-prod.properties 환경 구성
 * Spring 테스트 컨텍스트에서 특정 프로파일을 활성화하는 데 사용됩니다.
 * 이 애노테이션을 사용하면 테스트 실행 시 특정 프로파일을 활성화하여 해당 프로파일의 설정을 적용할 수 있습니다.
 * 이는 개발, 테스트, 프로덕션 등의 환경별로 다른 설정을 테스트할 때 유용합니다.
 */
@ActiveProfiles("prod")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Slf4j
class ContextHolderProdProfileTest {

    /**
     * TODO#13 - applicationContext 주입
     */
    @Autowired
    ApplicationContext applicationContext;

    /**
     * TODO#14 - environment 주입
     */
    @Autowired
    Environment environment;

    @Test
    @DisplayName("load context")
    void getApplicationContext(){

        //TODO#15 - prod  context Test

        DbProperties dbProperties = (DbProperties) applicationContext.getBean("dbProperties");
        log.debug("dbProperties:{}", dbProperties);

        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        log.debug("dataSource:{}", dataSource);

        Assertions.assertNotNull(dbProperties);
        Assertions.assertNotNull(dataSource);

        Assertions.assertAll(
                ()->Assertions.assertTrue(dbProperties.getUrl().contains("jdbc:mysql")),
                ()->Assertions.assertTrue(dbProperties.getUsername().contains("nhn_academy_")),
                ()->Assertions.assertEquals(5, dbProperties.getInitialSize()),
                ()->Assertions.assertEquals(5, dbProperties.getMaxTotal()),
                ()->Assertions.assertEquals(5, dbProperties.getMaxIdle()),
                ()->Assertions.assertEquals(5, dbProperties.getMinIdle()),
                ()->Assertions.assertEquals(2, dbProperties.getMaxWait()),
                ()->Assertions.assertEquals("com.mysql.cj.jdbc.Driver", dbProperties.getDriverClassName()),
                ()->Assertions.assertEquals("select 1", dbProperties.getValidationQuery()),
                ()->Assertions.assertTrue(dbProperties.isTestOnBorrow())
        );
    }

    @Test
    @DisplayName("profile : prod")
    void profileTest(){
        //TODO#16 - active prod profile
        boolean actual = Arrays.stream(environment.getActiveProfiles()).toList().contains("prod");
        Assertions.assertTrue(actual);
    }

    /**
     * Spring의 Environment 객체는 다음과 같은 주요 역할을 수행합니다:
     * - 프로파일 관리: 활성화된 프로파일을 관리하고, 환경별로 다른 설정을 사용할 수 있도록 지원합니다.
     * - 환경 변수 및 시스템 프로퍼티 접근: 운영체제의 환경 변수와 Java 시스템 프로퍼티에 접근하여 동적으로 설정을 조정합니다.
     * - 프로퍼티 소스 관리: 여러 프로퍼티 소스를 관리하여 설정 정보를 중앙화하고, 필요에 따라 읽어올 수 있습니다.
     * = 프로퍼티 값 변환: 프로퍼티 값을 다양한 타입으로 변환하여 사용합니다.
     * 즉 시스템 환경 변수들을 Environment를 통해서 접근할 수 있다.
     */
    @Test
    @DisplayName("environment test")
    void environmentTest(){
        //TODO#17 - 시스템 환경변수 출력
        for (PropertySource<?> propertySource : ((StandardEnvironment) environment).getPropertySources()) {
            if (propertySource.getSource() instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) propertySource.getSource();
                map.forEach((key, value) -> log.debug("key:{}= value:{}", key, value));
            }
        }
    }

}