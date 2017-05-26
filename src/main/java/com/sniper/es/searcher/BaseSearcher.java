package com.sniper.es.searcher;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.sniper.es.base.IndexType;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.projectx.elasticsearch.ClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Description:基本搜素类
 * User： JinHuaTao
 * Date：2017/4/17
 * Time: 18:19
 */

public class BaseSearcher {

    protected static final Logger logger = LoggerFactory.getLogger(BaseSearcher.class);

    @Autowired
    protected ClientTemplate clientTemplate;

    /**
     * 插入或修改文档
     */
    public IndexResponse insertOrUpdate(final String indices, final IndexType indexType, final String keyId, final Map<String, Object> source) {
        return clientTemplate.executeGet((Client client)->{
            IndexRequest request = Requests.indexRequest(indices).type(indexType.name()).id(keyId).source(source);
            logger.debug("insertOrUpdate-" + request.toString());
            ActionFuture<IndexResponse> res = client.index(request);
            clientTemplate.refreshIndex();
            return res;
        });
    }

    /**
     * 根据ID获取文档
     */
    public GetResponse get(final String indices, final IndexType indexType, final String keyId) {
        return clientTemplate.executeGet((Client client)->{
            GetRequest request = Requests.getRequest(indices).type(indexType.name()).id(keyId);
            logger.debug("get-" + request.toString());
            return client.get(request);
        });
    }

    /**
     * 根据ID删除文档
     */
    public DeleteResponse delete(final String indices, final IndexType indexType, final String keyId) {
        return clientTemplate.executeGet((Client client)->{
            DeleteRequest request = Requests.deleteRequest(indices).type(indexType.name()).id(keyId);
            logger.debug("delete-" + request.toString());
            return client.delete(request);
        });
    }

    /**
     * 查询文档
     */
    public SearchResponse search(final String indices, final IndexType indexType,final SearchSourceBuilder ssb, final SearchType searchType) {
        return clientTemplate.executeGet((Client client)->{
            SearchRequest request = Requests.searchRequest(indices).preference("_primary_first").types(indexType.name()).searchType(searchType).source(ssb);
            logger.debug("search-" + request.toString());
            return client.search(request);
        });
    }

    /**
     * 创建索引文档类型及其Mapping
     * */
    public PutMappingResponse putMapping(final String indices,final IndexType indexType, final XContentBuilder mapping){
        return clientTemplate.executeGet((IndicesAdminClient indicesAdminClient)->{
            PutMappingRequest mappingRequest = Requests.putMappingRequest(indices).type(indexType.name()).source(mapping);
            return indicesAdminClient.putMapping(mappingRequest);
        });
    }

    /**
     * 批量插入数据
     * */
    public void batchInsertData(final String indices, final IndexType indexType, final Map<String, Map<String, Object>> sourceMap) {
        try {
            clientTemplate.executeGet((Client client)->{
                BulkRequest br = Requests.bulkRequest();
                for (String id : sourceMap.keySet()) {
                    br.add(Requests.indexRequest(indices).type(indexType.name()).id(id).source(sourceMap.get(id)));
                }
                ActionFuture<BulkResponse> result = client.bulk(br);
                br.requests().clear();
                return result;
            });
            logger.info("Insert into " + indices + ", type: " + indexType.name() + ", total: " + sourceMap.size() + ", success!");
        } catch (Exception e) {
            logger.error("Insert into " + indices + ", type: " + indexType.name() + ", total: " + sourceMap.size() + ", fail!", e);
        }
    }

    /**
     * 批量删除相同索引文档
     */
    public void deleteIndexDoc(final String indices, final IndexType indexType, final List<String> ids) {
        try {

            clientTemplate.executeGet((Client client)->{
                BulkRequest br = Requests.bulkRequest();
                for (String id : ids) {
                    br.add(Requests.deleteRequest(indices).type(indexType.name()).id(id));
                }
                ActionFuture<BulkResponse> result = client.bulk(br);
                br.requests().clear();
                return result;
            });
            logger.info("Delete " + indices + ", type: " + indexType.name() + ", total: " + ids.size() + ", success!");
        } catch (Exception e) {
            logger.error("Delete " + indices + ", type: " + indexType.name() + ", total: " + ids.size() + ", fail!", e);
        }
    }

    /**
     * 判断索引是否存在
     */
    public boolean indexExists(final String indices){
        boolean exist = false;
        try{
            ClusterStateResponse csr = clientTemplate.executeGet((Client client)->{
                return client.admin().cluster().state(new ClusterStateRequest());
            });

            if(csr != null){
                ImmutableOpenMap<String, IndexMetaData> tempMap = csr.getState().getMetaData().indices();
                for(ObjectCursor<IndexMetaData> indexMetaData : tempMap.values()){
                    if(indexMetaData.value.getIndex().equals(indices))
                            exist = true;
                }
            }
        }catch(Exception e){
            logger.error("Get es cluster status exception",e);
        }
        return exist;
    }

