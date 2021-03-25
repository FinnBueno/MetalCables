package me.finnbon.metalcables.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Finn Bon
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
	String value();
}
