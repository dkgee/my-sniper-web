package com.sniper.es;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Description:
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 15:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:elasticsearch/applicationContext-es.xml"})
public class BaseTest {

    @Test
    public void base(){

    }
}
