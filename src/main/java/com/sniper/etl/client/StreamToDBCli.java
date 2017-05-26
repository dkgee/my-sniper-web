package com.sniper.etl.client;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:读取Kafka数据流同步至DB，
 * User： JinHuaTao
 * Date：2017/4/20
 * Time: 16:35
 */

public class StreamToDBCli{

    private final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());

    public void start() {

    }


    /**
     * 采集数据处理器
     * */
    private static class CrawlDataProcessorSupplier implements ProcessorSupplier<String, String>{

        /**
         * 实现具体业务处理器
         * */
        @Override
        public Processor<String, String> get() {
            return new Processor<String, String>() {

                /**
                 * 初始化处理器上下文环境变量，例如设置上下文调度时间，建立数据库连接对象等
                 * */
                @Override
                public void init(ProcessorContext processorContext) {

                }

                /**
                 * 处理的从kafka的流中取的具体内容，进行操作。例如对其进行操作后，插入数据库或ES等
                 * */
                @Override
                public void process(String dummy, String line) {

                }

                /**
                 * 时间戳
                 * */
                @Override
                public void punctuate(long l) {

                }

                /**
                 * 关闭处理器
                 * */
                @Override
                public void close() {

                }
            };
        }
    }

}
