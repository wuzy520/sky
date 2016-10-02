package com.wuzy.sky.example;

import com.wuzy.sky.server.ServerContext;
import com.wuzy.sky.server.iface.IServiceInitializer;

/**
 * Created by apple on 2016/10/2.
 */
public class ServerTest {

    public static void main(String[] args) {
        ServerContext context = ServerContext.create(new IServiceInitializer() {
            @Override
            public void init() {

            }

            @Override
            public Object getImpl(Class<?> iface) {
                if (iface==UserDao.class){
                    return new UserDaoImpl();
                }
                return null;
            }
        }).port(8000);

        context.start();
    }
}
