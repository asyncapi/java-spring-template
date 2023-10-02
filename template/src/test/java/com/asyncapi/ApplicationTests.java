package {{ params['userJavaPackage'] }};

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
