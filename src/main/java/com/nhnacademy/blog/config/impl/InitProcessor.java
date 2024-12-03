package com.nhnacademy.blog.config.impl;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.config.StartProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j
public class InitProcessor implements StartProcessor {
    //service loader 참고
    //https://medium.com/@manvendrapsingh/java-extension-mechanism-self-discovery-bean-instantiation-beeeeafcaa7f

    @Override
    public void process() {
        List<InitializableWrapper> initializableWrappers = getClassFromLoader();
        for (InitializableWrapper initializableWrapper : initializableWrappers) {
            log.debug("bean:{} , order:{}", initializableWrapper.getInitializeable().getClass().getSimpleName(), initializableWrapper.getOrder());

            //초기화
            initializableWrapper.getInitializeable().initialize();
        }
    }

    private List<InitializableWrapper> getClassFromLoader(){
        ServiceLoader<Initializeable> loader = ServiceLoader.load(Initializeable.class);
        Iterator<Initializeable> iterator = loader.iterator();
        List<InitializableWrapper> wrappers = new ArrayList<>();

        while (iterator.hasNext()) {
            Initializeable initializeable = iterator.next();
            InitOrder initOrder = initializeable.getClass().getAnnotation(InitOrder.class);
            int order  = Objects.nonNull(initOrder) ? initOrder.value() : 1;
            wrappers.add(new InitializableWrapper(order, initializeable));
        }

        Collections.sort(wrappers, (o1,o2)->{return o1.getOrder()-o2.getOrder();});

        return wrappers;
    }

    public class InitializableWrapper {
        private final int order;
        private final Initializeable initializeable;

        public InitializableWrapper(int order, Initializeable initializeable) {
            this.order = order;
            this.initializeable = initializeable;
        }

        public int getOrder() {
            return order;
        }

        public Initializeable getInitializeable() {
            return initializeable;
        }
    }

}
