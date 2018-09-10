package com.imooc.bigdata.hos.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@Configuration
@MapperScan(basePackages = HosDataSourceConfig.PPACKAGE,
            sqlSessionFactoryRef = "HosSqlSessionFactory")
public class HosDataSourceConfig {

    static final String PPACKAGE = "com.imooc.bigdata.hos.**";

    /**
     * GET datasource
     * @return
     * @throws IOException
     */
    @Bean(name = "HosDataSource")
    @Primary
    public DataSource hosDataSource() throws IOException{
        //1.get massage of datasource
        ResourceLoader loader = new DefaultResourceLoader();
        InputStream inputStream = loader.getResource("classpath:application.properties")
                .getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);
        Set<Object> keys = properties.keySet();
        Properties dsProperties = new Properties();
        for(Object key:keys){
            if(key.toString().startsWith("datasource")){
                dsProperties.put(key.toString().replace("datasource.",""),properties.get(key))
            }
        }
        //2.by HiKariDatasourcefactory generate a new datasource
        HikariDataSourceFactory factory = new HikariDataSourceFactory();
        factory.setProperties(dsProperties);
        inputStream.close();
        return factory.getDataSource();
    }

    public SqlSessionFactory hosSqlSessionFactory(
            @Qualifier("HosDataSource") DataSource hosDataSource){

    }
}
