package com.nhnacademy.blog.common.init.impl;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.db.DbProperties;
import com.nhnacademy.blog.common.init.Initializeable;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@InitOrder(value = 1)
public class InitDbPropertiesReader implements Initializeable {

    private static final String DEFAULT_DB_PROPERTIES_FILE="db.properties";
    private final String dbPropertiesFile;

    public InitDbPropertiesReader(){
        this(DEFAULT_DB_PROPERTIES_FILE);
    }

    public InitDbPropertiesReader(String dbPropertiesFile) {
        if(dbPropertiesFile == null || dbPropertiesFile.isBlank()) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", dbPropertiesFile));
        }
        this.dbPropertiesFile = dbPropertiesFile;
    }

    @Override
    public void initialize(Context context) {
        Properties properties = readProperties();
        Map<String, String> map = getMapFromProperties(properties);
        //properties 객체로 변환
        DbProperties dbProperties = getDbPropertiesFromMap(map);

        log.debug("context:{}",context);
        context.registerBean(DbProperties.BEAN_NAME,dbProperties);
    }

    private DbProperties getDbPropertiesFromMap(Map<String, String> map){

        String url = map.get("url");
        String username=map.get("username");
        String password=map.get("password");

        int initialSize = Integer.parseInt(map.get("initialSize"));
        int maxTotal = Integer.parseInt(map.get("maxTotal"));
        int maxIdle = Integer.parseInt(map.get("maxIdle"));
        int minIdle = Integer.parseInt(map.get("minIdle"));
        int maxWait = Integer.parseInt(map.get("maxWait"));
        String validationQuery = map.get("validationQuery");
        boolean testOnBorrow = Boolean.parseBoolean(map.get("testOnBorrow"));
        boolean spy = Boolean.parseBoolean(map.get("spy"));

        DbProperties dbProperties = new DbProperties(
                url,
                username,
                password,
                initialSize,
                maxTotal,
                maxIdle,
                minIdle,
                maxWait,
                validationQuery,
                testOnBorrow,
                spy
        );
        log.debug("dbProperties: {}", dbProperties);

        return dbProperties;
    }

    //properties -> map 변환
    private Map<String,String> getMapFromProperties(Properties properties) {
        Map<String,String> map = new HashMap<>();
        for(String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    private Properties readProperties(){
        Properties properties = new Properties();
        try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(dbPropertiesFile)) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
