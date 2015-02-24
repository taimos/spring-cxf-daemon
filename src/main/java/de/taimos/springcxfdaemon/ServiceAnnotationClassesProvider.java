package de.taimos.springcxfdaemon;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.restdoc.cxf.provider.IClassesProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceAnnotationClassesProvider implements IClassesProvider {
	
	private Class<? extends Annotation> serviceAnnotation;
	
	@Autowired
	private ListableBeanFactory beanFactory;
	
	
	public Class<? extends Annotation> getServiceAnnotation() {
		return this.serviceAnnotation;
	}
	
	public void setServiceAnnotation(Class<? extends Annotation> serviceAnnotation) {
		this.serviceAnnotation = serviceAnnotation;
	}
	
	@Override
	public Class<?>[] getClasses() {
		Map<String, Object> beansWithAnnotation = this.beanFactory.getBeansWithAnnotation(this.serviceAnnotation);
		List<Class<?>> classes = new ArrayList<>();
		for (Object bean : beansWithAnnotation.values()) {
			classes.add(bean.getClass());
			classes.addAll(Arrays.asList(bean.getClass().getInterfaces()));
		}
		return classes.toArray(new Class[0]);
	}
}
