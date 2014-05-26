package pl.ciruk.blog.jpa_unit_tests.conf;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class MyJpaInitializer {
	private PersistService persistService;
	
	@Inject
	MyJpaInitializer(PersistService service) {
		persistService = service;
		persistService.start();
	}
	
	public void stopService() {
		persistService.stop();
	}
}
