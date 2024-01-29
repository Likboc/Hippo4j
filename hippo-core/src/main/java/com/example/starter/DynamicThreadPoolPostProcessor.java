package com.example.starter;

import com.example.annotation.DynamicThreadPool;
import com.example.entity.CacheData;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class DynamicThreadPoolPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(DynamicThreadPool.class)) {
            try {
                URL url = new URL("http://www.baidu.com");
                String[] params =  {};
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
