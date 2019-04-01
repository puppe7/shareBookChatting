package cn.kai.book;

import cn.kai.book.test.service.FooService;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author：Kai
 * date：2019/03/30
 */
/*/配置多数据源，将spring boot的自动配置取消
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
  DataSourceTransactionManagerAutoConfiguration.class,
  JdbcTemplateAutoConfiguration.class})*/
@SpringBootApplication
@RestController
@Slf4j
public class BookApplication implements CommandLineRunner {

  @Autowired
  private FooService fooService;
  @Autowired
  private DataSource dataSource;
  @Autowired
  private JdbcTemplate jdbcTemplate;


	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}
	@RequestMapping("/hello")
	public String hello(){
		return "hello Springboot";
	}

	//多数据源初始化
  /*@Bean
  @ConfigurationProperties("bar.datasource")
  public DataSourceProperties barDataSourceProperties(){
	  return  new DataSourceProperties();
  }

  @Bean
  public DataSource barDataSource(){
    DataSourceProperties dataSourceProperties= barDataSourceProperties();
    log.info("bar datasource:{}",barDataSourceProperties().getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();

  }
  @Bean
  @Resource
  public PlatformTransactionManager barTxManager(DataSource barDataSource){
    return  new DataSourceTransactionManager(barDataSource);
  }
  @Bean
  @ConfigurationProperties("foo.datasource")
  public DataSourceProperties fooDataSourceProperties(){
    return  new DataSourceProperties();
  }

  @Bean
  public DataSource fooDataSource(){
    DataSourceProperties dataSourceProperties= fooDataSourceProperties();
    log.info("foo datasource:{}",fooDataSourceProperties().getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();

  }
  @Bean
  @Resource
  public PlatformTransactionManager fooTxManager(DataSource fooDataSource){
    return  new DataSourceTransactionManager(fooDataSource);
  }*/

  @Override
  public void run(String... args) throws  Exception{
    showConnection();
    showData();
    transactionManagementTest();

  }

  private void showConnection() throws  SQLException{
    log.info(dataSource.toString());
    Connection conn = dataSource.getConnection();
    log.info(conn.toString());
    conn.close();
  }
  private void showData(){
    jdbcTemplate.queryForList("select * from FOO");

  }

  private void transactionManagementTest(){
    fooService.insertRecord();
    log.info("AAA {}", jdbcTemplate.queryForObject("select count(*) from FOO where BAR='AAA'",Long.class));
    try {
      fooService.insertThenRollback();
    } catch (Exception e) {
      log.info("BBB {}",
          jdbcTemplate
              .queryForObject("SELECT COUNT(*) FROM FOO WHERE BAR='BBB'", Long.class));
    }

    try {
      fooService.invokeInsertThenRollback();
    } catch (Exception e) {
      log.info("BBB {}",
          jdbcTemplate
              .queryForObject("SELECT COUNT(*) FROM FOO WHERE BAR='BBB'", Long.class));
    }
  }
}
