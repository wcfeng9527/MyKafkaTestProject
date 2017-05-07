package com.shijie99.wcf.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
/**
 * 不使用负载均衡去获取数据，而是指定partition
 * @author Administrator
 *
 */
public class MyKafkaConsumer3 {
	public static void main(String[] args) throws Exception{
		KafkaConsumer<String, String> consumer = KafkaUtil.getConsumer(String.class);
		if(consumer==null){
			throw new Exception("consumer not found");
		}
	    String topic = "test";
//	    TopicPartition partition0 = new TopicPartition(topic, 0);
//	    TopicPartition partition1 = new TopicPartition(topic, 1);
//	    consumer.assign(Arrays.asList(partition0, partition1));
	    consumer.subscribe(Arrays.asList(topic));
		while(true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for(ConsumerRecord<String, String> record : records) {
				System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
			}
			//在测试中我发现，如果用手工指定partition的方法拉取消息，不知为何kafka的自动提交offset机制会失效，必须使用手动方式才能正确提交已消费的消息offset。
//			consumer.commitSync();
		}
	}
}
