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

public class SourceCFR extends Source {
	public static final String[] allowedSources = new String[] {"region","issue"};
	public static final String sourceName = "Council on Foreign Relations";
	private String subSource;
	private Pattern pattern;
	private String linkFormat;
	private int startPage;
	private int endPage;

	public SourceCFR(String subSource, int startPage, int endPage) {
		String s = getValidSubSource(subSource);

		this.articles = new ArrayList<Article>();

		//this.pattern = Pattern.compile("(\\/blogs\\/" + s + "\\/\\d+\\/\\d+\\/[a-zA-Z\\-\\d\\_]+)");
		this.pattern = Pattern.compile("<a href=\\\"(\\/[^\\/]+\\/[^\\/]+\\/p\\d+)\\\">");
		this.linkFormat = String.format("http://www.cfr.org/%s/?page=", s);

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
		int pages = 0;

		if (this.startPage > 0 && this.endPage >= this.startPage) {
			for (int i = this.startPage; i <= this.endPage; i++) {
				if (!this.action) {
					this.printer.println("[QUIT] Signal received.");
					break;
				}

				int count = 0;
				String url = this.makeLink(i + 1);
				this.printer.printf("[CRAWL %d] Fetching page %s... ", (++pages), url);
				Matcher matcher = this.pattern.matcher((new Http(url)).get().response());

				while (matcher.find()) {
					Article article = new Article(String.format("http://www.cfr.org%s", matcher.group(1)), true);
					if (!this.articles.contains(article)) {
						this.articles.add(article);
						count++;
					}
				}

				this.printer.printf(" Found %d articles (%d total)\n", count, this.articles.size());
			}

			this.printer.println("--");
		}
	}

	public String makeLink(int page) {
		if (page <= 0) {
			this.printer.println("[ERR] Invalid page number passed. Using 1.");
			page = 1;
		}

		return String.format("%s%d", this.linkFormat, (page - 1));
	}
}
