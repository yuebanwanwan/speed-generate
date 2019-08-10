import org.junit.Test;

import java.io.*;

/**
 * @author: zhouzhaoping
 * @description:
 * @date: 2019-08-08
 */
public class MainTest {

    @Test
    public void test1() {
        int a = 10;
        while (a > 0) {
            a--;
            final String str = "1";
            System.out.println(str);
        }
    }

    @Test
    public void test2() {
        try {
            final String fileBase = "/Users/zhouzhaoping/speedgenerator/cn/speedit/basic/todo/action";
            File dir = new File(fileBase);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "GlobalOrgSchemeTodoAction.java");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws IOException {
        //File file = new File("/Users/zhouzhaoping/speedgenerator/cn/speedit/basic/todo/action/test.txt");
        //if (!file.exists()) {
        //    file.createNewFile();
        //}
        fileWriter(new File("/Users/zhouzhaoping/speedgenerator/cn/speedit/basic/todo/action/GlobalOrgSchemeTodoAction.java"));
        // fileWriter(file);
    }


    public void fileWriter(File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        StringBuilder sb = new StringBuilder();
        sb.append("package cn.speedit.basic.todo.bean;\n");
        sb.append("/**\n" +
                " * Copyright (c) 2019, Author zhouzhaoping \n" +
                " *\tTechnology Co.Ltd. All rights reserved.\n" +
                " */");
        sb.append("import java.io.Serializable;\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "import javax.validation.constraints.Size;\n" +
                "\n" +
                "import cn.speedit.framework.authenticate.Entity;\n" +
                "import cn.speedit.framework.authenticate.PK;");
        sb.append("\n" +
                "@Entity(\"SEC_BASE.GLOBAL_ORG_SCHEME\")\n" +
                "public class GlobalOrgScheme implements Serializable {\n" +
                "\tprivate final static long serialVersionUID = 1L;\n" +
                "\tpublic static final String FIELD_ID = \"id\";\n" +
                "\tpublic static final String FIELD_ORG_ID = \"org_id\";\n" +
                "\tpublic static final String FIELD_SCHEME_HISTORY = \"scheme_history\";\n" +
                "\tpublic static final String FIELD_SCHEME_TARGET = \"scheme_target\";\n" +
                "\n" +
                "\t/** ID */\n" +
                "\t@Size(max = 64, message = \"id 最大长度不能大于64\")\n" +
                "\t@NotNull(message = \"id 不能为空\")\n" +
                "\t@PK\n" +
                "\tprivate String id;\n" +
                "\n" +
                "\t/** org_id */\n" +
                "\t@Size(max = 64, message = \"org_id 最大长度不能大于64\")\n" +
                "\t@NotNull(message = \"org_id 不能为空\")\n" +
                "\tprivate String orgId;\n" +
                "\n" +
                "\t/** 历史方案 */\n" +
                "\t@Size(max = 64, message = \"scheme_history 最大长度不能大于64\")\n" +
                "\t@NotNull(message = \"scheme_history 不能为空\")\n" +
                "\tprivate String schemeHistory;\n" +
                "\n" +
                "\t/** 最新方案 */\n" +
                "\t@Size(max = 64, message = \"scheme_target 最大长度不能大于64\")\n" +
                "\t@NotNull(message = \"scheme_target 不能为空\")\n" +
                "\tprivate String schemeTarget;\n" +
                "\n" +
                "\tpublic GlobalOrgScheme() {\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\tpublic GlobalOrgScheme(String id) {\n" +
                "\t\tthis.id = id;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getId() {\n" +
                "\t\treturn id;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setId(String id) {\n" +
                "\t\tthis.id = id;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getOrgId() {\n" +
                "\t\treturn orgId;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setOrgId(String orgId) {\n" +
                "\t\tthis.orgId = orgId;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getSchemeHistory() {\n" +
                "\t\treturn schemeHistory;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setSchemeHistory(String schemeHistory) {\n" +
                "\t\tthis.schemeHistory = schemeHistory;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic String getSchemeTarget() {\n" +
                "\t\treturn schemeTarget;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setSchemeTarget(String schemeTarget) {\n" +
                "\t\tthis.schemeTarget = schemeTarget;\n" +
                "\t}\n" +
                "\n" +
                "\t@Override\n" +
                "\tpublic String toString() {\n" +
                "\t\treturn \"GlobalOrgScheme [id=\" + id + \", orgId=\" + orgId + \", schemeHistory=\" + schemeHistory + \", schemeTarget=\"\n" +
                "\t\t\t\t+ schemeTarget + \"]\";\n" +
                "\t}\n" +
                "\n" +
                "}");
        byte[] data = sb.toString().getBytes();
        os.write(data);
    }

    @Test
    public void test4() {
        StringBuilder sb = new StringBuilder();
        String s = "SCHEME_HISTORY_OK";
        String[] strings = s.split("_");
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (i == 0) {
                sb.append(string.toLowerCase());
            } else {
                sb.append(string.substring(0, 1) + string.substring(1).toLowerCase());
            }
        }
        System.out.println(sb.toString());
    }









}
