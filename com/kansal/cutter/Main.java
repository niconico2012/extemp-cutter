/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class is the interface to the cutter. It contains the main routine.
 */

package com.kansal.cutter;

import javafx.collections.*;
import javafx.beans.value.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import java.util.*;
import java.io.*;

public class Main extends Application {
	public enum Action {
		LIST, OPEN, CUT, NONE;
	}

	public enum State {
		CUTTING, LISTING, OPENING, NONE;
	}

	private String[] sources = Cutter.getSourceList();
	private String[][] subSources = Cutter.getSubSourceList();

	//private String[] sources = new String[] {SourceFile.sourceName, SourceWSJ.sourceName, SourceEconomist.sourceName, SourceNYT.sourceName, SourceGuardian.sourceName, SourceChicagoTribune.sourceName, };
	//private String[][] subSources = new String[][] {SourceFile.allowedSources, SourceWSJ.allowedSources, SourceEconomist.allowedSources, SourceNYT.allowedSources, SourceGuardian.allowedSources, SourceChicagoTribune.allowedSources};

	private State state;
	private Action action;

	private TextArea textArea;
	private TextField startPageField;
	private TextField endPageField;
	private TextField limitField;

	private int selectedSourceIndex;
	private String selectedSubSource;

	private Cutter cutter;

	private int startPage;
	private int endPage;
	private int limit;

	private PrintManager printer;

