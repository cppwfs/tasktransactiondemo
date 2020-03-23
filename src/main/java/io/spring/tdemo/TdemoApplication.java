package io.spring.tdemo;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.cloud.task.repository.TaskExplorer;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.cloud.task.repository.support.SimpleTaskExplorer;
import org.springframework.cloud.task.repository.support.SimpleTaskRepository;
import org.springframework.cloud.task.repository.support.TaskExecutionDaoFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
@EnableTask
public class TdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TdemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				//Thread.sleep(600000);
				System.out.println("Hello World");
			}
		};
	}
	@Bean(name="hqhbTaskSource")
	@ConfigurationProperties(prefix = "spring.datasource.db-hqhb-task")
	public DataSource dataSource(){
		DataSourceBuilder dsb = DataSourceBuilder.create().url("jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE");
		dsb.driverClassName("org.h2.Driver");
		return dsb.build();
	}

	@Bean(name = "hqhbTaskTransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("hqhbTaskSource") DataSource ds){
		return new DataSourceTransactionManager(ds);
	}

	@Bean(name="hqhbTaskSource2")
	@ConfigurationProperties(prefix = "spring.datasource.db-hqhb-task2")
	public DataSource dataSource2(){
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "hqhbTaskTransactionManager2")
	public DataSourceTransactionManager transactionManager2(@Qualifier("hqhbTaskSource") DataSource ds){
		return new DataSourceTransactionManager(ds);
	}

	@Bean
	public TaskConfigurer taskConfigurer(@Qualifier("hqhbTaskSource") DataSource dataSource,
			@Qualifier("hqhbTaskTransactionManager") DataSourceTransactionManager transactionManager){

		MyTaskConfigurer myTaskConfigurer = new MyTaskConfigurer(dataSource);
//		myTaskConfigurer.transactionManager = transactionManager;
		return myTaskConfigurer;
	}
	public static class MyTaskConfigurer extends DefaultTaskConfigurer {

		PlatformTransactionManager transactionManager;

		public MyTaskConfigurer(DataSource dataSource) {
			super(dataSource);
		}

//		@Override
//		public PlatformTransactionManager getTransactionManager() {
//			return transactionManager;
//		}
	}
}

