package pl.ciruk.blog.producerconsumer.concurrent;

public interface Processable<T> {
	Processable EMPTY = new Processable<Object>() {
		@Override
		public void process(Object element) {
			// Nothing to do, just visiting
		}
		
	};
	
	void process(T element);
}
