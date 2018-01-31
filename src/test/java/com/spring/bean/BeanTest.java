package com.spring.bean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by lenovo on 2017/7/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from AppConfig and TestConfig
@Configuration
@ComponentScan(basePackages = {"com.spring.bean"})
@EnableAspectJAutoProxy
@ContextConfiguration(classes = {BeanTest.class})
public class BeanTest {
    @Resource
    private SpringBeanUtil springBeanUtil;

    static   Map<String, Validator> beans;

    @PostConstruct
    public void init(){
        beans= SpringBeanUtil.getApplicationContext().getBeansOfType(Validator.class);
    }
    @Test
    public void testGetBean() {

        System.out.println("##" + beans);

    }

}
