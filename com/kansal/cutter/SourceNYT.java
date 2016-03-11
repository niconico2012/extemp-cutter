/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */
 
package com.kansal.cutter;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class SourceNYT extends Source {
	public static final String[] allowedSources = new String[] {"all"};
	public static final String sourceName = "New York Times";

	private String subSource;
	private Pattern pagePattern;
	private Pattern linkPattern;
	private int startPage;
	private int endPage;

	public SourceNYT(String subSource, int startPage, int endPage) {
		String s = getValidSubSource(subSource);

		this.articles = new ArrayList<Article>();

		this.pagePattern = Pattern.compile("href=\\\"(http\\:\\/\\/[^n]+?nytimes[^p]+?pages[^\\.]+?\\.html)");
		this.linkPattern = Pattern.compile("href=\\\"(http\\:\\/\\/[^n]+?nytimes[^\\/]+?\\/\\d+\\/\\d+\\/\\d+[^\\.]+?\\.html)");

		this.startPage = startPage;
		this.endPage = endPage;
		this.hack = new Hack();
	}

	@Override
	public final String[] getAllowedSources () {
		return allowedSources;
	}

	@Override
	public void getArticles() {
		this.action = true;
		ArrayList<String> pages = new ArrayList<String>();
		ArrayList<String> visited = new ArrayList<String>();

		pages.add("http://www.nytimes.com/");
		int pagesCrawled = 0;

		while (!pages.isEmpty()) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			String page = pages.remove(0);

			this.printer.printf("[CRAWL %d] %s\n", (++pagesCrawled), page);
			visited.add(page);

			String pageText = (new Http(page).get().response());

			Matcher pageMatcher = this.pagePattern.matcher(pageText);
			Matcher linkMatcher = this.linkPattern.matcher(pageText);

			while (pageMatcher.find()) {
				String link = pageMatcher.group(1);
				if (!visited.contains(link) && !pages.contains(link)) {
					this.printer.println("... [ADD PAGE] " + link);
					pages.add(link);
				}
			}

			while (linkMatcher.find()) {
				if (!this.shouldIgnore(linkMatcher.group(1))){
					Article article = new Article(linkMatcher.group(1));
					if (!this.articles.contains(article)) {
						this.printer.printf("... [QUEUE %d] %s\n", this.articles.size() + 1, linkMatcher.group(1));
						this.articles.add(article);
					}
				}
			}
		}

		this.printer.println("--");
	}

	public boolean shouldIgnore(String link) {
		String[] blacklist = new String[]{
			"/health/","/sports/","/arts/","/fashion/","/dining/","/travel/","/movies/","/theater/","/style/","/automobiles/","/books/","/garden/","greathomesanddestinations/","realestate"
		};

		for (String item : blacklist) {
			if (link.contains(item)) return true;
		}

		return false;
	}
}
