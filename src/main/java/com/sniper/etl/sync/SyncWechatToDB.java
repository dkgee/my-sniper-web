package com.sniper.etl.sync;

import com.sniper.etl.base.EtlConsts;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Description: 同步Kafka中wechat数据至数据库中
 * User： JinHuaTao
 * Date：2017/4/20
 * Time: 17:02
 */

public class SyncWechatToDB {

    private final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());

    private Properties initConfig(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, EtlConsts.SYNC_WECHAT_TO_DB_PROCESSOR_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.5.111:6667");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "hmly1:2181");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, "5");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    public void start() {
        Properties props = initConfig();

        TopologyBuilder builder = new TopologyBuilder();
        builder.addSource("Source", EtlConsts.WECHAT_CRAWLDATA_TOPIC)
                .addProcessor("Wechat-into-DBProcess", new WechatProcessorSupplier(), "Source");

        final KafkaStreams kafkaStreams = new KafkaStreams(builder, props);
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> { kafkaStreams.close();}));

    }

    public static void main(String[] args) {
        new SyncWechatToDB().start();
    }

    /**
     * 采集数据处理器
     * */
    private static class WechatProcessorSupplier implements ProcessorSupplier<String, String> {

        /**
         * 实现具体业务处理器
         * */
        @Override
        public Processor<String, String> get() {
            return new Processor<String, String>() {

                private ProcessorContext context;

                /**
                 * 初始化处理器上下文环境变量，例如设置上下文调度时间，建立数据库连接对象等
                 * */
                @Override
                public void init(ProcessorContext processorContext) {
                    System.out.println("初始化-------");
                }

                /**
                 * 处理的从kafka的流中取的具体内容，进行操作。例如对其进行操作后，插入数据库或ES等
                 * */
                @Override
                public void process(String dummy, String line) {
                    System.out.println("处理数据:" + line);
                }

                /**
                 * 时间戳
                 * */
                @Override
                public void punctuate(long timestamp) {
                    this.context.commit();
                }

                /**
                 * 关闭处理器
                 * */
                @Override
                public void close() {
                    this.context.commit();
                }
            };
        }
    }
}
