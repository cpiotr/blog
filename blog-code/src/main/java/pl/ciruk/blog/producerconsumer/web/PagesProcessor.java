package pl.ciruk.blog.producerconsumer.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import pl.ciruk.blog.producerconsumer.concurrent.Consumer;
import pl.ciruk.blog.producerconsumer.concurrent.Processable;
import pl.ciruk.blog.producerconsumer.concurrent.Producer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PagesProcessor {
	Map<String, Producer<Page>> producers = new HashMap<>();
	
	ExecutorService executor = Executors.newFixedThreadPool(4);
	
	public void createProducer(final String producerId) {
		BlockingQueue<Page> queue = new LinkedBlockingQueue<Page>();
		
		Producer<Page> producer = new Producer<>(queue);
		producers.put(producerId, producer);
		
		Consumer<Page> consumer = new Consumer<>(queue, new Processable<Page>() {
			@Override
			public void process(Page element) {
				long start = System.currentTimeMillis();
				element.getContent();
				System.out.format("[Producer: %s] Fetched '%s' in %d ms\n", producerId, element.getUrl(), (System.currentTimeMillis()-start));
			}
		});
		executor.execute(consumer);
	}
	
	public void process(String producerId, String url) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(producerId), "ProducerId cannot be empty");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "Url cannot be empty");
		Preconditions.checkArgument(producers.containsKey(producerId), "No producers was found for given id: " + producerId);
		
		Producer<Page> producer = producers.get(producerId);
		producer.send(new Page(url));
	}
	
	public void endProcessing() {
		for (Producer<Page> producer : producers.values()) {
			producer.send(Page.LAST_PAGE);
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		PagesProcessor pagesProcessor = new PagesProcessor();
		pagesProcessor.createProducer("P1");
		pagesProcessor.createProducer("P2");
		
		List<String> p1Urls = Lists.newArrayList(
				"http://www.metacritic.com/movie/dead-poets-society",
				"http://www.filmweb.pl/Stowarzyszenie.Umarlych.Poetow#",
				"http://www.rottentomatoes.com/m/dead_poets_society/");
		List<String> p2Urls = Lists.newArrayList(
				"http://www.imdb.com/title/tt0097165/",
				"http://www.nytimes.com/movie/review?res=950DE0DE1F31F931A35755C0A96F948260",
				"http://www.rollingstone.com/movies/reviews/dead-poets-society-19890609");
		
		for (int i = 0; i < Math.min(p1Urls.size(), p2Urls.size()); i++) {
			pagesProcessor.process("P1", p1Urls.get(i));
			pagesProcessor.process("P2", p2Urls.get(i));
		}
		
		pagesProcessor.endProcessing();
		
		System.out.format("Processed in %d ms\n", (System.currentTimeMillis()-start));
	}
}
