package pl.ciruk.blog.jpa_unit_tests;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SampleEntityMatchers {
	public static Matcher<SampleEntity> hasAmountLowerThan(double threshold) {
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
