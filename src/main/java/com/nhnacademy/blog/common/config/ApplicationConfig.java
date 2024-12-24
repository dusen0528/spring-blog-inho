package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.RootPackageBase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {DataSourceConfig.class, TransactionConfig.class})
@ComponentScan(basePackageClasses = RootPackageBase.class)
public class ApplicationConfig {

}