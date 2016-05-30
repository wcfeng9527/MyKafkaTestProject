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
			ProducerRecord<String, MyLog> record = new ProducerRecord<String, MyLog>("qm_log_basestr", String.valueOf(i), new MyLog(1,"message:"+i,"name:"+i));
			producer.send(record, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null)
						e.printStackTrace();
					System.out.println("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
				}
			}).get();
			i++;
			Thread.sleep(1000);
		}
	}
	
//	public static void main(String[] args) throws Exception{  
//        Producer<String, String> producer = KafkaUtil.getProducer();  
//        int i = 0;  
//        while(true) {  
//            ProducerRecord<String, String> record = new ProducerRecord<String, String>("test4", String.valueOf(i), "this is message"+i);  
//            producer.send(record, new Callback() {  
//                public void onCompletion(RecordMetadata metadata, Exception e) {  
//                    if (e != null)  
//                        e.printStackTrace();  
//                    System.out.println("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());  
//                }  
//            });  
//            i++;  
//            Thread.sleep(1000);  
//        }  
//    }  
}
