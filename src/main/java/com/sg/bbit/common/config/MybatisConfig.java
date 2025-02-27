package com.sg.bbit.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@MapperScan(basePackages={ "com.sg.bbit" },sqlSessionFactoryRef=MybatisConfig.COMMON_SESSION_FACTORY)
public class MybatisConfig {
	public static final String COMMON_MYBATIS_SESSION_CONFIG = "commonMybatisSessionConfig";
	public static final String COMMON_SESSION_FACTORY = "commonSessionFactory";
	public static final String COMMON_SESSION_TEMPLATE = "commonSessionTemplate";
	public static final String COMMON_SESSION_TEMPLATE_BATCH = "commonSessionTemplateBatch";
	
	@Primary
	@Bean(name = COMMON_SESSION_FACTORY, destroyMethod = "")
	SqlSessionFactory commonSessionFactory(
			@Qualifier(DataSourceConfig.COMMON_DATASOURCE) final DataSource dataSource,
			final ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setTypeAliasesPackage("com.sg.bbit.vo, com.sg.bbit.security.vo, com.sg.bbit.generate.vo");
		sqlSessionFactoryBean.setMapperLocations(
			applicationContext.getResources("classpath:mappers/**/*.xml")
		);
		org.apache.ibatis.session.Configuration config = commonMybatisSessionConfig();
		config.setLocalCacheScope(LocalCacheScope.STATEMENT);
		config.setCacheEnabled(false);
		config.setMapUnderscoreToCamelCase(true);
		sqlSessionFactoryBean.setConfiguration(config);
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(name = COMMON_MYBATIS_SESSION_CONFIG)
	org.apache.ibatis.session.Configuration commonMybatisSessionConfig(){
		return new org.apache.ibatis.session.Configuration();
	}
	
	@Primary
	@Bean(name = COMMON_SESSION_TEMPLATE)
	SqlSessionTemplate commonSessionTemplate(@Qualifier(COMMON_SESSION_FACTORY) SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean(name = COMMON_SESSION_TEMPLATE_BATCH)
	SqlSessionTemplate commonSessionTemplateBatch(@Qualifier(COMMON_SESSION_FACTORY) SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
	}
}
