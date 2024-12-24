package com.nhnacademy.blog.common.init;


import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;

import java.util.concurrent.TimeUnit;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;

public class InitMySql /*implements Initializeable*/ {
    /**
     * 참고 : https://github.com/wix-incubator/wix-embedded-mysql
     * @param context
     */
    //@Override
    public void initialize() {
        MysqldConfig config = aMysqldConfig(Version.v8_latest)
                .withCharset(UTF8)
                .withPort(3306)
                .withUser("nhn_academy_blog", "1234")
                .withTimeZone("Asia/Seoul")
                .withTimeout(2, TimeUnit.MINUTES)
                .withServerVariable("max_connect_errors", 9999)
                .build();

        EmbeddedMysql mysqld = anEmbeddedMysql(config)
                .addSchema("nhn_academy_blog", classPathScript("blog.sql"))
                .start();
    }

}
