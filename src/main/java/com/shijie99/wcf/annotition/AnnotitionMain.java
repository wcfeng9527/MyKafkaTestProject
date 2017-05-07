package com.shijie99.wcf.annotition;

public class AnnotitionMain {
	@WxyResource
    private static AnnotitionTest annotitionTest;
	
	public static void main(String[] args) {
		WxyClassPathXMLApplicationContext context = new WxyClassPathXMLApplicationContext();
		context.annotationInject();
		annotitionTest.method1();
	}
}
