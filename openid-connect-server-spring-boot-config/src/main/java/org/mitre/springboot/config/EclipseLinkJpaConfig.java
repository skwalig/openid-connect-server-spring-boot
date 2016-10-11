package org.mitre.springboot.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mitre"})
@EnableTransactionManagement
@EntityScan(basePackages = {"org.mitre"} )
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class EclipseLinkJpaConfig extends JpaBaseConfiguration {

	@Autowired
	private JpaProperties properties;

	@Autowired
	private DataSource dataSource;

	@Override
	protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
		return new EclipseLinkJpaVendorAdapter();
	}

	/*Explicitly defining this bean as MITRE code looks for a TransactionManager named "defaultTransactionManager"*/
	//@Bean
	@Bean(name="defaultTransactionManager")
	@Override
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}
	
	/*Explicitly defining this bean as MITRE code looks for a persistenceUnit named "defaultPersistenceUnit"*/
	@Bean
	@Primary
	@Override
	@ConditionalOnMissingBean({ LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class })
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
		LocalContainerEntityManagerFactoryBean factory = super.entityManagerFactory(factoryBuilder);
		factory.setPersistenceUnitName("defaultPersistenceUnit");
		return factory;
	}

	@Override
	protected Map<String, Object> getVendorProperties() {
		Map<String, Object> vendorProperties = new LinkedHashMap<String, Object>();
		vendorProperties.putAll(this.properties.getHibernateProperties(this.dataSource));
		return vendorProperties;
	}

}