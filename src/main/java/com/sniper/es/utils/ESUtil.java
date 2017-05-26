package com.sniper.es.utils;

import org.elasticsearch.common.settings.Settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:ES配置工具类
 * User： JinHuaTao
 * Date：2017/4/18
 * Time: 10:35
 */

public class ESUtil {

    public static Settings getDefaultSettings(){
        int shards = 3; //3个机架
        int replicas = 0;//没有副本
        return getSettings(shards, replicas);
    }

    public static Settings getSettings(final int shards, final int replicas){
        int shard = shards < 2 ? 1:shards;
        Settings settings = Settings.builder()
                .put("index.number_of_shards", shard)  //机架数， 16 8 4
                .put("index.number_of_replicas", replicas) //数据副本数
                //.put("index.compress", true)        //是否压缩  es5.2版本不支持
                //.put("index.store.compress.tv", true)   //词元向量压缩  es5.2版本不支持
                //.put("index.translog.flush_threshold_ops", "100000")//设置事务日志，当累计操作达到10000时就执行flush操作，默认值 5000 es5.2版本不支持
                .put("index.refresh_interval", "-1") // 禁止 refresh
                .build();

        return settings;
    }

    public static Map<String, Object> getTestSource(){
        Map<String, Object> source = new HashMap<>();
        source.put("id", "MjM5MTAyMTk2MA==:404981954:2:f8081c900e6470ba2b5e5462a20e966c");
        source.put("article_id", "MjM5MTAyMTk2MA==:404981954:2:f8081c900e6470ba2b5e5462a20e966c");
        source.put("wx_biz", "MjM5MTAyMTk2MA==");
        source.put("author", "大楚网");
        source.put("title", "大家来支招丨领导把他侄女介绍给我了，没眼缘，我要怎么拒绝啊？");
        source.put("brief", "大家来帮忙今晚十点半——网友支招是大楚网为广大网友们在线解决情感难题的专栏.如果您也有情感困惑,请点击");
        source.put("content", "〈 未经许可严禁转载 分享到朋友圈才是义举 〉 大家来帮忙 今晚十点半——网友支招是大楚网为广大网友们在线解决情感难题的专栏。如果您也有情感困惑，请点击\"阅读原文\"给我们来信，希望大家可以帮到你哦。 大楚网友“冬冬”来信 我在一家国有企业工作了3年");
        source.put("read_count", 6176);
        source.put("like_count",6176);
        source.put("url", "http://mp.weixin.qq.com/s?__biz=MjM5MTAyMTk2MA==&mid=404981954&idx=2&sn=f8081c900e6470ba2b5e5462a20e966c");
        source.put("is_audio", false);
        source.put("is_video", false);
        source.put("is_infraction", false);
        source.put("is_headline", false);
        source.put("is_original", false);
        source.put("keywords", "1,2,3");
        source.put("state", -1);
        source.put("infractions", "22");
        source.put("task_id", "22222");
        source.put("create_time", "2017-04-18 11:31:00");
        source.put("crawler_time", "2017-04-18 11:21:00");
        source.put("update_time", "2017-04-18 11:51:00");
        return  source;
    }

    public static Map<String, Object> getVideoTestSource(){
        Map<String, Object> source = new HashMap<>();
        source.put("syncSource", 2);
        source.put("evidenceUser", 0);
        source.put("processStatus",0);
        source.put("isPermittee", 0);
        source.put("isEffect", 1);
        source.put("source", "");
        source.put("postTime", new Date());
        source.put("propellingCategoryScore", 0);
        source.put("id","b3310ff207a2d2e024b78a0f05a71b2a");
        source.put("forwardCount", 0);
        source.put("topicTypeName", "");
        source.put("topicTypeWord", "");
        source.put("author", "王子晖");
        source.put("videoType", 0);
        source.put("dataType", 5);
        source.put("videoPublish", 0);
        source.put("commentCount", 0);
        source.put("topicType", 0);
        source.put("domain", "cnhubei.com");
        source.put("auditUser", 0);
        source.put("evidenceStatus", 0);
        source.put("propellingCategoryName", "");
        source.put("siteKeyId", "7393");
        source.put("siteName", "cnhubei.com");
        source.put("keyId", "b3310ff207a2d2e024b78a0f05a71b2a");
        source.put("isViolation", 0);
        source.put("investigate", 0);
        source.put("title", "习近平全面从严治党的关键一着");
        source.put("propellingRegionWord", "");
        source.put("content", "新华网记者 王子晖 【学习进行时】“两学一做”学习教育，是推动全面从严治党向基层延伸的有力抓手。习近平强调，开展“两学一做”学习教育");
        source.put("titleKeyId", "76334a240a08e070cc504702638c3ec8");
        source.put("isLocalSiteData", 1);
        source.put("propellingRegionName", "");
        source.put("viewCount", 0);
        source.put("crawlerTopicType", 0);
        source.put("isImportantVA", false);
        source.put("summary", "“两学一做”为何要抓住基层？基层开展“两学一做”需要注重哪些方面，其效果用什么来检验？新华网《学习进行时》原创品牌栏目“讲习所”今天推出");
        source.put("programType", 0);
        source.put("isPropelling", false);
        source.put("isNeedCrawlerComment", 0);

        source.put("isBelongLocal", 0);
        source.put("url", "http://news.cnhubei.com/xw/2016zt/lxyz/201606/t3648668.shtml");
        source.put("statusList", "010");
        source.put("createTime", new Date());
        source.put("propellingSensitiveWord", "");
        source.put("auditStatus", 0);
        return  source;
    }

}
