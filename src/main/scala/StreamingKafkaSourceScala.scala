import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.{CheckpointConfig, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

object StreamingKafkaSourceScala {

//  设置常量zookeeper信息/kafkabroker信息/要消费的组信息
  var ZOOKEEPER_HOST="master:2181,slave1:2181,slave2:2181"
  var KAFKA_BROKER="master:9092,slave1:9092,slave2:9092"
  var KAFKA_GROUP ="MRCHI"

  def main(args: Array[String]): Unit = {
//    获取Flink运行环境也就是入口点
    val env=StreamExecutionEnvironment.getExecutionEnvironment
//    设置检查点时间为5秒
    env.enableCheckpointing(5000)
//    设置检查模式  恰好一次
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
//    设置检查点之间的最小暂停时间
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
//    设置检查点超时 60秒
    env.getCheckpointConfig.setCheckpointTimeout(60000)
//    设置最大并发检查点
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
//    外部的检查点  保留撤销
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

//    没有设置检查点请自行添加
//    env.getStateBackend(new FsStateBackend("hdfs://master:9000/flink/checkpoints"))


//    要消费的主题
    val topic="TB_Books"
//    kafka的配置信息
    val kafkaProps=new Properties()

    kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST)
    kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKER)
    kafkaProps.setProperty("group.id", KAFKA_GROUP)
//              Flink消费kafka 类型是String 3个参数 一个是主题，一个是简单消费一个是kafka的配置信息
//    FlinkKafkaConsumer011  这个是Scala2.11版本的
    val Consumer= new FlinkKafkaConsumer011[String](topic,new SimpleStringSchema(),kafkaProps)
//    将kafka信息添加进去
    val result=env.addSource(Consumer)
//  打印结果
    result.print()
//    result

//    execute可以带参数  是Job的名字
    env.execute("StreamingFromKafkaScala")
  }
}
