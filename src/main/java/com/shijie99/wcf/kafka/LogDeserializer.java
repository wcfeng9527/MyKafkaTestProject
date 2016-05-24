package com.shijie99.wcf.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class LogDeserializer<T> implements Deserializer<T> {
	private String encoding = "UTF8";

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		String propertyName = isKey ? "key.deserializer.encoding"
				: "value.deserializer.encoding";
		Object encodingValue = configs.get(propertyName);
		if (encodingValue == null)
			encodingValue = configs.get("deserializer.encoding");
		if (encodingValue != null && encodingValue instanceof String)
			encoding = (String) encodingValue;

	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(String topic, byte[] data) {
		 try {
	            if (data == null)
	                return null;
	            else{
//	         		return JSON.parseObject(new String(data, encoding), MyLog.class);
	            	ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data));
	            	return (T)is.readObject();
	            }
             
	        } catch (UnsupportedEncodingException e) {
	            throw new SerializationException("Error when deserializing byte[] to MyLog due to unsupported encoding " + encoding);
	        } catch (IOException e) {
				throw new SerializationException("Error when deserializing byte[] to MyLog");
			} catch (ClassNotFoundException e) {
				//获取数据类型
				Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
		                .getGenericSuperclass()).getActualTypeArguments()[0];
				throw new SerializationException("Error when deserializing byte[] to MyLog,Class "+clazz.getClass().getName()+" not funod");
			}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
