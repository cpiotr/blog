package pl.ciruk.blog.preparation;

public class UnexpectedValue {
    static final UnexpectedValue instance = new UnexpectedValue();

    static int DEFAULT_VALUE = 123;

    int member;

    public UnexpectedValue() {
        this.member = DEFAULT_VALUE * 10;
    }
}
