/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class reroutes output from the standard console output into
 * a textarea in the GUI so the user can observe the cutter's progress.
 */

package com.kansal.cutter;

import javafx.scene.control.*;
import javafx.application.Platform;
import java.util.*;
import java.io.*;

public class PrintManager {
	private TextArea textArea;
	public PrintManager(TextArea textArea) {
		this.textArea = textArea;
	}

	public void print(Object s) {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				textArea.appendText(s.toString());
			}
		});
	}

	public void println(Object s) {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				textArea.appendText(String.format("%s\n", s.toString()));
			}
		});
	}

	public void printf(String format, Object... arguments) {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				textArea.appendText(String.format(format, arguments));
			}
		});
	}
}
