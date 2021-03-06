


ES聚合(Aggregation)
    桶(Buckets):满足特定条件的一个文档集合,可以分桶，分桶是达到最终目的的手段：提供了对文档进行划分的方法，从而让你能够计算需要的指标。
        桶1
        桶2...
        桶n

    指标(Metrics):最终对每个桶中的文档进行某种指标计算

    注明：一个聚合就是一些桶和指标的组合。一个聚合可以只有一个桶，或者一个指标，或者每样一个。在桶中甚至可以有多个嵌套的桶。比如，我们
        可以将文档按照其所属国家进行分桶，然后对每个桶计算其平均薪资(一个指标)。

    因为桶是可以嵌套的，我们能够实现一个更加复杂的聚合操作：
        将文档按照国家进行分桶。(桶1)
        然后将每个国家的桶再按照性别分桶。(桶2)
        然后将每个性别的桶按照年龄区间进行分桶。(桶3)
        最后，为每个年龄区间计算平均薪资。(指标)
    此时，就能够得到每个<国家，性别，年龄>组合的平均薪资信息了。它可以通过一个请求，一次数据遍历来完成！

    分桶的目的是为了获取某个指标。

    缩写 Aggs

    在检索范围确定之后，ES 还支持对结果集做聚合查询，返回更直接的聚合统计结果。在 ES 1.0 版本之前，这个接口叫 Facet，1.0 版本之后，这个接口改为 Aggregation。
    Kibana 分别在 v3 中使用 Facet，v4 中使用 Aggregation。不过总的来说，Aggregation 是 Facet 接口的强化升级版本，我们直接了解 Aggregation 即可。

    在 Elasticsearch 1.x 系列中，aggregation 分为 bucket 和 metric 两种，分别用作词元划分和数值计算。而其中的 bucket aggregation，还支持在自身结果集的基础上，
    叠加新的 aggregation。这就是 aggregation 比 facet 最领先的地方。比如实现一个时序百分比统计，在 facet 接口就无法直接完成，而在 aggregation 接口就很简单了
