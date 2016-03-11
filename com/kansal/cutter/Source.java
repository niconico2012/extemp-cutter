/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class represents the base class of each kind of source, providing
 * common functionality and properties to all sources. It itself cannot be
 * instantiated, as that would make no sense. There are methods that all
 * sources will have. The idea is that each source is only different through
 * the fact that it has a unique way of finding article links on that webpage.
 * The actual cutting and the rest of the job remains the same, so it was
 * factored out in a robust, maintainable, extensible way (following OOP design).
 */

package com.kansal.cutter;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public abstract class Source {
	public static final String compiledAlreadyCutSerializationFileName = "data/cut_articles";
	public static String[] allowedSources;

	protected ArrayList<Article> articles;
	protected ArrayList<Article> alreadyCut;
	protected Hack hack;
	protected PrintManager printer;
	protected boolean action = false;

	private static final Point prepdButton = new Point(830, 78);
	private static final Point tagInput = new Point(Source.prepdButton.x - 300, Source.prepdButton.y + 190);
	private static final Point catchArticleButton = new Point(Source.prepdButton.x - 20, Source.prepdButton.y + 250);

	abstract public void getArticles();

	public String getValidSubSource(String subSource) {
		for (String source : this.getAllowedSources()) {
			if (source.equals(subSource)) {
				return subSource;
			}
		}
		return allowedSources[0];
	}

	public String[] getAllowedSources() {
		return allowedSources;
	}

	public void cutArticles() {
		this.cutArticles(0);
	}

	public void setPrinter(PrintManager printer) {
		this.printer = printer;
	}

	public void stop() {
		this.action = false;
	}

	public void cutArticles(int limit) {
		this.action = true;

		if (limit == 0 || limit >= this.articles.size()) limit = this.articles.size();
		this.printer.println("[INIT] Cutting " + limit + " articles...");
		int count = 0;

		for (int i = 0; i < limit; i++) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			Article article = (Article) this.articles.get(i);
			if (this.hasAlreadyCutArticle(article)) {
				this.printer.println("[SKIP] Already cut: " + article.getURL());
				continue;
			}

			article.open();
			this.printer.println("[CUT " + (++count) + "] Cutting " + article.getURL());

			this.hack.wait(8000); // Initial wait before opening Prepd -- wait for article to load.
			this.hack.leftClick(Source.prepdButton, 200, 500); // open prepd article catcher

			int wait_count = 0;
			while ((wait_count++) < 20) {
				Color pointColor = this.hack.getColorAt(Source.catchArticleButton);
				if (pointColor.equals(new Color(252,129,40))) break;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					this.printer.println("[ERR] Thread terminated (???).");
				}
			}

			this.hack.leftClick(Source.tagInput, 200, 500); // focus on tag input

			for (String tag : article.getTags()) {
				// copy tag to clipboard
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection data = new StringSelection(tag);
				clipboard.setContents(data, data);

				// paste tag
				this.hack.getRobot().keyPress(Hack.Key.KEY_CMD);
				this.hack.getRobot().keyPress(Hack.Key.CHR_V);
				this.hack.wait(50);
				this.hack.getRobot().keyRelease(Hack.Key.KEY_CMD);
				this.hack.getRobot().keyRelease(Hack.Key.CHR_V);
				this.hack.wait(400);

				// press enter (validate tag)
				this.hack.getRobot().keyPress(Hack.Key.KEY_ENTER);
				this.hack.wait(50);
				this.hack.getRobot().keyRelease(Hack.Key.KEY_ENTER);
				this.hack.wait(400);
			}

			// press shift-enter (submit article to catcher)
			this.hack.getRobot().keyPress(Hack.Key.KEY_SHIFT);
			this.hack.getRobot().keyPress(Hack.Key.KEY_ENTER);
			this.hack.wait(50);
			this.hack.getRobot().keyRelease(Hack.Key.KEY_SHIFT);
			this.hack.getRobot().keyRelease(Hack.Key.KEY_ENTER);
			this.hack.wait(8000);

			// press cmd-w (close tab - free resources)
			this.hack.getRobot().keyPress(Hack.Key.KEY_CMD);
			this.hack.getRobot().keyPress(Hack.Key.CHR_W);
			this.hack.wait(50);
			this.hack.getRobot().keyRelease(Hack.Key.KEY_CMD);
			this.hack.getRobot().keyRelease(Hack.Key.CHR_W);
			this.hack.wait(400);

			this.doneCutting(article);
		}
	}

	public void openArticles(int limit) {
		if (limit == 0 || limit >= this.articles.size()) limit = this.articles.size();

		for (int i = 0; i < limit; i++) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			this.printer.printf("[OPEN %d] Opening %s\n", (i + 1), ((Article) this.articles.get(i)).getURL());
			((Article) this.articles.get(i)).open();
		}

		this.printer.println("--\n");
	}

	public void listArticles(int limit) {
		if (limit == 0 || limit >= this.articles.size()) limit = this.articles.size();

		for (int i = 0; i < limit; i++) {
			if (!this.action) {
				this.printer.println("[QUIT] Signal received.");
				break;
			}

			this.printer.printf("[LIST %d] %s\n", (i + 1), ((Article) this.articles.get(i)).getURL());
		}

		this.printer.println("--\n");
	}

	public void addToFile() {
		this.printer.println("[SAVE] Saving links to file...");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(SourceFile.fileName, true));
			for (Article a : this.articles) {
				writer.write(a.getURL());
				writer.newLine();
				writer.flush();
			}

			writer.close();
			this.printer.println("[SAVE] Saved " + this.articles.size() + " articles.");
		} catch (Exception e) {
			this.printer.println("[ERR] Could not save articles to file.");
		}
	}

	private boolean hasAlreadyCutArticle(Article a) {
		this.loadAlreadyCutArticles();
		return this.alreadyCut.contains(a);
	}

	private void doneCutting(Article a) {
		this.loadAlreadyCutArticles();

		if (!this.alreadyCut.contains(a)) {
			this.alreadyCut.add(a);
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Source.compiledAlreadyCutSerializationFileName));
			oos.writeObject(this.alreadyCut);
			oos.close();
		} catch (Exception e) {
			this.printer.println("[ERR] Serialize failed.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadAlreadyCutArticles() {
		if (this.alreadyCut == null) {
			File file = new File(Source.compiledAlreadyCutSerializationFileName);
			if (file.exists() && !file.isDirectory()) {

				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Source.compiledAlreadyCutSerializationFileName));
					this.alreadyCut = (ArrayList<Article>) ois.readObject();
					ois.close();
				} catch (Exception e) {
					this.printer.println("[ERR] Deserialize failed.");
					e.printStackTrace();
				}
			} else {
				this.alreadyCut = new ArrayList<Article>();
			}
		}
	}
}
