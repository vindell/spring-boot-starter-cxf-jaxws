/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.spring.boot.jaxws.proxy;


import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 * @className	： JavassistProxyFactory
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2018年2月25日 下午3:18:26
 * @version 	V1.0 
 * @param <T>
 * @see http://blog.csdn.net/mingxin95/article/details/51810499
 */
@SuppressWarnings( { "deprecation", "unchecked" })
public class JavassistProxyFactory {

	/*
	 * 要代理的对象
	 */
    public static <T> T getProxy(T target) throws InstantiationException, IllegalAccessException {
    	 return (T) getProxy(target.getClass());
    }
    
    /*
     * 要代理的对象class
     */
    public static <T> T getProxy(Class<T> proxyClass) throws InstantiationException, IllegalAccessException {
        
    	// 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        proxyFactory.setSuperclass(proxyClass);
        
        /*
         * 定义一个拦截器。在调用目标方法时，Javassist会回调MethodHandler接口方法拦截，
         * 来实现你自己的代理逻辑，
         * 类似于JDK中的InvocationHandler接口。
         */
        proxyFactory.setHandler(new MethodHandler() {
            /*
             * self为由Javassist动态生成的代理类实例，
             *  thismethod为 当前要调用的方法
             *  proceed 为生成的代理类对方法的代理引用。
             *  Object[]为参数值列表，
             * 返回：从代理实例的方法调用返回的值。
             * 
             * 其中，proceed.invoke(self, args);
             * 
             * 调用代理类实例上的代理方法的父类方法（即实体类ConcreteClassNoInterface中对应的方法）
             */
            public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                System.out.println("--------------------------------");
                System.out.println(self.getClass());
                //class com.javassist.demo.A_$$_javassist_0
                System.out.println("代理类对方法的代理引用:"+thismethod.getName());
                System.out.println("开启事务 -------");

                Object result = proceed.invoke(self, args);

                System.out.println("提交事务 -------");
                return result;
            }
        });
        
        // 通过字节码技术动态创建子类实例
        return  (T) proxyFactory.createClass().newInstance();
    }

}