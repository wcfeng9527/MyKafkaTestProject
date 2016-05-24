package com.shijie99.wcf.kafka;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class KeywordMassage implements Serializer<MyLog> {
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

	@Override
	public byte[] serialize(String topic, MyLog data) {
		try {
			if (data == null) {
				return null;
			} else {
				return data.toString().getBytes(encoding);
			}
		} catch (UnsupportedEncodingException e) {
			throw new SerializationException(
					"Error when serializing string to byte[] due to unsupported encoding "
							+ encoding);
		}
	}

	@Override
	public void close() {
		// noting todo
	}

}
