package com.zzp;

import java.sql.*;

/**创建JDBC应用程序的步骤
 * 1.载入JDBC驱动程序
 * 2.定义连接URL
 * 3.建立连接
 * 4.创建Statement对象
 * 5.执行查询或更新
 * 6.结果处理
 * 7.关闭连接
 * Created by hp on 2016/11/10.
 */
public class Main {
    public static void main(String[] args){
        String driver = "oracle.jdbc.driver.OracleDriver";
        //2、定义连接URL
        String url = "jdbc:oracle:thin:@192.168.1.19:1521:assets";
        String username = "sec_base";//用户名
        String password = "base";//密码
        String sql = "SELECT * FROM SEC_BASE.GLOBAL_ORG_SCHEME";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            //1、加载驱动Oracle的jdbc驱动包
            Class.forName(driver);

            //3、建立连接 ：制定连接到哪里去jdbc:oracle:thin:  ip地址 : 端口号 : <数据库名>
            connection = DriverManager.getConnection(url, username, password);

            //4、创建statement对象，便于执行静态sql语句
            statement = connection.createStatement();
            
            /*动态执行SQL语句
            String updateSql = "update Users set name = ? where id = ?";
            PreparedStatement ps= connection.prepareStatement(updateSql);
            ps.setString(1, "王五");//设置第一个“？”的值
            ps.setInt(2, 1);//设置第二个“？”的值
            statement.executeUpdate(insertSql);
            */

            //5、执行查询或更新操作
            resultSet = statement.executeQuery(sql);

            //6、结果处理 方式一
            while (resultSet.next() && false){
                int empno = resultSet.getInt("empno");
                String ename = resultSet.getString("ename");

                System.out.println(empno+" "+ename);
            }

            //6、结果处理 方式二：比较通用，对于结果集有多列时，有好处

            //获取结果集的列数
            int columnCount = resultSet.getMetaData().getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                Object o = resultSet.getObject(i+1);
                System.out.print(o);
                //System.out.printf("%15s", o);//格式化输出
                if (i!=columnCount-1){
                    System.out.print(",");
                }
            }

            DatabaseMetaData dm = connection.getMetaData();
            PreparedStatement pStemt = null;
            pStemt = connection.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                System.out.println(rsmd.getColumnName(i + 1));
                // columnNames.add(rsmd.getColumnName(i + 1));
            }



            System.out.println();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            //7、关闭连接
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}