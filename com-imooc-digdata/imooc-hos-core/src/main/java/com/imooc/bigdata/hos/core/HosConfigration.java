package com.imooc.bigdata.hos.core;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HosConfigration {
    private static HosConfigration configration;
    private static Properties properties;

    static{
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        configration = new HosConfigration();
        try{
            configration.properties = new Properties();
            Resource[] resources = resourcePatternResolver
                    .getResources("classpath:*.properties");
            for(Resource resource:resources){
                Properties prop = new Properties();
                InputStream inputStream = resource.getInputStream();
                prop.load(inputStream);
                inputStream.close();
                configration.properties.putAll(prop);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private HosConfigration(){

    }

    public static HosConfigration getConfigration(){
        return configration;
    }

    public String getString(String key){
        return properties.get(key).toString();
    }

    public int getInt(String key){
        return Integer.parseInt(properties.getProperty(key));
    }
}
