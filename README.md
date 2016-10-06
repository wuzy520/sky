# sky rpc 框架
大部分rpc框架都利用了繁琐的配置文件，有的甚至依赖Spring，为了解放繁琐的配置，利用业余时间写了一个Rpc框架，用作学习之用，底层使用netty作为网络传输层，
使用Zookeeper作为服务的注册与发现，不管是服务端注册和客户端调用，代码都尽量保持简洁，欢迎大家多提意见。


服务端注册服务代码如下：
        //配置相关信息，如端口号等
        
        //服务具体的实现类,可以结合guice或者Spring进行整合
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
                .addService(UserDao.class)//添加需要暴露的接口服务
                .registry("localhost:2181")//zookeeper的注册中心
                .bind(8000);//端口号
        //启动服务
        context.start();






     客户端代码如下：
     //配置相关属性，例如服务器地址、端口号、最大等待时间。。。
               
             ClientContext clientContext = ClientContext.builder()
                    .option(ConfigOption.WAIT_TIMEOUT, 1000)//最大等待服务器时间为1000毫秒
                    .option(ConfigOption.CONNECT_TIMEOUT_MILLIS,3000)//最大等待连接时间3000毫秒
                    .registry("localhost:2181")//zookeeper 注册中心
                    .addService(UserDao.class);//添加要使用的服务接口
               
                  //启动服务,启动服务在实际使用中应该只初始化一次。
                  clientContext.start();
                  UserDao a = clientContext.get(UserDao.class);
                  String ret = a.findById("hh=="+(j++));
                  System.out.println("ret====" + ret);
