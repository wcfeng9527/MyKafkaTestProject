package com.shijie99.wcf.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


public class MyKafkaProducter {
	public static void main(String[] args) throws Exception{
		Producer<String,MyLog> producer = KafkaUtil.getProducer(MyLog.class);
		
		if(producer ==null){
			throw new Exception("producer not found");
		}
		int i = 0;
		while(true) {
			ProducerRecord<String, MyLog> record = new ProducerRecord<String, MyLog>("test", String.valueOf(i), new MyLog(1,"message:"+i,"name:"+i));
			producer.send(record, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null)
						e.printStackTrace();
					System.out.println("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
				}
			});
			i++;
			Thread.sleep(1000);
		}
	}
}