    /**
     * 创建索引
     */
    public boolean createIndex(final String indices, final Settings settings){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                return admin.prepareCreate(indices).setSettings(settings).execute();
            });
            logger.info("Create indices " + indices + " success！");
            return true;
        }  catch(Exception e) {
            logger.error("Create indices" + indices + " fail!", e);
            return false;
        }
    }

    /**
     * 修改索引分片设置
     */
    public boolean updateIndexSetting(final String indices, final Settings settings) {
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                return admin.updateSettings(Requests.updateSettingsRequest(indices).settings(settings));
            });
            logger.info("Update index " + indices + " settings success！");
            return true;
        }catch(Exception e){
            logger.error("Update index " + indices + " settings fail！", e);
            return false;
        }
    }

    /**
     * 创建索引别名
     */
    public boolean createIndexAliasName(final String indices,final String alias){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                IndicesAliasesRequest.AliasActions aliasAction =  IndicesAliasesRequest.AliasActions.add().index(indices).alias(alias);
                return admin.aliases(Requests.indexAliasesRequest().addAliasAction(aliasAction));
            });
            logger.info("Create alias " + alias + " for " + indices +" success!");
            return true;
        }catch(Exception e) {
            logger.error("Create alias " + alias + " for " + indices +" fail!", e);
            return false;
        }
    }

    /**
     * 移除索引别名
     */
    public boolean removeAlias(final String indices,final String alias){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                IndicesAliasesRequest.AliasActions aliasAction =  IndicesAliasesRequest.AliasActions.remove().index(indices).alias(alias);
                return admin.aliases(Requests.indexAliasesRequest().addAliasAction(aliasAction));
            });
            logger.info("Remove alias " + alias + " for " + indices +" success!");
            return true;
        } catch(Exception e) {
            logger.info("Remove alias " + alias + " for " + indices +" fail!", e);
            return false;
        }
    }

    /**
     * 修改索引别名
     */
    public boolean changeAlias(final String srcIndexName,final String destIndexName,final String indexAlias){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                IndicesAliasesRequest request = Requests.indexAliasesRequest();
                for(String alias : indexAlias.split(",")){
                    if(StringUtils.isBlank(alias)) continue;

                    IndicesAliasesRequest.AliasActions addAliasAction =  IndicesAliasesRequest.AliasActions.add().index(destIndexName).alias(alias);
                    IndicesAliasesRequest.AliasActions removeAliasAction =  IndicesAliasesRequest.AliasActions.remove().index(srcIndexName).alias(alias);
                    Requests.indexAliasesRequest().addAliasAction(addAliasAction).addAliasAction(removeAliasAction);
                }
                return admin.aliases(request);
            });

            logger.info("Change index name  success srcIndexName: " + srcIndexName + ", destIndexName: " + destIndexName + ", indexAlias" + indexAlias);
            return true;
        }catch(Exception e){
            logger.info("Change index name  fail" , e);
            return false;
        }
    }

    /**
     * 关闭指定索引
     * @param indices
     */
    public boolean closeIndex(final String indices){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                return admin.close(Requests.closeIndexRequest(indices));
            });
            logger.info("Shutdown " + indices + " success!");
            return true;
        } catch(Exception e) {
            logger.info("Shutdown " + indices + " fail!", e);
            return false;
        }
    }

    /**
     * 打开指定索引
     */
    public boolean openIndex(final String indices){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                return admin.open(Requests.openIndexRequest(indices));
            });
            logger.info("Open " + indices + " success!");
            return true;
        } catch(Exception e) {
            logger.info("Open " + indices + " fail!", e);
            return false;
        }
    }

    /**
     * 删除指定索引
     */
    public boolean deleteIndex(final String indices){
        try{
            clientTemplate.executeGet((IndicesAdminClient admin)->{
                return admin.delete(Requests.deleteIndexRequest(indices));
            });
            logger.info("Delete index " + indices + " success!");
            return true;
        } catch(Exception e) {
            logger.info("Delete index " + indices + " fail!", e);
            return false;
        }
    }

    /**
     * 刷新 索引
     */
    public void flushIndex(final String indices){
        clientTemplate.executeGet((IndicesAdminClient admin)->{
            return admin.flush(new FlushRequest(indices));
        });
    }

    /**
     * 滚动式获取数据
     */
    public SearchResponse searchScroll(final String scrollId,final long scrollTime){
        return clientTemplate.executeGet((Client client)->{
            return  client.prepareSearchScroll(scrollId).setScroll(TimeValue.timeValueHours(scrollTime)).execute();
        });
    }

}
