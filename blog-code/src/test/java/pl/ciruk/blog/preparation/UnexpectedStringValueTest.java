package pl.ciruk.blog.preparation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class UnexpectedStringValueTest {
    @Test
    public void shouldReferToPreparationTimeDefault() throws Exception {
        String text = UnexpectedStringValue.instance.member;

        System.out.println(text);

        assertThat(text, is(not(equalTo("Hello world"))));

    }
}