package com.shijie99.wcf.kafka;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Test {
	public static void main(String[] args) {
		MyLog myLog = new MyLog(1, "message:1", "name:1");
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream  os = new ObjectOutputStream(bo);
			os.writeObject(myLog);
			ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray()));
			MyLog myLog2 = (MyLog)is.readObject();
			System.out.println(myLog2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
