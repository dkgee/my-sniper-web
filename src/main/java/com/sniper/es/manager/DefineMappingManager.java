package com.sniper.es.manager;

import com.sniper.es.base.IndexType;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.projectx.elasticsearch.ClientTemplate;
import org.projectx.elasticsearch.NodeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Description:ES中定义的文档Mapping管理
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 16:52
 */
@Component
public class DefineMappingManager {

    private static final Logger logger = LoggerFactory.getLogger(DefineMappingManager.class);

    @Autowired
    private ClientTemplate clientTemplate;

    /**
     * 构建Mapping
     * @param indexType 索引类型
     * @param indexName 索引名称
     * */
    public void buildMapping(IndexType indexType, String indexName){
        XContentBuilder mapping = null;
        switch (indexType) {
            case wechat:
               mapping = wechatMapping();
                break;
            case mp:
                System.out.println("构建微信用户mapping");
                break;
            case weiboStatus:
                System.out.println("构建微博mapping");
                break;
            case weiboUser:
                System.out.println("构建微博用户mapping");
                break;
            case video:
                mapping = videoMapping();
                System.out.println("构建视听mapping");
                break;
            default:
                break;
        }

        if(mapping!=null) {
            try {
                putMapping(indexType, mapping, indexName);
                logger.info("在索引"+clientTemplate.getIndexName()+"中构建type是"+indexType.name()+"的mapping成功！");
            } catch(Exception e) {
                logger.error("在索引"+clientTemplate.getIndexName()+"中构建type是"+indexType.name()+"的mapping失败！出现异常 异常信息是："+e);
            }
        }
    }

    /**
     * 在ES中创建Mapping
     * */
    public PutMappingResponse putMapping(final IndexType indexType, final XContentBuilder mapping, final String indexName){
        return clientTemplate.executeGet(new NodeCallback<PutMappingResponse>() {
            @Override
            public ActionFuture<PutMappingResponse> execute(IndicesAdminClient indicesAdminClient) {
                PutMappingRequest mappingRequest = Requests.putMappingRequest(indexName).type(indexType.name()).source(mapping);
                return indicesAdminClient.putMapping(mappingRequest);
            }
        });
    }

    /**
     * 微信Mapiing
     * */
    public XContentBuilder wechatMapping() {
        XContentBuilder mapping = null;
        try{
            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(IndexType.wechat.name())
                    //.startObject("_timestamp").field("enabled", true).field("index", "not_analyzed").field("format", "yyyy-MM-dd HH:mm:ss").endObject() // es5.2 不支持
                    //.startObject("_source").field("enabled", true).field("compress", true).endObject()
                    .startObject("_source").field("enabled", true).endObject()// es5.2 不支持compress
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                        .startObject("id").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("article_id").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("wx_biz").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("author").field("type","string").endObject()
                        .startObject("title").field("type","string").endObject()
                        .startObject("brief").field("type","string").endObject()
                        .startObject("content").field("type","string").endObject()
                        .startObject("read_count").field("type","integer").endObject()
                        .startObject("like_count").field("type","integer").endObject()
                        .startObject("url").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("is_audio").field("type","boolean").field("index","not_analyzed").endObject()
                        .startObject("is_video").field("type","boolean").field("index","not_analyzed").endObject()
                        .startObject("is_infraction").field("type","boolean").field("index","not_analyzed").endObject()
                        .startObject("is_headline").field("type","boolean").field("index","not_analyzed").endObject()
                        .startObject("is_original").field("type","boolean").field("index","not_analyzed").endObject()
                        .startObject("keywords").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("state").field("type","integer").field("index","not_analyzed").endObject()
                        .startObject("infractions").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("task_id").field("type","string").field("index","not_analyzed").endObject()
                        .startObject("create_time").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                        .startObject("crawler_time").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                        .startObject("update_time").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            logger.error("在索引"+clientTemplate.getIndexName()+"中构建type是"+IndexType.video.name()+"的XContentBuilder 对象时失败！出现异常 异常信息是："+e);
        }
        return mapping;
    }

