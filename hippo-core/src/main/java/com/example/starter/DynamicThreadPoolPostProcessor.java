package com.example.starter;

import com.example.annotation.DynamicThreadPool;
import com.example.entity.BootstrapProperties;
import com.example.entity.CacheData;
import com.example.executor.DynamicThreadPoolExecutor;
import com.example.remote.HttpAgent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final BootstrapProperties properties;

    private final HttpAgent httpAgent;

    private final DynamicThreadPoolSubscribeConfig dynamicThreadPoolSubscribeConfig;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * registry dynamic bean;
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DynamicThreadPoolExecutor) && !DynamicThreadPoolAdapterChoose.match(bean)) {
            if (bean instanceof DynamicThreadPoolWrapper) {
                DynamicThreadPoolWrapper dynamicThreadPoolWrapper = (DynamicThreadPoolWrapper)bean;
                this.registerAndSubscribe(dynamicThreadPoolWrapper);
            }

            return bean;
        } else {
            try {
                DynamicThreadPool dynamicThreadPool = (DynamicThreadPool)ApplicationContextHolder.findAnnotationOnBean(beanName, DynamicThreadPool.class);
                if (Objects.isNull(dynamicThreadPool)) {
                    dynamicThreadPool = (DynamicThreadPool)DynamicThreadPoolAnnotationUtil.findAnnotationOnBean(beanName, DynamicThreadPool.class);
                    if (Objects.isNull(dynamicThreadPool)) {
                        return bean;
                    }
                }
            } catch (Exception var7) {
                log.error("Failed to create dynamic thread pool in annotation mode.", var7);
                return bean;
            }

            DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
            if ((dynamicThreadPoolExecutor = DynamicThreadPoolAdapterChoose.unwrap(bean)) == null) {
                dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor)bean;
            }

            DynamicThreadPoolWrapper dynamicThreadPoolWrapper = new DynamicThreadPoolWrapper(dynamicThreadPoolExecutor.getThreadPoolId(), dynamicThreadPoolExecutor);
            // get online pool config
            ThreadPoolExecutor remoteThreadPoolExecutor = this.fillPoolAndRegister(dynamicThreadPoolWrapper);
            DynamicThreadPoolAdapterChoose.replace(bean, remoteThreadPoolExecutor);
            this.subscribeConfig(dynamicThreadPoolWrapper);
            return DynamicThreadPoolAdapterChoose.match(bean) ? bean : remoteThreadPoolExecutor;
        }
    }
}
