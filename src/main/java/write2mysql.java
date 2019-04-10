import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class write2mysql {

    Connection coon = null;

    public write2mysql() {
        try {
            //            加载驱动程序 新版本写法
            Class.forName("com.mysql.cj.jdbc.Driver");
            coon = DriverManager.getConnection("jdbc:mysql://192.168.1.10:3306/flume", "root", "123456");
            if (!coon.isClosed()) {
                System.out.println("成功连接数据库！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    插入数据
    public void add() {
        String template = "INSERT INTO `flume`.`logs` (`id`, `time_stamp`, `category`, `type`, `servername`, `code`, `msg`) VALUES (";
        for (int i = 200; i < 400; i++) {
            String sql = "\'" + i + "\', 'apr-8-2014-7:06:22-pm-pdt', 'notice', 'weblogicserver', 'adminserver', 'bea-000360', 'server started in running mode');";
            try {
                PreparedStatement preStmt = coon.prepareStatement(template + sql);
                preStmt.executeUpdate();
                Thread.sleep(1000);
                System.out.println("插入数据成功！"+i);
                preStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String [] args){
        new write2mysql().add();
    }

}
