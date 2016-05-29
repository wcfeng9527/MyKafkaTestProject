package com.shijie99.wcf.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 * 使用负载均衡获取数据
 * @author Administrator
 *
 */
public class MyKafkaConsumer {

	public static void main(String[] args) throws Exception{
		KafkaConsumer<String,MyLog> consumer = KafkaUtil.getConsumer(MyLog.class);
		if(consumer==null){
			throw new Exception("consumer not found");
		}
		//这里设置subscribe 中topic就会自动分配patition,而且只会分配1个patition(目前还不知道怎么能默认获取全部patitions)
//		consumer.subscribe(Arrays.asList("test2"));
		//可以通过自己设置TopicPartition类实例传给consumer，来明确分配patition
		TopicPartition tp = new TopicPartition("test2", 0);
		TopicPartition tp2 = new TopicPartition("test2", 1);
		consumer.assign(Arrays.asList(tp,tp2));
		//对该patition从offset0开始获取
		consumer.seekToBeginning(tp);
		while(true) {
			ConsumerRecords<String,MyLog> records = consumer.poll(10);
			for(ConsumerRecord<String, MyLog> record : records) {
				System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
			}
		}
	}

}
