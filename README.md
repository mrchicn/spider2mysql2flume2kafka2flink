# spider2mysql2flume2kafka2flink
爬虫抓取的淘宝Python书信息写到mysql在用Flume监控传入Kafka再用Flink消费kafka的数据
# 开发工具：
    PyCharm 2018.3.5 x64
    IntelliJ IDEA 2018.3.5 x64
# 大数据环境：
    hadoop-2.7.4       搭建教程地址：http://www.mrchi.cn/2018/11/04/hadoop2-7-4-install-configration/
    zookeeper-3.4.5    搭建教程地址：http://www.mrchi.cn/2019/01/29/zookeeper-3-4-5/
    kafka_2.11-2.1.0   搭建教程地址：http://www.mrchi.cn/2019/01/30/kafka_2-11-2-1-0%E9%83%A8%E7%BD%B2/
    apache-flume-1.8.0-bin    搭建比较简单没有教程
    flink-1.7.1        搭建教程地址：http://www.mrchi.cn/2019/01/31/flink-1-7-1-%E9%9B%86%E7%BE%A4%E9%83%A8%E7%BD%B2/
    jdk1.8.0_201        开发语言环境就不用说了吧
    scala 2.11          开发语言环境就不用说了吧
    这里的数据没有写到Hadoop但是要写入Hadoop需要在Flume配置两个sink一个供kafka消费一个供Hadoop存储
# Python环境：
    python3.7
    selenium 测试框架
    pyquery  解析框架
    re  正则匹配
# MySQL表信息：
     CREATE TABLE `TB_Books` (
    `book_Url` varchar(255) DEFAULT NULL,
    `book_Address` varchar(1000) DEFAULT NULL,
    `book_Price` varchar(255) DEFAULT NULL,
    `book_Count` varchar(255) DEFAULT NULL,
    `book_Title` varchar(255) DEFAULT NULL,
    `book_Shop_Address` varchar(255) DEFAULT NULL,
    `shop_locate` varchar(255) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    #这里没有创建主键和外键生产环境是有的
    #由于书的地址比较长所以设置长度为1000
# Flume配置信息：
    配置信息比较长:
    ########定于三个必须组件################
    a1.channels = c1
    a1.sources = s1
    a1.sinks = k1
    ###################Sources#########################################
    a1.sources.s1.type = org.keedio.flume.source.SQLSource
    #mysql的链接地址
    a1.sources.s1.hibernate.connection.url = jdbc:mysql://192.168.1.10:3306/flume
    ##############Hibernate数据库连接属性################
    #登陆MySql的用户名
    a1.sources.s1.hibernate.connection.user = root
    #登陆MySql的用户密码
    a1.sources.s1.hibernate.connection.password = 123456
    #MySql的表名
    a1.sources.s1.table =TB_Books
    #自动提交
    a1.sources.s1.hibernate.connection.autocommit = true
    #驱动信息
    a1.sources.s1.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect
    a1.sources.s1.hibernate.connection.driver_class = com.mysql.jdbc.Driver
    #查询延迟5秒  也就是说5秒取一次数据
    a1.sources.s1.run.query.delay=5000
    #类似一个offset的配置这个是文件夹
    a1.sources.s1.status.file.path = /opt/software/TestData/flumeData
    a1.sources.s1.status.file.name = sql-source.status
    #起始位置
    a1.sources.s1.start.from = 0
    #查询语句
    a1.sources.s1.columns.to.select = *
    #增量的列名一般为主键 不为空的列
    a1.sources.s1.incremental.column.name =book_Url
    #增量起始位置
    a1.sources.s1.incremental.value = 0
    ##################channels####################################
    #channels的类型为内存
    a1.channels.c1.type=memory
    #容量
    a1.channels.c1.capacity=10000
    #事务处理能力
    a1.channels.c1.transactionCapacity=10000
    #字节容量缓冲百分比
    a1.channels.c1.byteCapacityBufferPercentage=20
    #字节容量
    a1.channels.c1.byteCapacity=800000
    ##################Sinks####################################
    #sink类型为kafka
    a1.sinks.k1.type=org.apache.flume.sink.kafka.KafkaSink
    #kafka的代理列表m,s1,s2
    a1.sinks.k1.brokerList = slave1:9092,slave2:9092,master:9092
    #kafka的主题
    a1.sinks.k1.topic=TB_Books
    #Acks为1
    a1.sinks.k1.requiredAcks = 1
    #批次大小为20
    a1.sinks.k1.batchSize = 20
    #连接到channel
    a1.sinks.k1.channel = c1
    a1.sources.s1.channel=c1
# Kafka配置信息：
    这里推荐一个软件比较好用可以查看topic内的数据信息在软件里可以直接创建主题和删除主题可视化操作
    《Kafka Tool 2.0》
    kafka的操作就简单的多了直接创建一个主题就可以拉。就是前期准备的工作比较多。
    首先启动zk  分别在三台虚机上启动
    启动zookeeper命令:
            zkServer.sh start
            然后在jps看一下看到有QuorumPeerMain这个名字就是啦
    三台机器都启动以后启动kafka，同样是三台机器都启动或者启动一个也可以
    启动kafka命令:
              cd /opt/software/kafka_2.11-2.1.0 在安装目录下启动
              kafka-server-start.sh config/server.properties >>/d
    然后是创建主题这里的主题是需要和Flume传如的主题相对应的上面的Flume下沉到Kafka的TB_Books主题所以需要创建TB_Books主题
    创建主题命令:
            kafka-topics.sh -create -zookeeper master:2181,slave1:2181,slave2:2181 -replication-factor 3 -partitions 1 -topic TB_Books
一切准备就绪将代码呈上来:Scala写的Flink消费kafka的代码 StreamingKafkaSourceScala
     代码里也需要配置kafka的代理,主题,和消费组
     在Java文件夹下面的是测试写入数据库用的






