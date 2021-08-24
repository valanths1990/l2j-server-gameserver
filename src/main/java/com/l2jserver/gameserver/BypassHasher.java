package com.l2jserver.gameserver;

import org.apache.logging.log4j.core.layout.PatternMatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BypassHasher {
	private Map<Integer, String> hashedBypasses = new ConcurrentHashMap<>();

	public String hashBypassesFromHtml(String html) {
		Matcher pattern = Pattern.compile("\"bypass (.+?\")").matcher(html);
		while (pattern.find()) {
			String bypass = pattern.group().replaceAll("bypass", "").replace("\"", "").trim();
			html = html.replaceFirst(bypass, String.valueOf(bypass.hashCode()));
			hashedBypasses.put(bypass.hashCode(), bypass);
		}
		return html;
	}
	public void clearBypasses(){
		hashedBypasses.clear();
	}

	public String decodeHash(Integer hash){
		return hashedBypasses.get(hash);
	}

	public static BypassHasher getInstance() {
		return BypassHasher.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final BypassHasher INSTANCE = new BypassHasher();
	}

	public static void main(String[] args) {
		String html = loadFile(new File("C:\\Users\\Silvar\\Desktop\\l2newestrelease\\hmtlCompressor\\gmshop.htm"));
		BypassHasher.getInstance().hashBypassesFromHtml(html);
	}

	private static String loadFile(File file) {

		String content = null;
		try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
			final int bytes = bis.available();
			final byte[] raw = new byte[bytes];

			bis.read(raw);
			content = new String(raw, StandardCharsets.UTF_8);
			content = content.replaceAll("(?s)<!--.*?-->", ""); // Remove html comments
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
