package com.shijie99.wcf.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class MyKafkaConsumer {

	public static void main(String[] args) throws Exception{
		KafkaConsumer<String, MyLog> consumer = KafkaUtil.getConsumer();
		consumer.subscribe(Arrays.asList("test"));
		while(true) {
			ConsumerRecords<String,MyLog> records = consumer.poll(1000);
			for(ConsumerRecord<String, MyLog> record : records) {
				System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
			}
		}
	}

}
