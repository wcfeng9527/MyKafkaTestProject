package com.shijie99.wcf.annotition;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import javax.annotation.Resource;


public class AnnotitionTest {
	
	@MyAnnotitionTest(id=1,description="hello")
	public void method1(){
		System.out.println("有对象了");
	}
	
	@MyAnnotitionTest(id=2)
	public void method2(){
		
	}
	
	@MyAnnotitionTest(id=3,description="method3")
	@Resource
	public void method3() throws IntrospectionException{
	}
	
	public static void main(String[] args) {
		Method[] methods = AnnotitionTest.class.getDeclaredMethods();
		for (Method method : methods) {
			boolean hasAnnotition = method.isAnnotationPresent(MyAnnotitionTest.class);
			if(hasAnnotition){
				MyAnnotitionTest annotition = method.getAnnotation(MyAnnotitionTest.class);
				System.out.println("MyAnnotitionTest(method="+method.getName()+",id="+annotition.id()+",description="+annotition.description()+")");
			}
		}
	}
}
