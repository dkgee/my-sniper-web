package com.sniper.es.searcher;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Iterator;

/**
 * Description:ES Java API操作参考样例
 * User： JinHuaTao
 * Date：2017/4/18
 * Time: 12:00
 */

public class SampleSearcher {

        /**
         * ES数据检索参考
         * */
        public void searchSample(){
            //根查询，所有的查询最后都会封装成 SearchSourceBuilder
            SearchSourceBuilder ssb = new SearchSourceBuilder();
            String keyId = "";
            //1,根据Id查询
            QueryBuilder qb = QueryBuilders.idsQuery().addIds(keyId);
            ssb.query(qb);

            //2，布尔查询精确匹配字段查询， must:包含， mustNot:不包含, should:应该包含
            QueryBuilder qb2 = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("titleKeyId", ""))
                    .must(QueryBuilders.termQuery("postTime", ""));
            ssb.query(qb2).size(1);

            //3,模糊查询
            QueryBuilder qb3 = QueryBuilders.queryStringQuery("content").field("title").defaultOperator(Operator.OR);
            ssb.query(qb3);

            //4,通配符查询
            QueryBuilder qb4 = QueryBuilders.wildcardQuery("statusList", "");
            ssb.query(qb4);

            //5,模糊查询
            QueryBuilder qb5 = QueryBuilders.fuzzyQuery("content", "");
            ssb.query(qb5);

            //6,范围查询
            RangeQueryBuilder rqb = QueryBuilders.rangeQuery("evidenceTime").gte("beginTime").lte("endTime");//时间大于小于
            ssb.query(rqb);

            QueryBuilder rqb2 = QueryBuilders.rangeQuery("evidenceStatus").from("").to(null);//数据范围
            ssb.query(rqb2);

            //7，排序
            ssb.sort("", SortOrder.DESC);//7.1 直接使用field

            SortBuilder  sort = SortBuilders.fieldSort("").order(SortOrder.DESC);//7.2使用builder
            ssb.sort(sort);
        }

        /**
         *  ES聚合操作参考样例
         *      http://blog.csdn.net/u012116196/article/details/51789253
         * */
        public void aggSample(){
            //1,Min聚合
            AggregationBuilder minAggregation = AggregationBuilders.min("min_agg").field("height");// 最小高度值聚合
            SearchSourceBuilder ssb = null;
            ssb.aggregation(minAggregation); // 添加至查询中
            SearchResponse sr = null;
            Min minAgg = sr.getAggregations().get("min_agg");//构建搜索相应
            double minAggValue = minAgg.getValue();

            //2,Max聚合
            AggregationBuilder maxAggregation = AggregationBuilders.max("max_agg").field("height");
            ssb.aggregation(maxAggregation);
            Max maxAgg = sr.getAggregations().get("max_agg");
            double maxValue = maxAgg.getValue();

            //3,Sum聚合
            AggregationBuilder sumAggregation = AggregationBuilders.sum("sum_agg").field("height");
            ssb.aggregation(sumAggregation);
            Sum sumAgg = sr.getAggregations().get("sum_agg");
            double sumValue = sumAgg.getValue();

            //4,Avg聚合
            AggregationBuilder avgAggregation = AggregationBuilders.avg("avg_agg").field("height");
            ssb.aggregation(avgAggregation);
            Avg avgAgg = sr.getAggregations().get("avg_agg");
            double avgValue = avgAgg.getValue();

            //5,Stats统计    统计总条数、最大值、最小值、平均值、求和数
            AggregationBuilder statsAggregation = AggregationBuilders.stats("stats_agg").field("height");
            ssb.aggregation(statsAggregation);
            Stats statsAgg = sr.getAggregations().get("stats_agg");
            long count = statsAgg.getCount();
            double max = statsAgg.getMax();
            double min = statsAgg.getMin();
            double avg = statsAgg.getAvg();
            double sum = statsAgg.getSum();

            //6,Extend Stats  （扩展数据聚合）
            AggregationBuilder extendedStatsAggregation = AggregationBuilders.extendedStats("extend_stats_agg").field("height");
            ssb.aggregation(extendedStatsAggregation);
            ExtendedStats extendedStats = sr.getAggregations().get("extend_stats_agg");
            long ecount = extendedStats.getCount();
            double emax = extendedStats.getMax();
            double emin = extendedStats.getMin();
            double eavg = extendedStats.getAvg();
            double esum = extendedStats.getSum();
            double stdDeviation = extendedStats.getStdDeviation();//标准差
            double sumOfSquares = extendedStats.getSumOfSquares();//平方和
            double variance = extendedStats.getVariance();//方差

            //7, ValueCount  (求条数)
            AggregationBuilder valueCountAggregation = AggregationBuilders.count("value_count_agg").field("height");
            ssb.aggregation(valueCountAggregation);
            ValueCount valueCountAgg = sr.getAggregations().get("value_count_agg");
            long value = valueCountAgg.getValue();

            //8,Percentile （百分位聚合）
            AggregationBuilder percentAggregation = AggregationBuilders.percentiles("percent_agg").field("height");
//            AggregationBuilder percentAggregation = AggregationBuilders.percentiles("percent_agg").field("height")
//                    .percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0);//使用自定义百分位
            ssb.aggregation(percentAggregation);
            Percentiles percentiles = sr.getAggregations().get("percent_agg");
            for (Percentile entry: percentiles){
                double ppercent = entry.getPercent();
                double pvalue = entry.getValue();
            }

            //9,PercentileRanks （百分位排名聚合）
            AggregationBuilder percentRankAggregation = AggregationBuilders.percentileRanks("percent_rank_agg").field("height");
            ssb.aggregation(percentAggregation);
            PercentileRanks percentileRanksAgg = sr.getAggregations().get("percent_rank_agg");
            for(Percentile entry: percentileRanksAgg){
                double prpercent = entry.getPercent();
                double prvalue = entry.getValue();
            }
            /**
             * 此外还有  基数聚合、点击率最高聚合、脚本化的度规聚合等，根据需要查询
             * */


            //分组过滤统计参考样例
            RangeQueryBuilder todayrqb = QueryBuilders.rangeQuery("postTime")
                    .gte("2017-02-05 00:00:00")
                    .lte("2017-05-05 00:00:00");
            FilterAggregationBuilder todayAgg = AggregationBuilders.filter("tAgg", todayrqb)
                    .subAggregation(AggregationBuilders.terms("t").field("domain").size(1));
            ssb.aggregation(todayAgg);
            String[] fields = { "tAgg", "tiAgg", "yAgg", "yiAgg" };
            long todayCount = 0;
            for (String field : fields) {
                InternalFilter filter =  sr.getAggregations().get(field);
                if(filter != null){
                    Iterator<Aggregation> it = filter.getAggregations().iterator();
                    while (it.hasNext()){
                        Aggregation aggregation = it.next();
                        long countt = ((StringTerms)aggregation).getBuckets().get(0).getDocCount();
                        String name = aggregation.getName();
                        if("t".equals(name)){
                            todayCount =  count;
                        }
                    }
                }
            }


        }

        /**
         * 分面搜索参考示例
         *      http://www.cnblogs.com/huangfox/p/3636604.html
         * */
        public void facetSample(){

        }

}
