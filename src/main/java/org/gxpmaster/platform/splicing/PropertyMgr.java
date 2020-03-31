package org.gxpmaster.platform.splicing;

import java.io.IOException;
import java.util.Properties;

/**
 * @author David Sun
 * 配置文件读取工具
 */
public class PropertyMgr {
    static Properties props = new Properties();

    static {
        try {
            props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("config"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        if(null == props) return null;
        return (String)props.get(key);
    }

    public static void main(String[] args) {
        System.out.println(PropertyMgr.get("servermode"));
    }

}
