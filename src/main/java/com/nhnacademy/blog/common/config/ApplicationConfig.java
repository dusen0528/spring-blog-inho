package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.RootPackageBase;
import com.nhnacademy.blog.common.config.prod.DataSourceConfig;
import com.nhnacademy.blog.common.config.prod.TransactionConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Application Context를 생성할 때의 환경설정을 담당.
 *
 * - 이 클래스는 Spring 애플리케이션 컨텍스트 초기화 시, 필요한 설정을 제공합니다.
 * - @Import와 @ComponentScan 애노테이션을 사용하여 설정 및 컴포넌트 스캔 범위를 정의합니다.
 */

/**
 * @Import:
 * - 외부 구성 클래스를 가져와 현재 애플리케이션 컨텍스트의 설정으로 포함합니다.
 * - 여기서는 DataSourceConfig와 TransactionConfig를 가져옵니다.
 * - 이를 통해 데이터베이스 연결 및 트랜잭션 관리 관련 설정을 통합합니다.
 */
@Import(value = {DataSourceConfig.class, TransactionConfig.class})


/**
 * @ComponentScan:
 * - 지정된 RootPackageBase.class를 기준으로 하위 패키지에서 Spring의 특정 애노테이션(@Component, @Service, @Repository, @Controller)을 검색합니다.
 * - basePackageClasses 속성을 통해 특정 클래스를 기준으로 기본 패키지를 설정합니다.
 * - 여기서는 RootPackageBase를 기준으로 스캔 범위를 정의합니다.
 *
 * @Component:
 * - Spring 컨테이너가 관리하는 일반적인 Bean 객체를 정의합니다.
 * - 구체적인 역할에 따라 @Service, @Repository, @Controller로 세분화할 수 있습니다.
 *
 * @Service:
 * - 비즈니스 로직을 처리하는 서비스 계층에서 사용됩니다.
 * - 서비스 계층에 명확한 역할을 부여하며, 비즈니스 로직이 포함된 클래스를 나타냅니다.
 *
 * @Repository:
 * - 데이터 액세스 계층에서 사용되며, 데이터베이스와의 상호작용을 담당합니다.
 * - 주로 DAO(Data Access Object) 클래스에 사용됩니다.
 * - @Repository는 데이터 액세스 예외를 Spring 데이터 액세스 예외로 변환하는 역할도 합니다.
 *
 * @Controller:
 * - 프레젠테이션 계층에서 사용되며, 웹 요청을 처리하고 응답을 반환하는 역할을 합니다.
 * - 주로 HTTP 요청을 매핑하고, 적절한 뷰(View)를 선택하거나 데이터를 반환합니다.
 */
@ComponentScan(basePackageClasses = RootPackageBase.class)
public class ApplicationConfig {

}

