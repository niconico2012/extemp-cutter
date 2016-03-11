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

public class SourceWashingtonPost extends Source {
	public static final String[] allowedSources = new String[] {"all"};
	public static final String sourceName = "The Washington Post";

	private ArrayList<String> blacklist;

	private String subSource;
	private Pattern pagePattern;
	private Pattern linkPatternA;
	private Pattern linkPatternB;
	private int startPage;
	private int endPage;

	public SourceWashingtonPost(String subSource, int startPage, int endPage) {
		this.articles = new ArrayList<Article>();
		this.blacklist = new ArrayList<String>();

		this.pagePattern = Pattern.compile("(http:\\/\\/www\\.washingtonpost\\.com\\/[a-zA-Z\\-\\_\\/]+)\\\""); //Pattern.compile("href=\\\"(\\/[a-zA-Z\\-\\_\\/]+\\/)");
		this.linkPatternA = Pattern.compile("(http:\\/\\/www\\.washingtonpost\\.com\\/[a-zA-Z\\-\\_\\/]+\\/\\d+\\/\\d+\\/\\d+\\/[a-zA-Z0-9\\-\\_]+\\/)\\\""); //Pattern.compile("href=\\\"(\\/[a-zA-Z\\-\\_\\/]+\\/[a-zA-Z0-9\\-\\_\\/]+\\.html)");
		this.linkPatternB = Pattern.compile("(\\/[a-zA-Z0-9\\-\\_\\/]+\\/\\d+\\/\\d+\\/\\d+\\/[a-zA-Z0-9\\-\\_]+\\.html)");

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

		pages.add("http://www.washingtonpost.com/");
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
			Matcher linkMatcherA = this.linkPatternA.matcher(pageText);
			Matcher linkMatcherB = this.linkPatternB.matcher(pageText);

			while (pageMatcher.find()) {
				String link = pageMatcher.group(1);
				if (!visited.contains(link) && !pages.contains(link) && !this.blacklist.contains(link) && !this.shouldIgnore(link)) {
					this.printer.println("... [ADD PAGE] " + link);
					pages.add(link);
				}
			}

			while (linkMatcherA.find()) {
				Article article = new Article(linkMatcherA.group(1));
				if (!this.articles.contains(article) && !this.shouldIgnore(article.getURL())) {
					this.printer.printf("... [QUEUE %d] %s\n", this.articles.size() + 1, article.getURL());
					this.articles.add(article);
				}
			}

			while (linkMatcherB.find()) {
				Article article = new Article(String.format("http://www.washingtonpost.com%s", linkMatcherB.group(1)));
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
