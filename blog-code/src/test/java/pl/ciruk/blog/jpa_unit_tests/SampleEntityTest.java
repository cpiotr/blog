package pl.ciruk.blog.jpa_unit_tests;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.ciruk.blog.jpa_unit_tests.conf.MyGuiceModule;
import pl.ciruk.blog.jpa_unit_tests.conf.MyJpaInitializer;

import com.google.inject.Guice;

public class SampleEntityTest {

	@Inject
	private EntityManager entityManager;
	
	@Inject
	private MyJpaInitializer jpaInitializer;
	
	@Before
	public void setUp() {
		Guice.createInjector(new MyGuiceModule()).injectMembers(this);
		entityManager.getTransaction().begin();
	}
	
	@After
	public void cleanUp() {
		entityManager.getTransaction().rollback();
		jpaInitializer.stopService();
	}
	
	@Test
	public void shouldRetrieveEntitiesWithAmountBelowThreshold() {
		int numberOfMockEntities = 100;
		List<SampleEntity> mockEntities = Entities.mockListOf(SampleEntity.class, numberOfMockEntities);
		
		double amountThreshold = 0.75;
		long numberOfEntitesWithAmountBelowThreshold = mockEntities.stream()
			.filter(entity -> entity.getAmount() < amountThreshold)
			.count();
		
		mockEntities.stream()
			.forEach(entityManager::persist);
	
		@SuppressWarnings("unchecked")
		List<SampleEntity> actualEntities = entityManager
				.createNamedQuery(SampleEntity.Query.WITH_AMOUNT_BELOW)
				.setParameter("amountThreshold", amountThreshold)
				.getResultList();
		
		assertThat(actualEntities.stream().count(), 
				is(equalTo(numberOfEntitesWithAmountBelowThreshold)));
		
		assertThat(actualEntities, 
				allOf(
					everyItem(isInCollection(mockEntities)),
					everyItem(hasAmountLowerThan(amountThreshold))
				));
	}

	private static <T> Matcher<T> isInCollection(Collection<T> collection) {
		return new TypeSafeMatcher<T>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("in collection: ")
						.appendValueList("[", ",", "]", collection);
			}

			@Override
			protected boolean matchesSafely(T item) {
				return collection.contains(item);
			}
			
		};
	}
	
	private static Matcher<SampleEntity> hasAmountLowerThan(double threshold) {
		return new TypeSafeMatcher<SampleEntity>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("having amount lower than")
						.appendValue(threshold);
			}
			
			@Override
			protected boolean matchesSafely(SampleEntity item) {
				return item.getAmount() < threshold;
			}
		};
	}
}
