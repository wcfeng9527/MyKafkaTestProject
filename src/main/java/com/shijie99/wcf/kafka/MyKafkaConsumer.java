package com.shijie99.wcf.kafka;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

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
		consumer.subscribe(Arrays.asList("qm_log_basestr"));
		List<PartitionInfo> list = consumer.partitionsFor("qm_log_basestr");
		consumer.assignment();
		for (PartitionInfo partitionInfo : list) {
			System.out.println("========================"+partitionInfo.partition());
		}
		while(true) {
			ConsumerRecords<String,MyLog> records = consumer.poll(10);
			for(ConsumerRecord<String, MyLog> record : records) {
				System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
				consumer.commitAsync();
			}
		}
	}

}
