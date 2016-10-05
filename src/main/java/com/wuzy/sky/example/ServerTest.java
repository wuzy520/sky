package com.wuzy.sky.example;

import com.wuzy.sky.exception.ServiceException;
import com.wuzy.sky.server.ServerContext;
import com.wuzy.sky.server.iface.IServiceInitializer;

/**
 * Created by apple on 2016/10/2.
 */
public class ServerTest {

    public static void main(String[] args) throws ServiceException {
        //服务具体的实现类,可以结合guice和Spring进行整合
        IServiceInitializer initializer = new IServiceInitializer() {
            @Override
            public void init() {

            }
            @Override
            public Object getImpl(Class<?> iface) {
                if (iface == UserDao.class) {
                    return new UserDaoImpl();
                }
                return null;
            }
        };

        //服务启动类
        ServerContext context = ServerContext.create(initializer)
                .addService(UserDao.class)//添加需要运行的接口服务
                .registry("localhost:2181")//zookeeper的注册中心
                .bind(8000);//端口号
        //启动服务
        context.start();

    }
}
