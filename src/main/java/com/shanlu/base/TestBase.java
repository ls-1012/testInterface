package com.shanlu.base;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

//作為所有接口請求測試的父类
@Deprecated
public class TestBase {

    public Properties prop;
    public static final int RESPONSE_STATUS_CODE_200 = 200;
    public static final int RESPONSE_STATUS_CODE_201 = 201;
    public static final int RESPONSE_STATUS_CODE_404 = 404;
    public static final int RESPONSE_STATUS_CODE_500 = 500;

    public TestBase(){

        try{
            //每次调用该方法都加载config配置文件
            prop = new Properties();
//            System.out.println("user.dir.path : "+System.getProperty("user.dir"));
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/com/shanlu/config/config.properties");
            prop.load(fis);


        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
