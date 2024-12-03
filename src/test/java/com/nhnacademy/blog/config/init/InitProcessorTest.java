package com.nhnacademy.blog.config.init;

import com.nhnacademy.blog.config.impl.InitProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class InitProcessorTest {

    @Test
    public void process(){
      InitProcessor initProcessor = new InitProcessor();
      initProcessor.process();
    }

}