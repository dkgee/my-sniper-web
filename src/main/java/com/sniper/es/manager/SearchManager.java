package com.sniper.es.manager;

import com.sniper.es.base.IndexType;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.projectx.elasticsearch.ClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:ES查询
 * User： JinHuaTao
 * Date：2017/4/14
 * Time: 15:36
 */

@Component
public class SearchManager {

    private static final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    @Autowired
    private ClientTemplate clientTemplate;


    /**
     * 根据keyId查询帖子
     */
    public Map<String, Object> findTopicByKeyId(IndexType indexType, String keyId, String indices) {
        Map<String, Object> resultMap = new HashMap<>();
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        QueryBuilder qb = QueryBuilders.idsQuery().addIds(keyId);
        ssb.query(qb);

        if(StringUtils.isNotBlank(indices)){
            SearchResponse response = search(ssb, indexType, SearchType.DEFAULT, indices);
            if (response.getHits().totalHits() > 0) {
                resultMap = response.getHits().getAt(0).getSource();
            }
        }
        return resultMap;
    }

    /**
     * 根据keyId查询帖子
     *
     * @author ycl
     * @date 2013-03-04
     */
    public Map<String, Object> findTopicByKeyId(IndexType indexType, String keyId) {
        return this.findTopicByKeyId(indexType, keyId, clientTemplate.getIndexName());
    }

    /**
     * ES检索
     */
    public SearchResponse search(final SearchSourceBuilder ssb, final IndexType indexType, final SearchType searchType, final String indices) {
       /* return clientTemplate.executeGet(new ClientCallback<SearchResponse>() {
            public ActionFuture<SearchResponse> execute(Client client) {
                SearchRequest request = Requests.searchRequest(indices).preference("_primary_first").types(indexType.name()).searchType(searchType).source(ssb);

                logger.info("search-" + request.toString());
                return client.search(request);
            }
        });*/

        return clientTemplate.executeGet((Client client)->{
            SearchRequest request = Requests.searchRequest(indices).preference("_primary_first").types(indexType.name()).searchType(searchType).source(ssb);
            logger.info("search-" + request.toString());
            return client.search(request);
        });
    }
}
