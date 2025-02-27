package com.sg.bbit.common.config;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.sg.bbit.common.factory.YamlPropertySourceFactory;

@Primary
@PropertySource(value = {
	"classpath:properties/env-{profile}.yml",
	"classpath:properties/database-{profile}.yml"
}, factory = YamlPropertySourceFactory.class)
public class PropertiesConfig {

}
