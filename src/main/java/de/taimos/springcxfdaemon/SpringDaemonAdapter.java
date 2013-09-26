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
	public void doStart() throws Exception {
		super.doStart();
		try {
			this.doBeforeSpringStart();
		} catch (Exception e) {
			throw new RuntimeException("Before spring failed", e);
		}
		
		try {
			SpringDaemonAdapter.context = this.createSpringContext();
			String[] profiles = System.getProperty(Configuration.PROFILES, Configuration.PROFILES_PRODUCTION).split(",");
			SpringDaemonAdapter.context.getEnvironment().setActiveProfiles(profiles);
			
			final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(DaemonStarter.getDaemonProperties());
			SpringDaemonAdapter.context.addBeanFactoryPostProcessor(configurer);
			
			SpringDaemonAdapter.context.setConfigLocation(this.getSpringResource());
			SpringDaemonAdapter.context.refresh();
		} catch (Exception e) {
			throw new RuntimeException("Spring context failed", e);
		}
		
		try {
			this.doAfterSpringStart();
		} catch (Exception e) {
			throw new RuntimeException("After spring failed", e);
		}
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
	
	@Override
	public void doStop() throws Exception {
		try {
			this.doBeforeSpringStop();
		} catch (Exception e) {
			throw new RuntimeException("Before spring stop failed", e);
		}
		try {
			SpringDaemonAdapter.context.stop();
		} catch (Exception e) {
			throw new RuntimeException("spring stop failed", e);
		}
		try {
			this.doAfterSpringStop();
		} catch (Exception e) {
			throw new RuntimeException("After spring stop failed", e);
		}
		super.doStop();
	}
	
	@Override
	public Map<String, String> loadProperties() {
		Map<String, String> props = super.loadProperties();
		if (System.getProperty(Configuration.SERVICE_PACKAGE) == null) {
			props.put(Configuration.SERVICE_PACKAGE, this.getClass().getPackage().getName());
		}
		return props;
	}
	
	/**
	 * @return the Spring context
	 */
	public static ApplicationContext getContext() {
		return SpringDaemonAdapter.context;
	}
	
}
