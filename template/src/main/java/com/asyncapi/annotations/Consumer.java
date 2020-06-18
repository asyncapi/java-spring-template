package {{ params['userJavaPackage'] }}.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import {{ params['userJavaPackage'] }}.constants.Topic;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consumer {
	Topic topic();
}
