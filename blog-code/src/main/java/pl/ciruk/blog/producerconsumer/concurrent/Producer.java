package pl.ciruk.blog.producerconsumer.concurrent;

import java.util.concurrent.BlockingQueue;

public class Producer<T extends Queueable> {
	private BlockingQueue<T> queue;
	
	public Producer(BlockingQueue<T> queue) {
		this.queue = queue;
	}
	
	public void send(T message) {
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
