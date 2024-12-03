package com.nhnacademy.blog.config.init;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
public class InitProcessorTest {

    @Test
    public void process(){
      InitProcessor initProcessor = new InitProcessor();
      initProcessor.process();
    }

}