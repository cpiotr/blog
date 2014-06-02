package pl.ciruk.blog.jpa_unit_tests;

import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CollectionMatchers {
	public static <T> Matcher<T> isInCollection(Collection<T> collection) {
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
}
