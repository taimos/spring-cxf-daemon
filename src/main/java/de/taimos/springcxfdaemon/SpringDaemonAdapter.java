package de.taimos.springcxfdaemon;

/*
 * #%L Daemon with Spring and CXF %% Copyright (C) 2013 Taimos GmbH %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.util.Map;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import de.taimos.daemon.DaemonLifecycleAdapter;
import de.taimos.daemon.DaemonStarter;

public abstract class SpringDaemonAdapter extends DaemonLifecycleAdapter {
	
	private static AbstractXmlApplicationContext context;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public boolean doStart() {
		try {
			this.doBeforeSpringStart();
		} catch (Exception e) {
			this.logger.error("Before spring failed", e);
			return false;
		}
		
		try {
			SpringDaemonAdapter.context = this.createSpringContext();
			SpringDaemonAdapter.context.getEnvironment().setActiveProfiles(System.getProperty(Configuration.PROFILES, "prod").split(","));
			
			final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(DaemonStarter.getDaemonProperties());
			SpringDaemonAdapter.context.addBeanFactoryPostProcessor(configurer);
			
			SpringDaemonAdapter.context.setConfigLocation(this.getSpringResource());
			SpringDaemonAdapter.context.refresh();
		} catch (Exception e) {
			this.logger.error("Spring context failed", e);
			return false;
		}
		
		try {
			this.doAfterSpringStart();
		} catch (Exception e) {
			this.logger.error("After spring failed", e);
			return false;
		}
		return super.doStart();
	}
	
	protected void doAfterSpringStart() {
		//
	}
	
	protected void doBeforeSpringStart() {
		//
	}
	
	protected void doAfterSpringStop() {
		//
	}
	
	protected void doBeforeSpringStop() {
		//
	}
	
	/**
	 * @return the created Spring context
	 */
	protected AbstractXmlApplicationContext createSpringContext() {
		return new ClassPathXmlApplicationContext();
	}
	
	/**
	 * @return the name of the Spring resource
	 */
	protected String getSpringResource() {
		return "spring/beans.xml";
	}
	
	/**
	 * @return the name of the config file
	 */
	protected String getConfigFile() {
		return "core.properties";
	}
	
	@Override
	public boolean doStop() {
		try {
			this.doBeforeSpringStop();
		} catch (Exception e) {
			this.logger.error("Before spring stop failed", e);
			return false;
		}
		try {
			SpringDaemonAdapter.context.stop();
		} catch (Exception e) {
			this.logger.error("spring stop failed", e);
			return false;
		}
		try {
			this.doAfterSpringStop();
		} catch (Exception e) {
			this.logger.error("After spring stop failed", e);
			return false;
		}
		return super.doStart();
	}
	
	@Override
	public Map<String, String> loadProperties() {
		try {
			Map<String, String> props = DaemonLifecycleAdapter.loadPropertiesFile(this.getConfigFile());
			if (System.getProperty(Configuration.SERVICE_PACKAGE) == null) {
				props.put(Configuration.SERVICE_PACKAGE, this.getClass().getPackage().getName());
			}
			return props;
		} catch (final Exception e) {
			this.logger.error("Error loading properties", e);
		}
		return Maps.newHashMap();
	}
	
	/**
	 * @return the Spring context
	 */
	public static ApplicationContext getContext() {
		return SpringDaemonAdapter.context;
	}
	
}
