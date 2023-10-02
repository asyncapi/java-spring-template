package {{ params['userJavaPackage'] }};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