    /**
     * 视听Mapping
     * */
    private XContentBuilder videoMapping() {
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(IndexType.video.name())
                    .startObject("_timestamp").field("enabled", true).field("index", "not_analyzed").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("_source").field("enabled", true).field("compress", true).endObject()
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                        .startObject("author").field("type","multi_field").field("path", "just_name")
                             .startObject("fields")
                                .startObject("author").field("type", "string").endObject()
                                 .startObject("author-full").field("type", "string").field("index","not_analyzed").endObject()
                            .endObject()
                        .endObject()
                    .startObject("title").field("type","string").endObject()
                    .startObject("url").field("type", "string").field("index","not_analyzed").endObject()
                    .startObject("content").field("type","string").endObject()
                    .startObject("postTime").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("viewCount").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("commentCount").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("forwardCount").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("dataType").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("source").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("siteKeyId").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("boardKeyId").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("keyId").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("topicType").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("titleKeyId").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("summary").field("type","string").endObject()
                    .startObject("isPropelling").field("type","boolean").field("index","not_analyzed").endObject()
                    .startObject("propellingRegionName").field("type","string").endObject()
                    .startObject("propellingHitRegionWord").field("type","string").endObject()
                    .startObject("propellingCategoryName").field("type","string").endObject()
                    .startObject("propellingHitSensitiveWord").field("type","string").endObject()
                    .startObject("propellingCategoryScore").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("topicTypeName").field("type","string").endObject()
                    .startObject("topicTypeWord").field("type","string").endObject()
                    .startObject("createTime").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("isImportantVA").field("type","boolean").field("index","not_analyzed").endObject()
                    .startObject("authorImageUrl").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("microblogUserUnique").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("isNeedCrawlerComment").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("imageUrl").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("syncSource").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("containArea").field("type","string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()//贴文包含的地域--一找
                    .startObject("containMans").field("type","string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()//贴文包含的人物--一找
                    .startObject("isCommercial").field("type","string").field("index","not_analyzed").endObject()//是否是广告--一找
                    .startObject("articleCategory").field("type","string").field("index","not_analyzed").endObject()//文本类别--一找
                    .startObject("isBelongLocal").field("type","integer").field("index","not_analyzed").endObject()//贴文是否属于本地
                    .startObject("isLocalSiteData").field("type","integer").field("index","not_analyzed").endObject()//贴文所属网站是否属于本地
                    .startObject("videoPublish").field("type","integer").field("index","not_analyzed").endObject()

                    .startObject("isEffect").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("effectTime").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("tags").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("keyword").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("videoUrl").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("thumbnailPath").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("programType").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("evidenceTime").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("auditStatus").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("auditTime").field("type","date").field("index","not_analyzed").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("auditUser").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("remark").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("siteName").field("type","string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                    .startObject("domain").field("type","multi_field").field("path", "just_name")
                    .startObject("fields")
                    .startObject("domain").field("type","string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                    .startObject("domain-full").field("type", "string").field("index","not_analyzed").endObject()
                    .endObject()
                    .endObject()
                    .startObject("videoType").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("isPermittee").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("siteImportantLevel").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("isViolation").field("type","integer").field("index","not_analyzed").endObject()
                    .startObject("investigate").field("type","integer").field("index","not_analyzed").endObject()//节目查处状态
                    .startObject("evidenceStatus").field("type","integer").field("index","not_analyzed").endObject()//取证状态
                    .startObject("evidenceUser").field("type","integer").field("index","not_analyzed").endObject()//取证人
                    .startObject("processStatus").field("type","integer").field("index","not_analyzed").endObject()//处理状态
                    //持证,ip是否所属本地,icp是否备案是否在本地备案 1)0未持证，1持证;2)0非本地，1本地;3)0未备案，1本地备案，2异地备案
                    .startObject("statusList").field("type","string").field("index","not_analyzed").endObject()
                    .startObject("crawlerTopicType").field("type","integer").field("index","not_analyzed").endObject()//bt区分字段
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            logger.error("在索引"+clientTemplate.getIndexName()+"中构建type是"+IndexType.video.name()+"的XContentBuilder 对象时失败！出现异常 异常信息是："+e);
        }
        return mapping;
    }

    /**
     * 模板Mapping
     * */
    public static XContentBuilder getMapping(){
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("_ttl") //开启倒计时功能
                        .field("enabled",false)
                        .endObject()
                            .startObject("properties")
                            .startObject("title")
                                    .field("type","string")
                            .endObject()
                            .startObject("question")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("answer")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("category")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("author")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("date")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("answer_author")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("answer_date")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("description")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("keywords")
                                    .field("type","string")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("read_count")
                                    .field("type","integer")
                                    .field("index","not_analyzed")
                            .endObject()
                            .startObject("list").field("type","object").endObject()//关联数据
                    .endObject()
                    .endObject();
        } catch (IOException e) {
           logger.error("构建XXmapping异常", e);
        }

        return mapping;
    }

}
