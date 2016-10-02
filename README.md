# sky
大部分rpc框架都利用了繁琐的配置文件，有的甚至依赖Spring，为了解放繁琐的配置，利用业余时间写了一个Rpc框架，用作学习之用，底层使用netty作为网络传输层，
这一版本比较简单，后期会加上Zookeeper用作服务调动使用。


服务端注册服务代码如下：
        //配置相关信息，如端口号等
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
        //启动服务
        context.start();






客户端代码如下：
 //配置相关属性，例如服务器地址、端口号、最大等待时间。。。
 ClientContext clientContext = ClientContext.create()
                .option(ConfigOption.SERVER, "localhost")//RPC 服务器地址
                .option(ConfigOption.PORT, 8000)//RPC 服务器端口号
                .option(ConfigOption.WAIT_TIMEOUT, 1000)//最大等待服务器时间为100毫秒
                .start();

        //获得业务类，调用相关方法
        UserDao a = clientContext.get(UserDao.class);
        String ret = a.findById("100");
        System.out.println("ret===="+ret);
