package com.sniper.es.manager;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.projectx.elasticsearch.ClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:ES索引管理，主要用于检查索引、创建索引等操作
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 15:10
 */

@Component
public class IndexManager {

    private static final Logger logger = LoggerFactory.getLogger(IndexManager.class);

    @Autowired
    private ClientTemplate clientTemplate;

    /**
     * 创建索引
     */
    public boolean createIndex(final String indexName,final int shards,final int replicas){
        try{
            /*clientTemplate.executeGet(new NodeCallback<CreateIndexResponse>() {
                @Override
                public ActionFuture<CreateIndexResponse> execute(IndicesAdminClient admin) {
                    int shard = shards<2?1:shards;
                    Settings settings = Settings.builder()
                            .put("index.number_of_shards", shard) // 16 8 4
                            .put("index.number_of_replicas", replicas)
                            .put("index.compress", true)
                            .put("index.store.compress.tv", true)
                            .put("index.translog.flush_threshold_ops", "100000")
                            .put("index.refresh_interval", "-1") // 禁止 refresh
                            .build();

                    return admin.prepareCreate(indexName).setSettings(settings).execute();
                }
            });*/

            clientTemplate.executeGet((IndicesAdminClient admin)->{
                int shard = shards<2?1:shards;
                Settings settings = Settings.builder()
                        .put("index.number_of_shards", shard) // 16 8 4
                        .put("index.number_of_replicas", replicas)
                        .put("index.compress", true)
                        .put("index.store.compress.tv", true)
                        .put("index.translog.flush_threshold_ops", "100000")
                        .put("index.refresh_interval", "-1") // 禁止 refresh
                        .build();

                return admin.prepareCreate(indexName).setSettings(settings).execute();
            });

            logger.info("索引"+indexName+"创建成功！");
            return true;
        } catch(Exception e) {
            logger.error("创建索引"+indexName+"出现异常", e);
            return false;
        }
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     * @return boolean
     */
    public boolean indexExists(String indexName){
        boolean exists = false;
        try{
            /*ClusterStateResponse csr = clientTemplate.executeGet(new ClientCallback<ClusterStateResponse>() {
                public ActionFuture<ClusterStateResponse> execute(Client client) {
                    return client.admin().cluster().state(new ClusterStateRequest());
                }
            });*/

            ClusterStateResponse csr = clientTemplate.executeGet((Client client)->{
                return client.admin().cluster().state(new ClusterStateRequest());
            });

            if(csr!=null){
                ImmutableOpenMap<String, IndexMetaData> tempMap = csr.getState().getMetaData().indices();
                for(ObjectCursor<IndexMetaData> indexMetaData : tempMap.values()){
                    if(indexMetaData.value.getIndex().equals(indexName)){
                        exists = true;
                    }
                }
            }
        }catch(Exception e){
            logger.error("获得es集群状态信息异常！",e);
        }
        return exists;
    }

    /**
     * 创建索引别名
     */
    public boolean createIndexAliasName(final String indexName,final String aliasName){
        try{
           /*clientTemplate.executeGet(new NodeCallback<IndicesAliasesResponse>() {
                @Override
                public ActionFuture<IndicesAliasesResponse> execute(IndicesAdminClient admin) {
                    IndicesAliasesRequest.AliasActions aliasAction =  IndicesAliasesRequest.AliasActions.add().index(indexName).alias(aliasName);
                    return  admin.aliases(Requests.indexAliasesRequest().addAliasAction(aliasAction));
                }
            });*/

            clientTemplate.executeGet((IndicesAdminClient admin)->{
                IndicesAliasesRequest.AliasActions aliasAction =  IndicesAliasesRequest.AliasActions.add().index(indexName).alias(aliasName);
                return admin.aliases(Requests.indexAliasesRequest().addAliasAction(aliasAction));
            });
            logger.info("索引"+aliasName+"别名命名成功创建成功！");
            return true;
        }catch(Exception e) {
            logger.error("创建索引别名"+aliasName+"出现异常", e);
            return false;
        }
    }

    /**
     * 修改索引Refresh_Interval参数
     * @param indexName
     * @param interval
     * @return
     * @return boolean
     */
    public boolean updateIndexRefreshInterval(final String indexName,final int interval){
        try{
            /*clientTemplate.executeGet(new NodeCallback<UpdateSettingsResponse>() {
                @Override
                public ActionFuture<UpdateSettingsResponse> execute(IndicesAdminClient admin) {
                    Settings settings = Settings.builder()
                            .put("index.refresh_interval", interval)
                            .build();
                    return admin.updateSettings(Requests.updateSettingsRequest(indexName).settings(settings));
                }
            });*/

            clientTemplate.executeGet((IndicesAdminClient admin)->{
                Settings settings = Settings.builder().put("index.refresh_interval", interval).build();
                return admin.updateSettings(Requests.updateSettingsRequest(indexName).settings(settings));
            });

            logger.info("更新索引"+indexName+"Refresh_Interval参数成功！");
            return true;
        }catch(Exception e){
            logger.error("更新索引"+indexName+"Refresh_Interval参数异常！",e);
            return false;
        }
    }


}
