package pl.ciruk.blog.producerconsumer.concurrent;

public class Queueables {
	private Queueables() {
		// Utility class
	}
	
	public static boolean isLastMessage(Queueable message) {
		return message == null || message.isLastMessage();
	}
}
