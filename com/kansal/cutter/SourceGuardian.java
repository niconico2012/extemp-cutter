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

public class SourceGuardian extends Source {
	public static final String[] allowedSources = new String[] {"all"};
	public static final String sourceName = "The Guardian";
	public static final boolean allowProfiles = false;

	private ArrayList<String> blacklist;

	private String subSource;
	private Pattern pagePattern;
	private Pattern linkPattern;
	private int startPage;
	private int endPage;

	public SourceGuardian(String subSource, int startPage, int endPage) {
		this.articles = new ArrayList<Article>();
		this.blacklist = new ArrayList<String>();

		this.blacklist.add("http://www.theguardian.com/uk");
		this.blacklist.add("http://www.theguardian.com/au");
		this.blacklist.add("http://www.theguardian.com/preference/edition/uk");
		this.blacklist.add("http://www.theguardian.com/preference/edition/au");
		this.blacklist.add("http://www.theguardian.com/preference/edition/us");
		this.blacklist.add("http://www.theguardian.com/artanddesign/photography");
		this.blacklist.add("http://www.theguardian.com/lifeandstyle/love-and-sex");
		this.blacklist.add("http://www.theguardian.com/crosswords");
		this.blacklist.add("http://www.theguardian.com/video");

		this.pagePattern = Pattern.compile("(http\\:\\/\\/www\\.theguardian\\.com\\/[a-zA-Z\\-\\_\\/]+)\\\"");
		this.linkPattern = Pattern.compile("(http\\:\\/\\/www\\.theguardian\\.com\\/[a-zA-Z\\-\\_\\/]+\\/\\d+\\/[a-zA-Z]+\\/\\d+\\/[a-zA-Z0-9\\-\\_]+)");

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

		pages.add("http://www.theguardian.com/");
		int pagesCrawled = 0;
		boolean initCrawlPerformed = false;

		while (!pages.isEmpty()) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			String page = pages.remove(0);

			this.printer.printf("[CRAWL %d] %s\n", (++pagesCrawled), page);
			visited.add(page);

			String pageText = (new Http(page).get().response());

			if (!initCrawlPerformed) {
				Matcher pageMatcher = this.pagePattern.matcher(pageText);
				while (pageMatcher.find()) {
					String link = pageMatcher.group(1);
					if (!visited.contains(link) && !pages.contains(link) && !this.blacklist.contains(link) && !this.isAuthorProfile(link)) {
						this.printer.println("... [ADD PAGE] " + link);
						pages.add(link);
					}
				}
				initCrawlPerformed = true;
			}


			Matcher linkMatcher = this.linkPattern.matcher(pageText);

			while (linkMatcher.find()) {
				Article article = new Article(linkMatcher.group(1));
				if (!this.articles.contains(article)) {
					this.printer.printf("... [QUEUE %d] %s\n", this.articles.size() + 1, linkMatcher.group(1));
					this.articles.add(article);
				}
			}
		}

		this.printer.println("--");
	}

	private boolean isAuthorProfile(String link) {
		if (SourceGuardian.allowProfiles) return false;
		return link.matches("\\/profile\\/[a-zA-Z\\-\\_]+");
		//Pattern pattern = Pattern.compile("\\/profile\\/[a-zA-Z\-\_]+");
		//Matcher
	}
}
