package com.sniper.etl.base;

/**
 * Description:常量类
 * User： JinHuaTao
 * Date：2017/4/20
 * Time: 16:18
 */

public class EtlConsts {


    public static final String KAFKA_CONFIG_FILE_PATH = "kafka/kafka-config.properties";//kafka配置文件路径

    public static final String SYNC_WECHAT_TO_DB_PROCESSOR_ID = "sniper-wechat-kafka-streams-to-db-processor";//同步微信数据处理器Id

    /**
     *  配置Kafka常量
     * */
    public static final String ROW_KEY_TOPIC = "sniper-content-v1-rowkey";// 行内容主题

    public static final String WEIBO_STATUS_ROW_KEY_TOPIC = "sniper-weibo-status-v4-rowkey"; //微博内容主题
    public static final String WEIBO_USER_ROW_KEY_TOPIC = "sniper-weibo-user-v8-rowkey"; //微博账户主题

    public static final String WECHAT_ACCOUNT_ROW_KEY_TOPIC = "sniper-wechat-account-v4-rowkey";//微信账户主题
    public static final String WECHAT_CONTENT_ROW_KEY_TOPIC = "sniper-wechat-content-v4-rowkey";//微博内容主题

    /**
     * 配置 Kafka 分组id
     * */
    public static final String WECHAT_KAFKA_TO_DB_GROUP_ID = "sniper-mp-kafka-to-db-processor-v1";//微信同步至DB分组Id

    public static final String WECHAT_ACCOUNT_TO_ES_GROUP_ID = "sniper-mp-to-es-processor-v1"; //微信账户同步至ES分组Id
    public static final String WECHAT_CONTENT_TO_ES_GROUP_ID = "sniper-wechat-to-es-processor-v1";//微信内容同步至ES分组Id

    public static final String WEIBO_CRAWLDATA_TOPIC = "sniper-demo-weibo-v4"; //微博采集测试Id
    public static final String WECHAT_CRAWLDATA_TOPIC = "sniper-demo-wechat-v7";//微信采集测试Id


    /**
     * ES配置参数
     * */
    public static final String ES_WECHAT_INDEX_NAME = "sniper-app-web-v3";//ES微信索引名称
    public static final String ES_WEIBO_INDEX_NAME = "sniper-app-web-v3";//ES微博索引名称

    public static final String ES_CLUSTER_NAME = "gd_es_cluster"; //ES集群名称
    public static final String ES_INDEX_NAME = "hawkeye-app-web";//ESs索引名称
    public static final String ES_HOSTS = "hmly1:9501,hmly2:9501,hmly3:9501"; //ES集群主机
    public static final String[] ES_HOSTS_ARR = new String[]{"hmly1:9501","hmly2:9501","hmly3:9501"}; //ES集群主机


}
