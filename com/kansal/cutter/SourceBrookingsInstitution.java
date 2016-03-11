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

public class SourceBrookingsInstitution extends Source {
	public static final String[] allowedSources = new String[] {"Research", "Blogs"};
	public static final String sourceName = "The Brookings Institution";

	private ArrayList<String> blacklist;

	private String subSource;
	private String startPageURL;
	private Pattern pagePattern;
	private Pattern linkPattern;
	private int startPage;
	private int endPage;

	public SourceBrookingsInstitution(String subSource, int startPage, int endPage) {
		this.articles = new ArrayList<Article>();
		this.subSource = this.getValidSubSource(subSource);
		this.startPage = startPage;
		this.endPage = endPage;

		if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[0])) this.pagePattern = Pattern.compile("(\\/research\\/[a-zA-Z\\-\\_\\/]+)\\\"");
		if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[1])) this.pagePattern = Pattern.compile("(\\/blogs\\/[a-zA-Z\\-\\_\\/]+)\\\"");

		if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[0])) this.startPageURL = "http://www.brookings.edu";
		if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[1])) this.startPageURL = "http://www.brookings.edu/blogs";

		//this.linkPattern = Pattern.compile("(\\/[a-zA-Z0-9\\-\\_\\/]+\\/[a-zA-Z0-9\\-\\_]+\\d+)\\\"");
		this.linkPattern = Pattern.compile("(\\/[a-zA-Z\\-\\_\\/]+\\/\\d+\\/\\d+\\/[a-zA-Z0-9\\-\\_]+)\\\"");
	}

	@Override
	public String[] getAllowedSources() {
		return this.allowedSources;
	}

	@Override
	public void getArticles() {
		this.action = true;
		ArrayList<String> pages = new ArrayList<String>();
		ArrayList<String> visited = new ArrayList<String>();

		pages.add(this.startPageURL);
		if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[1])) {
			for (int i = this.startPage; i <= this.endPage; i++) {
				pages.add(String.format("%s?page=%d", this.startPageURL, i));
			}
		}

		int pagesCrawled = 0;

		while (!pages.isEmpty()) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			String page = pages.remove(0);
			this.printer.printf("[CRAWL %d] Crawling %s\n", (++pagesCrawled), page);
			visited.add(page);

			String pageText = (new Http(page)).get().response();

			Matcher pageMatcher = this.pagePattern.matcher(pageText);
			Matcher linkMatcher = this.linkPattern.matcher(pageText);

			while (pageMatcher.find()) {
				String link = String.format("http://www.brookings.edu%s", pageMatcher.group(1));

				if (!visited.contains(link) && !pages.contains(link) && !this.shouldIgnore(link)) {
					this.printer.println("... [ADD PAGE] " + link);
					pages.add(link);
				}

				if (this.subSource.equals(SourceBrookingsInstitution.allowedSources[1]) && !link.contains("?page=")) {
					for (int i = this.startPage; i <= this.endPage; i++) {
						String new_link = String.format("%s?page=%d", link, i);
						if (!visited.contains(new_link) && !pages.contains(new_link) && !this.shouldIgnore(new_link)) {
							this.printer.println("... [ADD PAGE] " + new_link);
							pages.add(new_link);
						}
					}
				}
			}

			while (linkMatcher.find()) {
				Article article = new Article(String.format("http://www.brookings.edu%s", linkMatcher.group(1)));
				if (!this.articles.contains(article) && !this.shouldIgnore(article.getURL())) {
					this.printer.printf("... [QUEUE %d] %s\n", this.articles.size() + 1, article.getURL());
					this.articles.add(article);
				}
			}
		}

		this.printer.println("--");
	}

	private boolean shouldIgnore(String link) {
		return false;
	}


}
