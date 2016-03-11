/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class is the main controller for cutting. It is the interface
 * between the GUI and the cutting logic. It determines which class
 * to instantiate in order to cut from that source, and controls its cutting.
 */

package com.kansal.cutter;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class Cutter {
	private Source source;

	public Cutter(int startPage, int endPage, String source, String subSource, PrintManager printer) throws Exception {
		if (source.equals(SourceWSJ.sourceName)) {
			this.source = new SourceWSJ(subSource, startPage, endPage);

		} else if (source.equals(SourceEconomist.sourceName)) {
			this.source = new SourceEconomist(subSource, startPage, endPage);

		} else if (source.equals(SourceNYT.sourceName)) {
			this.source = new SourceNYT(subSource, startPage, endPage);

		} else if (source.equals(SourceGuardian.sourceName)) {
			this.source = new SourceGuardian(subSource, startPage, endPage);

		} else if (source.equals(SourceChicagoTribune.sourceName)) {
			this.source = new SourceChicagoTribune(subSource, startPage, endPage);

		} else if (source.equals(SourceWashingtonPost.sourceName)) {
			this.source = new SourceWashingtonPost(subSource, startPage, endPage);

		} else if (source.equals(SourceNationalInterest.sourceName)) {
			this.source = new SourceNationalInterest(subSource, startPage, endPage);

		} else if (source.equals(SourceBrookingsInstitution.sourceName)){
			this.source = new SourceBrookingsInstitution(subSource, startPage, endPage);

		} else if (source.equals(SourceCFR.sourceName)) {
			this.source = new SourceCFR(subSource, startPage, endPage);

		} else if (source.equals(SourceTheDiplomat.sourceName)){
			this.source = new SourceTheDiplomat(subSource, startPage, endPage);

		} else if (source.equals(SourceFile.sourceName)) {
			this.source = new SourceFile();

		} else {
			throw new Exception("crap.");
		}

		this.source.setPrinter(printer);
	}

	public static final String[] getSourceList() {
		return new String[] {SourceFile.sourceName, SourceWSJ.sourceName, SourceEconomist.sourceName, SourceNYT.sourceName, SourceGuardian.sourceName, SourceChicagoTribune.sourceName, SourceWashingtonPost.sourceName, SourceNationalInterest.sourceName, SourceBrookingsInstitution.sourceName, SourceCFR.sourceName, SourceTheDiplomat.sourceName};
	}

	public static final String[][] getSubSourceList() {
		return new String[][] {SourceFile.allowedSources, SourceWSJ.allowedSources, SourceEconomist.allowedSources, SourceNYT.allowedSources, SourceGuardian.allowedSources, SourceChicagoTribune.allowedSources, SourceWashingtonPost.allowedSources, SourceNationalInterest.allowedSources, SourceBrookingsInstitution.allowedSources, SourceCFR.allowedSources, SourceTheDiplomat.allowedSources};
	}

	public Source getSource() {
		return this.source;
	}

	public void cut() { this.cut(0); }
	public void list() { this.list(0); }
	public void open()  { this.open(0); }

	public void cut(int actionDelimiter) {
		if (this.source != null) {
			this.source.getArticles();
			this.source.cutArticles(actionDelimiter);
		}
	}

	public void list(int actionDelimiter) {
		if (this.source != null) {
			this.source.getArticles();
			this.source.listArticles(actionDelimiter);
		}
	}

	public void open(int actionDelimiter) {
		if (this.source != null) {
			this.source.getArticles();
			this.source.openArticles(actionDelimiter);
		}
	}

	public void save() {
		if (this.source != null) {
			if (this.source.articles.size() > 0) {
				this.source.addToFile();
			}
		}
	}

	public void stop() {
		if (source != null) {
			this.source.stop();
		}
	}
}
