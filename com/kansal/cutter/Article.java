/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class is a digital representation of an article.
 * It is used as a container to encapsulate the process of finding
 * article tags and storing relate information during the duration of
 * the program runtime and to persist which articles have been cut.
 */

package com.kansal.cutter;

import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class Article implements Serializable {
	private static final long serialVersionUUID = 2084829751089517234l;

	private String url;
	private ArrayList<String> tags;
	private boolean skipFirstTag;

	public Article(String url) {
		this.url = url;
		this.tags = new ArrayList<String>();
		this.skipFirstTag = false;
	}

	public Article(String url, boolean skipFirstTag) {
		this.url = url;
		this.tags = new ArrayList<String>();
		this.skipFirstTag = skipFirstTag;
	}

	public ArrayList<String> getTags() {
		if (this.tags.size() > 0) {
			return this.tags;
		}

		// force all new articles into folder: 'Categorize These'
		//this.tags.add("Categorize These");
		//return this.tags;

		// rest of the code

		Http http = new Http(this.url);
		String response = http.get().response();
		//System.out.println(response);

		Pattern pattern = Pattern.compile("<meta.*?news\\_keywords\\\".*?\\=.*?\\\"([^\\\"]+)\\\"");
		Matcher matcher = pattern.matcher(response);

		while (matcher.find()) {
			String[] t = matcher.group(1).split("[,;]");
			for (String tag : t){
				if (!this.tags.contains(tag.trim())) {
					this.tags.add(tag.trim());
					//System.out.println(tag);
				}
			}
		}

		if (this.tags.size() > 0) {
			if (this.skipFirstTag) {
				this.tags.remove(0);
			}
		}

		if (this.tags.size() == 0) {
			this.tags.add("Miscellaneous");
		}

		return this.tags;
	}

	public void open() {
		try {
			if (PlatformUtils.isMac()) (new ProcessBuilder("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", this.url)).start();
			if (PlatformUtils.isWindows()) (new ProcessBuilder("cmd", "/c", "start", "chrome", this.url)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getURL() {
		return this.url;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;

		final Article other = (Article) obj;
		return (this.getURL().equals(other.getURL()));
	}
}
