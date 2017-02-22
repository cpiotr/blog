package pl.ciruk.blog.preparation;

public class UnexpectedStringValue {
    static final UnexpectedStringValue instance = new UnexpectedStringValue();

    static String DEFAULT_VALUE = " world";

    String member;

    public UnexpectedStringValue() {
        this.member = "Hello" + DEFAULT_VALUE;
    }
}
