package com.albo.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
		basePackages = "com.albo.prSuma.repository", 
		entityManagerFactoryRef = "servicioPrSumaEntityManager", 
		transactionManagerRef = "servicioPrSumaTransactionManager")
public class ServicioPrSumaDatabaseConfig {

	@Value("${spring.datasource.servicio-prsuma.hibernate-hbm2ddl-auto}")
	private String ddlMode;

	@Primary
	@Bean
	public PlatformTransactionManager servicioPrSumaTransactionManager() {
		return new JpaTransactionManager(servicioPrSumaEntityManager().getObject());
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean servicioPrSumaEntityManager() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", ddlMode);
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
		properties.put("hibernate.schema_update.unique_constraint_strategy", "RECREATE_QUIETLY");

		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

		factoryBean.setDataSource(servicioPrSumaDataSource());
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setPackagesToScan("com.albo.prSuma.model");
		factoryBean.setJpaPropertyMap(properties);

		return factoryBean;
	}

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.servicio-prsuma")
	public DataSource servicioPrSumaDataSource() {
		return DataSourceBuilder.create().build();
	}
	
}
