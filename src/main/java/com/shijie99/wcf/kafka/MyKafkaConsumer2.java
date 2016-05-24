package com.shijie99.wcf.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
/**
 * 不使用负载均衡去获取数据，而是指定partition
 * @author Administrator
 *
 */
public class MyKafkaConsumer2 {
	public static void main(String[] args) throws Exception{
		KafkaConsumer<String, MyLog> consumer = KafkaUtil.getConsumer();
	    String topic = "test";
	    TopicPartition partition0 = new TopicPartition(topic, 0);
	    TopicPartition partition1 = new TopicPartition(topic, 1);
	    consumer.assign(Arrays.asList(partition0, partition1));
		while(true) {
			ConsumerRecords<String, MyLog> records = consumer.poll(100);
			for(ConsumerRecord<String, MyLog> record : records) {
				System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
			}
			//在测试中我发现，如果用手工指定partition的方法拉取消息，不知为何kafka的自动提交offset机制会失效，必须使用手动方式才能正确提交已消费的消息offset。
			consumer.commitSync();
		}
	}
}
