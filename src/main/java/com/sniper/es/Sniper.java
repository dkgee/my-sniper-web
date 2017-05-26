package com.sniper.es;

import com.sniper.es.base.IndexType;
import com.sniper.es.searcher.SniperSearcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Description:
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 16:35
 */

public class Sniper {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:elasticsearch/applicationContext-es.xml");

//        SearchManager searchManager = (SearchManager) context.getBean("searchManager");
//        String keyId = "MjIyNDQ5Nzc4MQ==:402247603:2:d1979855a9d2497d5f88765e89eb18b9";
//        Map<String, Object> rs =  searchManager.findTopicByKeyId(IndexType.wechat, keyId);

        SniperSearcher sniperSearcher  = (SniperSearcher) context.getBean("sniperSearcher");
//        String indices = "medicine_test";
//        String alias = "medicine";
        //1,测试id检索
        String keyId = "MjIyNDQ5Nzc4MQ==:402247603:2:d1979855a9d2497d5f88765e89eb18b9";
        Map<String, Object> rs = sniperSearcher.get(IndexType.wechat, keyId);
        System.out.println(rs);

        //2,测试统计
//        QueryBuilder audioQueryBuilder = QueryBuilders.termQuery("wx_biz", "MjM5MTAyMTk2MA==");
//        long audioTotal = sniperSearcher.count(IndexType.wechat, audioQueryBuilder);
//        System.out.println("微博账号Id=MjM5MTAyMTk2MA==的文章数：" + audioTotal);

        //3,测试创建索引
//        String indices = "medicine_test";
//        sniperSearcher.createIndex(indices, ESUtil.getDefaultSettings());

        //4,创建索引类型
//        sniperSearcher.createIndexAliasName(indices, alias);

        //5，添加索引文档类型及其mapping
//        DefineMappingManager defineMappingManager = (DefineMappingManager)context.getBean("defineMappingManager");
//        sniperSearcher.putMapping(indices, IndexType.wechat, defineMappingManager.wechatMapping());

        //6,插入数据
//        String indices = "hawkeye-app-web-video";
//        String keyId = "MjM5MTAyMTk2MA==:404981954:2:f8081c900e6470ba2b5e5462a20e966c";
//        String keyId = "b3310ff207a2d2e024b78a0f05a71b2a";
//        sniperSearcher.insertOrUpdate(indices, IndexType.video, keyId, ESUtil.getVideoTestSource());


        //7,检索数据
//        Map<String, Object> rs = sniperSearcher.get(indices, IndexType.wechat, keyId).getSource();
//        System.out.println(rs);

        //8，删除索引
//        sniperSearcher.deleteIndex(indices);

    }

    public void loadContextSample(){

        //1，web运行时，直接从上下文获取当前bean
//        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
//        String systemParams = context.getBean("systemParams");

        //2, 上下文类路径加载配置文件方式
//        ApplicationContext context = new ClassPathXmlApplicationContext("conf");

        //3，Spring配置文件加载方式 ，配置文件路径 resources/config/ip138.properties
//        Resource resource = new ClassPathResource("config/ip138.properties");


    }
}
