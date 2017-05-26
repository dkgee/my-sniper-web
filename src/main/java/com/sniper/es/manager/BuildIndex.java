package com.sniper.es.manager;

import com.sniper.es.base.IndexType;
import org.apache.commons.lang3.StringUtils;
import org.projectx.elasticsearch.ClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 构建索引
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 17:21
 */

@Component
public class BuildIndex {

    private static final Logger logger = LoggerFactory.getLogger(BuildIndex.class);

    @Autowired
    private ClientTemplate clientTemplate;

    @Autowired
    private DefineMappingManager defineMappingManager;

    @Autowired
    private IndexManager indexManager;

    private static final int shard = 2;//ES分片数

    private static final int replica = 1;//数据备份数

    /**
     * 用户创建索引
     * */
    public boolean createIndex(boolean isRecreate,String currentIndexName) {
        boolean result = false;
        try{
            logger.info("移除旧的索引创建新索引");
            if (StringUtils.isNotBlank(currentIndexName.trim()) && indexManager.indexExists(currentIndexName.trim()) && isRecreate) {
                clientTemplate.deleteIndex();
            }
            if(isRecreate && StringUtils.isNotBlank(currentIndexName)){
                indexManager.createIndex(currentIndexName.trim(),shard,replica);
                indexManager.createIndexAliasName(currentIndexName.trim(), clientTemplate.getIndexName());
                indexManager.updateIndexRefreshInterval(currentIndexName.trim(), 5);

                defineMappingManager.buildMapping(IndexType.video,clientTemplate.getIndexName());
                result = true;
            }
        }catch(Exception e){
            logger.error("移除旧的索引创建新索引失败",e);
        }
        return result;

    }

}
