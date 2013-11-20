package de.taimos.springcxfdaemon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

public class SpringDaemonTestRunner extends BlockJUnit4ClassRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringDaemonTestRunner.class);
	
	private SpringTest springTest;
	
	
	/**
	 * @param klass the class
	 * @throws InitializationError on error
	 */
	public SpringDaemonTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.configureLogging();
	}
	
	private void configureLogging() {
		org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
		final ConsoleAppender console = new ConsoleAppender();
		console.setName("CONSOLE");
		console.setLayout(new PatternLayout("%d{HH:mm:ss,SSS} %-5p %c %x - %m%n"));
		console.setTarget(ConsoleAppender.SYSTEM_OUT);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		final Statement invoker = super.methodInvoker(method, test);
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				invoker.evaluate();
			}
		};
	}
	
	@Override
	protected Statement withAfterClasses(Statement statement) {
		final Statement next = super.withAfterClasses(statement);
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				next.evaluate();
				SpringDaemonTestRunner.this.springTest.stop();
			}
		};
	}
	
	@Override
	protected Statement withBeforeClasses(Statement statement) {
		final Statement next = super.withBeforeClasses(statement);
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				final RunnerConfiguration cfgClass = this.findConfigAnnotation(SpringDaemonTestRunner.this.getTestClass().getJavaClass());
				if (cfgClass == null) {
					// Die on missing annotation
					throw new RuntimeException("Missing @RunnerConfiguration");
				}
				
				final RunnerConfig cfg;
				cfg = cfgClass.config().newInstance();
				cfg.addProperty("serviceName", cfgClass.svc());
				cfg.addProperty("profiles", "test");
				cfg.addProperty("developmentMode", "true");
				try {
					SpringDaemonTestRunner.this.springTest = new SpringTest() {
						
						@Override
						protected String getServiceName() {
							return cfgClass.svc();
						}
						
						@Override
						protected void fillProperties(Map<String, String> props) {
							String servicePackage = cfg.getServicePackage();
							if (servicePackage != null) {
								props.put(Configuration.SERVICE_PACKAGE, servicePackage);
								System.setProperty(Configuration.SERVICE_PACKAGE, servicePackage);
							}
							
							Enumeration<?> names = cfg.getProps().propertyNames();
							while (names.hasMoreElements()) {
								String key = (String) names.nextElement();
								props.put(key, cfg.getProps().getProperty(key));
							}
						}
						
						@Override
						protected String getSpringResource() {
							return cfg.getSpringFile();
						}
						
					};
					SpringDaemonTestRunner.this.springTest.start();
					next.evaluate();
				} catch (BeansException | IllegalStateException e) {
					SpringDaemonTestRunner.logger.error("Starting Spring context failed", e);
					throw new RuntimeException("Starting Spring context failed", e);
				}
			}
			
			private RunnerConfiguration findConfigAnnotation(Class<?> clazz) {
				if (clazz == null) {
					return null;
				}
				RunnerConfiguration cfgClass = clazz.getAnnotation(RunnerConfiguration.class);
				if (cfgClass == null) {
					cfgClass = this.findConfigAnnotation(clazz.getSuperclass());
				}
				return cfgClass;
			}
		};
	}
	
	@Override
	protected Object createTest() throws Exception {
		return this.springTest.getContext().getBeanFactory().createBean(this.getTestClass().getJavaClass());
	}
	
	
	/**
	 * Copyright 2013 Cinovo AG<br>
	 * <br>
	 * 
	 * @author thoeger
	 * 
	 */
	public static class RunnerConfig {
		
		private final Properties props = new Properties();
		
		
		/**
		 * @param key the prop key
		 * @param value the prop value
		 */
		protected void addProperty(final String key, final String value) {
			this.props.setProperty(key.trim(), value);
		}
		
		/**
		 * @return the properties
		 */
		public Properties getProps() {
			return this.props;
		}
		
		/**
		 * @return the Spring file nme
		 */
		public String getSpringFile() {
			return "spring-test/beans.xml";
		}
		
		public String getServicePackage() {
			return null;
		}
		
		protected static Integer randomPort() {
			return (int) ((Math.random() * 20000) + 10000);
		}
	}
	
	/**
	 * Copyright 2013 Cinovo AG<br>
	 * <br>
	 * 
	 * @author thoeger
	 * 
	 */
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface RunnerConfiguration {
		
		/**
		 * @return the class
		 */
		Class<? extends RunnerConfig> config() default RunnerConfig.class;
		
		/**
		 * @return the service name
		 */
		String svc();
	}
}