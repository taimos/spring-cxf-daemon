package de.taimos.springcxfdaemon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import de.taimos.springcxfdaemon.Configuration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repository
@Profile(Configuration.PROFILES_PRODUCTION)
public @interface ProdRepository {
	// marker
}
