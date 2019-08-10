package com.zzp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.1.19:1521:assets";
    private static final String USERNAME = "sec_base";
    private static final String PASSWORD = "base";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver", e);
            System.out.println(e.getMessage());
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<String>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            DatabaseMetaData db = conn.getMetaData();
            rs = db.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                if (rs.getString(3).indexOf("GLOBAL_ORG") != -1) {
                    tableNames.add(rs.getString(3));
                    break;
                }
                // tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    public static Map<String, Object> getRelationData(String tableName, Map<String, Object> map) throws SQLException {
        Connection conn = getConnection();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        // 获取表的主键
        ResultSet pkResultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        //while (pkResultSet.next()) {
        //    System.out.println("****** Table ******");
        //    System.out.println("TABLE_NAME : " + pkResultSet.getObject(3));
        //    System.out.println("COLUMN_NAME: " + pkResultSet.getObject(4));
        //    System.out.println("KEY_SEQ : " + pkResultSet.getObject(5));
        //    System.out.println("PK_NAME : " + pkResultSet.getObject(6));
        //    System.out.println("****** ******* ******");
        //}
        pkResultSet.next();
        String primaryKeyColumnName = (String) pkResultSet.getObject(4);

        ResultSet columnSet = databaseMetaData.getColumns(null, "%",
                tableName, "%");
        // 每个列的信息存入map中
        List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
        while (columnSet.next()) {
            // 只处理名称为大写的表名
            if (columnSet.getString("COLUMN_NAME").toLowerCase().equals(columnSet.getString("COLUMN_NAME"))) {
                continue;
            }
            Map<String, String> columnIndexMap = new HashMap<String, String>();
            String columnName = columnSet.getString("COLUMN_NAME");
            String columnType = columnSet.getString("TYPE_NAME");
            String columnSize = columnSet.getString("COLUMN_SIZE");
            // 返回"1"表示可以为空 "0"表示不可以为空
            String nullAble = columnSet.getString("NULLABLE");

            columnIndexMap.put("COLUMN_NAME", columnName);
            columnIndexMap.put("TYPE_NAME", columnType);
            columnIndexMap.put("COLUMN_SIZE", columnSize);
            columnIndexMap.put("NULLABLE", nullAble);
            columnIndexMap.put("IS_PRIMARY_KEY", columnName.equals(primaryKeyColumnName) ? "1" : "0");

            columns.add(columnIndexMap);
        }
        map.put(tableName, columns);
        return map;
    }


    public static void main(String[] args) throws SQLException, IOException {
        List<String> tableNames = getTableNames();
        // 存储每个表的信息
        Map<String, Object> tableInfosMap = new HashMap<String, Object>();
        for (String tableName : tableNames) {
            getRelationData(tableName, tableInfosMap);
        }

        speedGenerator(tableInfosMap);

        System.out.println("tableNames:" + tableNames);
        System.out.println("tablesInfosMap:" + tableInfosMap);

    }


    /**
     * 传入多表的信息生成基本文件，如bean、dao、service等.
     */
    public static void speedGenerator(Map<String, Object> relationMap) throws IOException {
        for (Map.Entry<String, Object> entry : relationMap.entrySet()) {
            String tableName = entry.getKey();
            List<Map<String, String>> columns = (List<Map<String, String>>) entry.getValue();
            beanGenerator(tableName, columns);
        }
    }

    public static void beanGenerator(String tableName, List<Map<String, String>> columns) throws IOException {
        String baseDir = "/Users/zhouzhaoping/speedgenerator/cn/speedit/";
        // 此处须为配置文件所填内容
        String dynamicDir = "basic/todo/";
        String bean = "bean";
        File dir = new File(baseDir + dynamicDir + bean);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, tableName + ".java");
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream os = new FileOutputStream(file);
        StringBuilder beanText = new StringBuilder();
        // 此处须为配置文件所填内容
        String basePackage = "basic.todo";
        beanText.append("package cn.speedit." + basePackage + ".bean;\n");
        beanText.append("\n" +
                "/**\n" +
                " * Copyright (c) 2019, ChengDu Speed Information \n" +
                " *\tTechnology Co.Ltd. All rights reserved.\n" +
                " */\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "import javax.validation.constraints.Size;\n" +
                "\n" +
                "import cn.speedit.framework.authenticate.Entity;\n" +
                "import cn.speedit.framework.authenticate.PK;\n\n");

        beanText.append("@Entity(\"SEC_BASE." + tableName + "\")\n");

        beanText.append("public class " + tableNameToJavaClassName(tableName) + " implements Serializable {\n" +
                "\tprivate final static long serialVersionUID = 1L;\n" + staticVarDefine(columns) + "\n");

        beanText.append(varDefine(columns));

        beanText.append(constructor(tableName, columns));

        beanText.append(getterAndSetters(columns));

        beanText.append(toString(tableName, columns));

        byte[] data = beanText.toString().getBytes();
        os.write(data);
    }


    public static String staticVarDefine(List<Map<String, String>> columns) {
        List<String> staticVarDefine = new ArrayList<String>();
        for (Map<String, String> column : columns) {
            // 处理每一个字段
            String filed = column.get("COLUMN_NAME");
            String line = "\tpublic static final Sting FIELD_" + filed + " = " + "\"" + filed.toLowerCase() + "\"" + ";\n";
            staticVarDefine.add(line);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : staticVarDefine) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String varDefine(List<Map<String, String>> columns) {
        List<String> varDefine = new ArrayList<String>();
        StringBuilder sbl = new StringBuilder();
        for (Map<String, String> column : columns) {
            if (column.get("TYPE_NAME").indexOf("VARCHAR") != -1) {
                sbl.append("\t@Size(max = " + column.get("COLUMN_SIZE") + ", message = \"" + column.get("COLUMN_NAME") + " 最大长度不能大于" + column.get("COLUMN_SIZE") + "\")\n");
            }
            if (column.get("NULLABLE").equals("0")) {
                sbl.append("\t@NotNull(message = \"" + column.get("COLUMN_NAME") + " 不能为空\")\n");
            }
            if (column.get("IS_PRIMARY_KEY").equals("1")) {
                sbl.append("\t@PK\n");
            }
            sbl.append("\tprivate " + getJavaType(column.get("TYPE_NAME")) + " " + fieldConversion(column.get("COLUMN_NAME")) + "\n\n");
        }
        return sbl.toString();
    }



    public static String getJavaType(String columnName) {
        Map<String, String> ormMap = new HashMap<String, String>();
        ormMap.put("VARCHAR", "String");
        ormMap.put("VARCHAR2", "String");
        ormMap.put("CHAR", "String");
        ormMap.put("LONG", "String");
        ormMap.put("NUMBER", "BigDecimal");
        ormMap.put("DATE", "DATE");
        return ormMap.get(columnName);
    }

    public static String fieldConversion(String s) {
        StringBuilder sb = new StringBuilder();
        String[] strings = s.split("_");
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (i == 0) {
                sb.append(string.toLowerCase());
            } else {
                sb.append(string.substring(0, 1) + string.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String tableNameToJavaClassName(String tableName) {
        StringBuilder sb = new StringBuilder();
        String[] strings = tableName.split("_");
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            sb.append(string.substring(0, 1) + string.substring(1).toLowerCase());
        }
        return sb.toString();
    }


    public static String constructor(String tableName, List<Map<String, String>> columns) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> primaryKeyMap = null;
        for (Map<String, String> column : columns) {
            if (column.get("IS_PRIMARY_KEY").equals("1")) {
                primaryKeyMap = column;
                break;
            }
        }
        String primaryKeyColumnName = fieldConversion(primaryKeyMap.get("COLUMN_NAME"));
        String primaryKeyTypeName = getJavaType(primaryKeyMap.get("TYPE_NAME"));

        sb.append("\tpublic " + tableNameToJavaClassName(tableName) + "() {\n" +
                "\t\t\n" +
                "\t}\n" +
                "\n" +
                "\tpublic " + tableNameToJavaClassName(tableName) + "(" + primaryKeyTypeName + " " + primaryKeyColumnName + ") {\n" +
                "\t\tthis." + primaryKeyColumnName + " = " + primaryKeyColumnName + ";\n" +
                "\t}\n");
        return sb.toString();
    }

    public static String getterAndSetters(List<Map<String, String>> columns) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> column : columns) {
            String returnType = getJavaType(column.get("TYPE_NAME"));
            String fieldName = fieldConversion(column.get("COLUMN_NAME"));
            String method = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            sb.append("\n\tpublic " + returnType + " get" + method + "() {\n" +
                    "\t\treturn " + fieldName + ";\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic void set" + method + "(" + returnType + " " + fieldName + ") {\n" +
                    "\t\tthis." + fieldName + " = " + fieldName + ";\n" +
                    "\t}\n");
        }
        return sb.toString();
    }

    public static String toString(String tableName, List<Map<String, String>> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t@Override\n" +
                "\tpublic String toString() {\n" +
                "\t\treturn \"" + tableNameToJavaClassName(tableName) + " [");

        int size = columns.size();
        for (Map<String, String> column : columns) {
            size--;
            String fieldName = fieldConversion(column.get("COLUMN_NAME"));
            if (size != 0) {
                sb.append(fieldName + "=\" + " + fieldName + " + \", ");
            } else {
                sb.append(fieldName + "=\" + " + fieldName + " + \"");
            }
        }

        sb.append("]\";\n" +
                "\t}\n" +
                "\n" +
                "}");

        return sb.toString();
    }


}
