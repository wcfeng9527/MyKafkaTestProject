package com.shijie99.wcf.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
/**
 * 不使用负载均衡去获取数据，而是指定partition
 * @author Administrator
 *
 */
public class MyKafkaConsumer5 {
	public static void main(String[] args) throws Exception{
		Properties props = new Properties();
		props.put("bootstrap.servers","192.168.149.132:9092");
		// Consumer的group
		// id，同一个group下的多个Consumer不会拉取到重复的消息，不同group下的Consumer则会保证拉取到每一条消息。
		// 注意，同一个group下的consumer数量不能超过分区数
		props.put("group.id", "shijie99_qm_log_basestr");
		// 每次最小拉取的消息大小（byte）。Consumer会等待消息积累到一定尺寸后进行批量拉取。默认为1，代表有一条就拉一条
		props.put("fetch.min.bytes", 100);
		// 每次从单个分区中拉取的消息最大尺寸（byte），默认为1M
		props.put("max.partition.fetch.bytes", 1024 * 1024);
		// 是否自动提交已拉取消息的offset。
		// 提交offset即视为该消息已经成功被消费，该组下的Consumer无法再拉取到该消息（除非手动修改offset）。默认为true
		props.put("enable.auto.commit", "true");
		// 自动提交offset的间隔毫秒数，默认5000。
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		// key序列化对象
		props.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		// value反序列化对象，自定义对象，可反序列化实现了Serializer接口的对象
		props.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
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
