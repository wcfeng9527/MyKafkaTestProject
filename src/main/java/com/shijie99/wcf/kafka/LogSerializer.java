package com.shijie99.wcf.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class LogSerializer<T> implements Serializer<T> {
	private String encoding = "UTF8";

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		//get setting encoding
		String propertyName = isKey ? "key.serializer.encoding"
				: "value.serializer.encoding";
		Object encodingValue = configs.get(propertyName);
		if (encodingValue == null)
			encodingValue = configs.get("serializer.encoding");
		if (encodingValue != null && encodingValue instanceof String)
			encoding = (String) encodingValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(String topic, T data) {
		try {
			if (data == null) {
				return null;
			} else {
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream  os = new ObjectOutputStream(bo);
				os.writeObject(data);
				return bo.toByteArray();
				//使用序列化对象方式转换成字节
				//return data.toString().getBytes(encoding);
			}
		} catch (UnsupportedEncodingException e) {
			throw new SerializationException(
					"Error when serializing MyLog to byte[] due to unsupported encoding "
							+ encoding);
		} catch (IOException e) {
			//获取数据类型
			Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
	                .getGenericSuperclass()).getActualTypeArguments()[0];
			throw new  SerializationException("Error when serializing "+clazz.getClass().getName()+" to byte[]");
		}
	}

	@Override
	public void close() {
		// noting todo
	}


}
