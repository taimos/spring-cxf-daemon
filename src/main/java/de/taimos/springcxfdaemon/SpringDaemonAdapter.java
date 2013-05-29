package de.taimos.springcxfdaemon;

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
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public boolean doStart() {
		this.doBeforeSpringStart();
		
		SpringDaemonAdapter.context = this.createSpringContext();
		SpringDaemonAdapter.context.getEnvironment().setActiveProfiles(System.getProperty(Configuration.PROFILES, "prod").split(","));
		
		final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setProperties(DaemonStarter.getDaemonProperties());
		SpringDaemonAdapter.context.addBeanFactoryPostProcessor(configurer);
		
		SpringDaemonAdapter.context.setConfigLocation(this.getSpringResource());
		SpringDaemonAdapter.context.refresh();
		
		this.doAfterSpringStart();
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
		this.doBeforeSpringStop();
		SpringDaemonAdapter.context.stop();
		this.doAfterSpringStop();
		return super.doStart();
	}
	
	@Override
	public Map<String, String> loadProperties() {
		try {
			return DaemonLifecycleAdapter.loadPropertiesFile(this.getConfigFile());
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
