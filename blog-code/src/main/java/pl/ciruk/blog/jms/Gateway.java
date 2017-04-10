package pl.ciruk.blog.jms;

public interface Gateway<T> {
    void receive(T message);

    void send(String destination, String message);
}