	@Override
	public void start(Stage stage) {
		System.out.println("> [INIT] LAUNCHING APPLICATION...");

		this.state = State.NONE;
		this.action = Action.NONE;
		this.selectedSourceIndex = -1;
		this.selectedSubSource = "";

		BorderPane pane = new BorderPane();

		// TOP BOX

		HBox topBox = new HBox();
		topBox.setPadding(new Insets(15, 12, 15, 12));
		topBox.setSpacing(10);
		topBox.setStyle("-fx-background-color: #336699;");

		Label title = new Label("Extemp Article Cutter");
		title.setTextFill(Color.web("#ffffff"));
		title.setFont(Font.font("Helvetica", FontWeight.LIGHT, 36));
		//title.setFont(new Font("Arial", 36));

		topBox.getChildren().add(title);
		pane.setTop(topBox);

		// BOTTOM BOX

		HBox bottomBox = new HBox();
		bottomBox.setPadding(new Insets(15, 12, 15, 12));
		bottomBox.setSpacing(10);
		bottomBox.setStyle("-fx-background-color: #ffffff");

		this.textArea = new TextArea();
		textArea.setPrefRowCount(18);
		textArea.setPrefColumnCount(100);
		textArea.setWrapText(false);
		textArea.setPrefWidth(870);

		if (PlatformUtils.isMac()) textArea.setFont(Font.font("Monaco", FontWeight.NORMAL, 11));
		if (PlatformUtils.isWindows()) textArea.setFont(Font.font("Courier New", FontWeight.NORMAL, 11));

		bottomBox.getChildren().add(this.textArea);
		pane.setBottom(bottomBox);

		// LEFT BOX

		VBox leftBox = new VBox();
		leftBox.setPadding(new Insets(20,10,10,20));
		leftBox.setSpacing(10);
		//leftBox.setStyle("-fx-border-right: 1px solid #000000");
		leftBox.setPrefWidth(200);

		Label optionsTitle = new Label("Options");
		optionsTitle.setTextFill(Color.web("#336699"));

		if (PlatformUtils.isMac()) optionsTitle.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 20));
		if (PlatformUtils.isWindows()) optionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		ToggleGroup group = new ToggleGroup();

		group.selectedToggleProperty().addListener(
			(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
				if (group.getSelectedToggle() != null) {
					if (group.getSelectedToggle().getUserData().toString().equals("list")) {
						this.action = Action.LIST;
					} else if (group.getSelectedToggle().getUserData().toString().equals("open")) {
						this.action = Action.OPEN;
					} else if (group.getSelectedToggle().getUserData().toString().equals("cut")) {
						this.action = Action.CUT;
					} else {
						this.action = Action.NONE;
					}
				}
			}
		);

		RadioButton listButton = new RadioButton("List Articles");
		RadioButton openButton = new RadioButton("Open Articles");
		RadioButton cutButton  = new RadioButton("Cut Articles");

		listButton.setUserData("list");
		openButton.setUserData("open");
		cutButton.setUserData("cut");

		listButton.setToggleGroup(group);
		openButton.setToggleGroup(group);
		cutButton.setToggleGroup(group);

		Label startPageLabel = new Label("Start: ");
		Label endPageLabel = new Label("End:   ");
		Label limitLabel = new Label("Limit: ");

		this.startPageField = new TextField();
		startPageField.setPrefColumnCount(5);
		startPageField.setText("0");

		this.endPageField = new TextField();
		endPageField.setPrefColumnCount(5);
		endPageField.setText("0");

		this.limitField = new TextField();
		limitField.setPrefColumnCount(5);
		limitField.setText("0");

		HBox startPageBox = new HBox();
		startPageBox.setAlignment(Pos.CENTER);

		HBox endPageBox = new HBox();
		endPageBox.setAlignment(Pos.CENTER);

		HBox limitBox = new HBox();
		limitBox.setAlignment(Pos.CENTER);

		startPageBox.getChildren().addAll(startPageLabel, this.startPageField);
		endPageBox.getChildren().addAll(endPageLabel, this.endPageField);
		limitBox.getChildren().addAll(limitLabel, this.limitField);

		leftBox.getChildren().add(optionsTitle);
		leftBox.getChildren().addAll(listButton, openButton, cutButton);
		leftBox.getChildren().addAll(startPageBox, endPageBox, limitBox);
		pane.setLeft(leftBox);

		HBox centerBox = new HBox();
		centerBox.setPadding(new Insets(20, 20, 20, 20));
		centerBox.setSpacing(10);

		ListView<String> sourceList = new ListView<String>();
		ListView<String> subSourceList = new ListView<String>();

		sourceList.getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends String> ov, String old_string, String new_string) -> {
				for (int i = 0; i < sources.length; i++) {
					if (sources[i].equals(new_string)) {
						this.selectedSourceIndex = i;
						subSourceList.setItems(FXCollections.observableArrayList(subSources[i]));
					}
				}
			}
		);

		subSourceList.getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends String> ov, String old_string, String new_string) -> {
				for (int i = 0; i < subSources[this.selectedSourceIndex].length; i++) {
					if (subSources[this.selectedSourceIndex][i].equals(new_string)) {
						this.selectedSubSource = subSources[this.selectedSourceIndex][i];
					}
				}
			}
		);

		sourceList.setItems(FXCollections.observableArrayList(this.sources));

		centerBox.getChildren().addAll(sourceList, subSourceList);
		pane.setCenter(centerBox);

		VBox rightBox = new VBox();
		rightBox.setPadding(new Insets(20, 20, 20, 20));
		rightBox.setSpacing(10);
		rightBox.setPrefWidth(200);

		Label actionTitle = new Label("Actions");
		actionTitle.setTextFill(Color.web("#336699"));

		if (PlatformUtils.isMac()) actionTitle.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 20));
		if (PlatformUtils.isWindows()) actionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button clearButton = new Button("Clear");
		Button saveButton = new Button("Save to File");

		startButton.setOnAction((ActionEvent e) -> {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						start_cutter();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			};

			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
		});


		stopButton.setOnAction((ActionEvent e) -> {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						stop_cutter();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			};

			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
		});

		clearButton.setOnAction((ActionEvent e) ->{
			this.textArea.clear();
		});

		saveButton.setOnAction((ActionEvent e) -> {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						cutter.save();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			};

			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
		});

		rightBox.getChildren().add(actionTitle);
		rightBox.getChildren().addAll(startButton, stopButton, clearButton, saveButton);

		pane.setRight(rightBox);

		this.printer = new PrintManager(this.textArea);

		stage.setScene(new Scene(pane, 900, 640));
		stage.setResizable(false);
		stage.show();

		System.out.println("> [INIT] ... COMPLETED.");
		System.out.println("> [INFO] PREPD BUTTON NEEDS TO BE CENTERED AT POINT (830, 78), ORIGIN (0, 0) IS TOP-LEFT CORNER.");
		System.out.println("> [INFO] PREPD MUST ALREADY BE SIGNED INTO.");
		if (PlatformUtils.isMac()) System.out.println("> [INFO] GOOGLE CHROME MUST BE LOCATED AT /Applications/Google Chrome.app .");

	}

	public void start_cutter() {
		if (this.selectedSourceIndex >= 0 && this.selectedSourceIndex < this.sources.length) {
			if (this.selectedSubSource != null && this.selectedSubSource.length() > 0) {
				try {
					this.startPage = Integer.parseInt(this.startPageField.getText());
					this.endPage = Integer.parseInt(this.endPageField.getText());
					this.limit = Integer.parseInt(this.limitField.getText());
					this.cutter = null;
					this.cutter = new Cutter(this.startPage, this.endPage, this.sources[this.selectedSourceIndex], this.selectedSubSource, this.printer);
				} catch (NullPointerException e) {
					this.printer.println("[ERR] Select another source and try again.");
					e.printStackTrace();
				} catch (NumberFormatException e) {
					this.printer.println("[ERR] Invalid input for Start, End, or Limit.");
				} catch (Exception e) {
					this.printer.println("[ERR] Unknown error.");
					this.printer.printf("[ERR] Error: %s\n", e);
					this.printer.printf("[ERR] Message: %s\n", e.getMessage());
					this.printer.printf("[ERR] Stack trace: %s\n", e.getStackTrace().toString());
				}
			}
		}

		if (this.cutter != null) {
			if (this.action == Action.CUT) {
				this.disable();
				this.state = State.CUTTING;
				this.cutter.cut(this.limit);
				this.state = State.NONE;
			} else if (this.action == Action.LIST) {
				this.disable();
				this.state = State.LISTING;
				this.cutter.list(this.limit);
				this.state = State.NONE;
			} else if (this.action == Action.OPEN) {
				this.disable();
				this.state = State.OPENING;
				this.cutter.open(this.limit);
				this.state = State.NONE;
			}
		}
	}

	public void stop_cutter() {
		if (this.state != null && this.state != State.NONE) {
			this.printer.println("[INFO] Stop signal sent... Waiting for target...");
			this.enable();
			this.state = State.NONE;
			this.cutter.stop();
		}
	}

	public void disable() {

	}

	public void enable() {

	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.println("=== EXTEMP ARTICLE CUTTER v2.0.14 ===");
		System.out.println("DEVELOPER: Nikhil Kansal");
		System.out.println("CONTACT: nkansal96@gmail.com");
		System.out.print("Use of this program is only allowed by authorized individuals/machines and implies unconditional acceptence of these Terms of Use. ");
		System.out.print("Unauthorized use and/or attempting to force authority to use this application in any way (including but not restricted to tampering with internal functions, imitating another machine, registering someone else's key, and hostilely contacting the Extemp Cutter Server in an attempt to disable or break into the service) is not allowed and violators will be persecuted to the full extent of the law. ");
		System.out.print("You may not redistribute, repackage, rebrand, or modify this program as a whole or any part of it without express, written consent of the developer. ");
		System.out.print("Functionality is provided AS-IS and the developer will not be held responsible for any damages or losses whatsoever to the User, the User's property, the User's computer, the User's Prepd Account, the User's standing within his/her Extemp team, the User's standing within the Extemp society, the User's Internet Service Provider, or any Web Service the program contacts. ");
		System.out.print("The developer reserves the right to deny or revoke access to this service to any User and any Machine at any time, regardless of any prior agreement, payment, or access policy. ");
		System.out.println("Use of this software is subject to all applicable laws governing such Software and Software as a Service. ");
		System.out.println();
		System.out.println("Unauthorized Access.");
		return;
		
		System.out.println();
		System.out.print("> ACCEPT TERMS OF USE? [Y/N]: ");

		String response = scanner.nextLine();
		if (response.equals("Y")) {
			System.out.println("> TERMS ACCEPTED.");
			System.out.print("> ENTER ACCESS KEY: ");

			String key = scanner.nextLine();
			System.out.println("> VALIDATING...");
			System.out.println("> ...");

			if (Security.validateMachineAccess(key)) {
				System.out.println("> ACCESS GRANTED.");
				Application.launch(args);
			} else {
				System.out.println("> ACCESS DENIED. CONFIRM THAT YOU HAVE PERMISSION TO EXECUTE THIS PROGRAM.");
				System.out.println("> MACH_REF_HWID: " + Security.getHardwareID());
				System.exit(0);
			}
		} else {
			System.out.println("> TERMS DECLINED. QUITTING PROGRAM.");
			System.exit(0);
		}
	}
}
