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

public class SourceFile extends Source {
	public static final String[] allowedSources = new String[] {"all"};
	public static final String sourceName = "File";
	public static final String fileName = "data/articles_file";
	private Pattern linkPattern;

	public SourceFile() {
		this.articles = new ArrayList<Article>();
		this.linkPattern = Pattern.compile("^(http(?:s)?\\:\\/\\/.+)$");
		this.hack = new Hack();
	}

	@Override
	public String[] getAllowedSources() {
		return this.allowedSources;
	}

	@Override
	public void getArticles() {
		this.action = true;
		this.printer.println("[LOAD] Loading links from file...");
		try (BufferedReader br = new BufferedReader(new FileReader(new File(SourceFile.fileName)))) {
			for (String line; (line = br.readLine()) != null; ) {
				Matcher matcher = this.linkPattern.matcher(line);
				while (matcher.find()) {
					Article article = new Article(matcher.group(1));
					if (!this.articles.contains(article)) {
						this.articles.add(article);
					}
				}
			}
		} catch (Exception e) {
			this.printer.println("[ERR] Unable to open and read from file.");
		}

		this.printer.println("[LOAD] Loaded " + this.articles.size() + " articles.");
	}
}
