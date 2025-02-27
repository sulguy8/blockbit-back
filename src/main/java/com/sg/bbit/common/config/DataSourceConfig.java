package com.sg.bbit.common.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	public static final String COMMON_DATASOURCE = "commonDatasource";

	@Primary
	@Bean(name = COMMON_DATASOURCE)
	@ConfigurationProperties(prefix = "datasource.postgresql.common")
	DataSource commonDs() {
		return new HikariDataSource();
	}

}
