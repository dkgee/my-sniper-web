package com.sniper.es.searcher;

import com.sniper.es.base.IndexType;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:查询ES
 * User： JinHuaTao
 * Date：2017/4/15
 * Time: 11:28
 */
@Component
public class SniperSearcher extends BaseSearcher{

    /**
     * 每次只传一个条件查询
     */
    public SearchHit[] search(String indices, final IndexType indexType, QueryBuilder queryBuilder, SortBuilder sortBuilder) {
        SearchSourceBuilder ssb  = new SearchSourceBuilder();
        ssb.query(queryBuilder);

       if (sortBuilder != null)
            ssb.sort(sortBuilder);

        SearchResponse response =  search(indices, indexType, ssb,SearchType.DEFAULT);
        return response.getHits().getHits();
    }

    /**
     * 每次可传多个条件查询
     */
    public SearchHit[] search(String indices, final IndexType indexType, SearchSourceBuilder ssb, SortBuilder sortBuilder) {
        if (sortBuilder != null)
            ssb.sort(sortBuilder);

        SearchResponse response =  search(indices, indexType, ssb,SearchType.DEFAULT);
        return response.getHits().getHits();
    }

    /**
     * 分页查询
     * @param indices
     * @param indexType
     * @param queryBuilder
     * @param offset
     * @param size
     * @param sortBuilder
     * @return
     */
    public SearchHit[] search(String indices, final IndexType indexType, QueryBuilder queryBuilder, int offset, int size, SortBuilder sortBuilder) {
        SearchSourceBuilder ssb  = new SearchSourceBuilder();
        ssb.query(queryBuilder);

        if(offset > 0) ssb.from(offset);

        if(size > 0) ssb.size(size);

        if (sortBuilder != null)
            ssb.sort(sortBuilder);

        SearchResponse response = search(indices, indexType, ssb,SearchType.DEFAULT);
        return response.getHits().getHits();
    }

    /**
     * Count docs
     *
     * @param indices
     * @param indexType
     * @param queryBuilder
     * @return
     */
    public long count(final String indices, final IndexType indexType, QueryBuilder queryBuilder) {
        SearchSourceBuilder ssb  = new SearchSourceBuilder();
        ssb.query(queryBuilder);
        SearchResponse response = search(indices, indexType, ssb,SearchType.DEFAULT);
        return  response.getHits().getTotalHits();
    }

    /**
     * Count docs
     *
     * @param indexType
     * @param queryBuilder
     * @return
     */
    public long count(final IndexType indexType, QueryBuilder queryBuilder) {
        SearchSourceBuilder ssb  = new SearchSourceBuilder();
        ssb.query(queryBuilder);
        SearchResponse response = search(clientTemplate.getIndexName(), indexType, ssb,SearchType.DEFAULT);
        return  response.getHits().getTotalHits();
    }

    /**
     *
     * @param indices
     * @param indexType
     * @param id
     * @return
     */
    public Map<String, Object> getById(String indices,IndexType indexType, String id) {
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        Map<String, Object> resultMap = new HashMap<>();

        QueryBuilder qb = QueryBuilders.idsQuery().addIds(id);
        ssb.query(qb);
        if(StringUtils.isNotBlank(indices)){
            SearchResponse response = search(indices, indexType, ssb,SearchType.DEFAULT);
            if (response.getHits().totalHits() > 0) {
                resultMap = response.getHits().getAt(0).getSource();
            }
        }
        return resultMap;
    }

    /**
     *
     * @param indexType
     * @param id
     * @return
     */
    public Map<String, Object> get(IndexType indexType, String id) {
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        Map<String, Object> resultMap = new HashMap<>();

        QueryBuilder qb = QueryBuilders.idsQuery().addIds(id);
        ssb.query(qb);
        SearchResponse response = search(clientTemplate.getIndexName(), indexType, ssb,SearchType.DEFAULT);
        if (response.getHits().totalHits() > 0) {
            resultMap = response.getHits().getAt(0).getSource();
        }
        return resultMap;
    }


}
