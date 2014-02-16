package pl.ciruk.blog.producerconsumer.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import pl.ciruk.blog.producerconsumer.concurrent.Queueable;

public class Page implements Queueable {
	public static final Page LAST_PAGE = new Page() {
		@Override
		public boolean isLastMessage() {
			return true;
		}
	};
	
	private String urlAddress;
	
	private String content;
	
	private Page() {
		
	}
	
	public Page(String url) {
		this.urlAddress = url;
	}
	
	@Override
	public boolean isLastMessage() {
		return false;
	}
	
	public String getUrl() {
		return urlAddress;
	}
	
	public String getContent() {
		if (content == null) {
			populateContent();
		}
		
		return content;
	}
	
	private void populateContent() {
		try {
			URL url = new URL(urlAddress);
			content = readContentFromStream(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String readContentFromStream(InputStream is) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			char[] buffer = new char[1024];
			int length = -1;
			while ((length = reader.read(buffer)) > -1) {
				contentBuilder.append(buffer, 0, length);
			}
		}
		
		return contentBuilder.toString();
	}
}