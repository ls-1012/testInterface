package com.shanlu.config;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseDAO {
    private static String driver="com.mysql.cj.jdbc.Driver";
    private static String url="jdbc:mysql://127.0.0.1:3306/xxxxmerchant";
    private static String user="xxx";
    private static String password="xxx";

    private static Connection conn;
//    private static PreparedStatement stmt;
    private static PreparedStatement stmt;
    private static ResultSet rs;
    static {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败！！！");
            e.printStackTrace();
        }catch (SQLException e) {
            System.out.println("数据库连接异常！！！");
            e.printStackTrace();
        }
    }

    /* 获取连接 */
    public static Connection getConnect(){
        return conn;
    }

    /* 关闭连接 */
    public static void close(){
            try {
                if(rs!=null) {
                    rs.close();

                }
                if(stmt!=null){
                    stmt.close();


                }
                if(conn!=null){
                    conn.close();

                }
            } catch (SQLException e) {
                System.err.println("资源释放发生错误");
                e.printStackTrace();
            }
        }


    /* 执行sql，返回ResultSet */
    private static ResultSet excutSql(String sql,Object... args) {
        try {
            stmt = conn.prepareStatement(sql);


            if (null != args && args.length != 0) {
                for (int i = 0; i < args.length; i++) {
                    stmt.setObject(i + 1, args[i]);

                }
            }


            rs = stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;

    }

    //查询数据库，返回hashmap组成的list结构
    public static List<HashMap<String,String>> excutQuery(String sql,String column, Object... args) {
        List<HashMap<String, String>> resultMap = new ArrayList<>();
        try {
            rs = excutSql(sql, args);

            while (rs.next()) {
                String value="";
                ResultSetMetaData metaData = rs.getMetaData();//获取结果的列数
                HashMap<String, String> columnMap = new HashMap<String, String>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnLabel(i);

                    if (columnName.equals(column)){
                        value = rs.getString(i);
                        columnMap.put(columnName, value);
                        break;
                    }


                }
                resultMap.add(columnMap);

            }
            int size = resultMap.size();
            System.out.println("返回的size长度是： "+size);

        } catch (SQLException e) {
            System.out.println("数据库查询异常！！！");
            e.printStackTrace();
        } finally {

        }
        return resultMap;
    }

    //查询数据库，返回String组成的list结构
    public static List<String> excutQuerySingleResult(String sql, Object... args) {
        List<String> resultList = new ArrayList<>();

        try {
            rs = excutSql(sql, args);

            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();//获取结果的列数
//
                String value = rs.getString(1);
                resultList.add(value);

            }
            int size = resultList.size();
            System.out.println("返回的size长度是： "+size);

        } catch (SQLException e) {
            System.out.println("数据库查询异常！！！");
            e.printStackTrace();
        } finally {

        }
        return resultList;
    }

}

