package com.shijie99.wcf.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaUtil {
	private static KafkaProducer<String, MyLog> kp;
	private static KafkaConsumer<String, MyLog> kc;

	public static KafkaProducer<String,MyLog> getProducer() {
		if (kp == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "10.0.0.100:9092,10.0.0.101:9092");
			props.put("acks", "1");
			props.put("retries", 0);
			props.put("batch.size", 16384);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "com.shijie99.wcf.kafka.KeywordMassage");
			kp = new KafkaProducer<String, MyLog>(props);
		}
		return kp;
	}
	
	public static KafkaConsumer<String, MyLog> getConsumer() {
		if(kc == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "10.0.0.100:9092,10.0.0.101:9092");
			props.put("group.id", "1");
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "com.shijie99.wcf.kafka.KeywordMassage");
			kc = new KafkaConsumer<String, MyLog>(props);
		}
		return kc;
	}
}
