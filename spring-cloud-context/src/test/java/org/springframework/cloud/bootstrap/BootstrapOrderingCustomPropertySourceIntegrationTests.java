package org.springframework.cloud.bootstrap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.bootstrap.BootstrapOrderingCustomPropertySourceIntegrationTests.Application;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = { "encrypt.key:deadbeef",
		"spring.cloud.bootstrap.name:custom" })
@ActiveProfiles("encrypt")
public class BootstrapOrderingCustomPropertySourceIntegrationTests {

	@Autowired
	private ConfigurableEnvironment environment;

	@Test
	@Ignore // FIXME: spring boot 2.0.0
	public void bootstrapPropertiesExist() {
		assertTrue(this.environment.getPropertySources().contains(
				PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME));
	}

	@Test
	public void customPropertiesDecrypted() {
		assertEquals("bar", this.environment.resolvePlaceholders("${custom.foo}"));
	}

	@EnableAutoConfiguration
	@Configuration
	protected static class Application {

	}

	@Configuration
	// This is added to bootstrap context as a source in bootstrap.properties
	protected static class PropertySourceConfiguration implements PropertySourceLocator {

		public static Map<String, Object> MAP = new HashMap<String, Object>(
				Collections.<String, Object>singletonMap("custom.foo",
						"{cipher}6154ca04d4bb6144d672c4e3d750b5147116dd381946d51fa44f8bc25dc256f4"));

		@Override
		public PropertySource<?> locate(Environment environment) {
			return new MapPropertySource("testBootstrap", MAP);
		}

	}

}
