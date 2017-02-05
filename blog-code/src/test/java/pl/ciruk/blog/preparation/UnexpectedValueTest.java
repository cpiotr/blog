package pl.ciruk.blog.preparation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnexpectedValueTest {
    @Test
    public void shouldResolveToDefaultIntValue() throws Exception {
        int valueOfMember = UnexpectedValue.instance.member;

        assertThat(valueOfMember, is(equalTo(defaultIntValue())));
    }

    int defaultIntValue() {
        return 0;
    }
}
