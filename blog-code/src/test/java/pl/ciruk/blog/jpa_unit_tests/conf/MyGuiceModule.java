package pl.ciruk.blog.jpa_unit_tests.conf;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class MyGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaPersistModule("my-persistence-unit"));
		
		bind(MyJpaInitializer.class).asEagerSingleton();
	}
}
