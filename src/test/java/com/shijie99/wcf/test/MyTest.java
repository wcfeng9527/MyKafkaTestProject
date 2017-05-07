package com.shijie99.wcf.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class MyTest {
	static Long s;
	
	@Test
	public void test(){
		System.out.println(s);
		List<Integer> list = new ArrayList<Integer>(10);
		list.add(1);
		Map<String,Object> map = new HashMap<String,Object>();
		System.out.println(map.put(null, 0));
		System.out.println(map.put(null, 1));
		System.out.println(map.put(null, 2));
		System.out.println(map.get(null));
		new LinkedList<Integer>();
//		new BlockingQueue<Integer>();
//		Callable<V>
		new ArrayBlockingQueue<>(10);
		new LinkedBlockingQueue<>(10);
		int i=101241;
		int j = i >> 1;
		System.out.println(j);
	}
	
}
