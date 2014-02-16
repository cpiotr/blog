package pl.ciruk.blog.producerconsumer.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer<T extends Queueable> implements Runnable {
	private BlockingQueue<T> queue;
	
	private Processable<T> processor;
	
	public Consumer(BlockingQueue<T> queue) {
		this(queue, Processable.EMPTY);
	}
	
	public Consumer(BlockingQueue<T> queue, Processable<T> processor) {
		this.queue = queue;
		this.processor = processor;
	}

	@Override
	public void run() {
		
		try {
			consumeMessages();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void consumeMessages() throws InterruptedException {
		while (true) {
			T message = queue.poll(1, TimeUnit.SECONDS);
			if (Queueables.isLastMessage(message)) {
				return;
			}
			
			processor.process(message);
		}
	}
}
