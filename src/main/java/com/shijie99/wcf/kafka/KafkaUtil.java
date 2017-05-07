package com.shijie99.wcf.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaUtil {
	private static KafkaProducer<String, ?> kp;
//	private static KafkaProducer<String, String> kp;
	private static KafkaConsumer<String, ?> kc;

	@SuppressWarnings("unchecked")
	public static <T> KafkaProducer<String, T> getProducer(Class<T> clazz) {
		if (kp == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers","192.168.149.132:9092");
			props.put("acks", "1");
			props.put("retries", 0);
			props.put("batch.size", 16384);
			props.put("key.serializer",
					"org.apache.kafka.common.serialization.StringSerializer");
			//延迟推送消息设置，设置该配置后，会延迟一定时间将消息推送到服务器，也就实现了批量推送的目的
			props.put("linger.ms", 5000);
			// value序列化对象，自定义对象，可序列化实现了Serializer接口的对象
//			 props.put("value.serializer","com.shijie99.wcf.kafka.LogSerializer");
			props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
			kp = new KafkaProducer<String, T>(props);
		}
		return (KafkaProducer<String, T>) kp;
	}

	// public static KafkaProducer<String, String> getProducer() {
	// if (kp == null) {
	// Properties props = new Properties();
	// props.put("bootstrap.servers",
	// "192.168.31.211:9092,192.168.31.211:9093");
	// props.put("acks", "1");
	// props.put("retries", 0);
	// props.put("batch.size", 16384);
	// props.put("key.serializer",
	// "org.apache.kafka.common.serialization.StringSerializer");
	// props.put("value.serializer",
	// "org.apache.kafka.common.serialization.StringSerializer");
	// kp = new KafkaProducer<String, String>(props);
	// }
	// return kp;
	// }

	@SuppressWarnings("unchecked")
	public static <T> KafkaConsumer<String, T> getConsumer(Class<T> clazz) {
		if (kc == null) {
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
			kc = new KafkaConsumer<String, T>(props);
		}
		return (KafkaConsumer<String, T>) kc;
	}
}
