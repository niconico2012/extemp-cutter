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

public class SourceTheDiplomat extends Source {
	public static final String[] allowedSources = new String[] {"all"};
	public static final String sourceName = "The Diplomat";

	private ArrayList<String> blacklist;

	private String subSource;
	private String startPageURL;
	private Pattern pagePattern;
	private Pattern linkPattern;
	private int startPage;
	private int endPage;

	public SourceTheDiplomat(String subSource, int startPage, int endPage) {
		this.articles = new ArrayList<Article>();
		this.subSource = this.getValidSubSource(subSource);
		this.startPage = startPage;
		this.endPage = endPage;

		this.pagePattern = Pattern.compile("href=\\\"(http:\\/\\/thediplomat\\.com\\/[a-zA-Z\\-]+\\/[a-zA-Z\\-]+)\\\"");
		this.startPageURL = "http://thediplomat.com/";
		this.linkPattern = Pattern.compile("href=\\\"(http:\\/\\/thediplomat\\.com\\/\\d+\\/\\d+\\/[^\\/]+\\/)\\\"");
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
				String link = pageMatcher.group(1);

				if (!visited.contains(link) && !pages.contains(link) && !this.shouldIgnore(link)) {
					this.printer.println("... [ADD PAGE] " + link);
					pages.add(link);
				}
			}

			while (linkMatcher.find()) {
				Article article = new Article(linkMatcher.group(1));
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
		//return link.contains("/profile/") || link.contains("/taxonomy/") || link.contains("/issue/");
	}


}
