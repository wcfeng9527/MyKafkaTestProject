package com.shijie99.wcf.annotition;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WxyClassPathXMLApplicationContext {
    //存放BeanDefinition的列表，在beans.xml中定义的bean可能不止一个

//    private final List<BeanDefinition> beanDefines = new ArrayList<BeanDefinition>();

    //将类名作为索引，将创建的Bean对象存入到Map中

    private final static Map<String, Object>  sigletons   = new HashMap<String, Object>();
    static{
    	sigletons.put("com.shijie99.wcf.annotition.AnnotitionTest", new AnnotitionTest());
    	sigletons.put("com.shijie99.wcf.annotition.AnnotitionMain", new AnnotitionMain());
    }
	
    
    /** 

     * 使用注解方式注入对象方法实现

     * @throws IntrospectionException 

     */

    public void annotationInject() {

        //循环所有bean对象

        for (String beanName : sigletons.keySet()) {

            //获取bean对象

            Object bean = sigletons.get(beanName);

            //如果bean不为空,取得bean的属性

            if (bean != null) {

                try {

                    //按属性注入

                    PropertyDescriptor[] ps = Introspector.getBeanInfo(bean.getClass())

                        .getPropertyDescriptors();

                    for (PropertyDescriptor properdesc : ps) {

                        //获取属性的setter方法

                        Method setter = properdesc.getWriteMethod();

                        //判断注解是否存在

                        if (setter != null && setter.isAnnotationPresent(WxyResource.class)) {

                            //取得注解

                            WxyResource resource = setter.getAnnotation(WxyResource.class);

                            Object value = null;

                            //如果按名字找到

                            if (resource.name() != null && !"".equals(resource.name())) {

                                //取得容器中的bean对象

                                value = sigletons.get(resource.name());

 

                            } else {//没有按名字找到，按类型寻找

                                //取得容器中的bean对象

                                value = sigletons.get(resource.name());

                                if (value == null) {

                                    for (String key : sigletons.keySet()) {

                                        if (properdesc.getPropertyType().isAssignableFrom(

                                            sigletons.get(key).getClass())) {

                                            value = sigletons.get(key);

                                            break;

                                        }

                                    }

                                }

                            }

                            //把引用对象注入到属性

                            setter.setAccessible(true); 

                            setter.invoke(bean, value);

                        }

                    }

                    //按字段注入

                    Field[] fields = bean.getClass().getDeclaredFields();

                    for (Field field : fields) {

                        //如果注解存在

                        if (field.isAnnotationPresent(WxyResource.class)) {

                            //取得注解

                            WxyResource resource = field.getAnnotation(WxyResource.class);

                            Object value = null;

                            //如果按名字找到

                            if (resource.name() != null && !"".equals(resource.name())) {

                                //取得容器中的bean对象

                                value = sigletons.get(resource.name());

                            } else {//没有按名字找到，按类型寻找

                                //取得容器中的bean对象

                                value = sigletons.get(field.getName());

                                if (value == null) {

                                    for (String key : sigletons.keySet()) {

                                        if (field.getType().isAssignableFrom(

                                            sigletons.get(key).getClass())) {

                                            value = sigletons.get(key);

                                            break;

                                        }

                                    }

                                }

                            }

                            //允许访问private 

                            field.setAccessible(true);

                            field.set(bean, value);

                        }

                    }

                } catch (IntrospectionException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IllegalArgumentException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IllegalAccessException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (InvocationTargetException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                }

            }

        }

    }
}
