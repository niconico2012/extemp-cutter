/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class is a custom class written to manipulate the GUI and enable
 * the cutter to interact with Prepd's Chrome Extension and cut the article.
 * It is a general purpose GUI interaction class.
 */

package com.kansal.cutter;

public class Hack
{
	private final static short DEFAULT_MOUSE_ACTION_DELAY = 20; // milliseconds to delay after a mouse event
	private final static short DEFAULT_KEY_PRESS_DELAY = 20; // milliseconds to delay after key press
	private final static int LEFT_MOUSE_BUTTON = java.awt.event.InputEvent.BUTTON1_DOWN_MASK; // bind local constant to InputEvent for left mouse button
	private final static int MID_MOUSE_BUTTON = java.awt.event.InputEvent.BUTTON2_DOWN_MASK; // bind local constant to InputEvent for middle mouse button
	private final static int RIGHT_MOUSE_BUTTON = java.awt.event.InputEvent.BUTTON3_DOWN_MASK; // bind local constant to InputEvent for right mouse button
	private java.awt.Robot robot = null; // instance Robot variable

	/**
	 * The one and only constructor for this class...
	 * Instantiates the object and assigns an instance <code>Robot</code>
	 * @return a reference to the instanciated <code>Hack</code> object
	 */

	public Hack()
	{
		try {
			this.robot = new java.awt.Robot(); // instantiate Robot on object construction
		} catch (Exception e) {
			System.err.println("An error occured while creating the Hack Robot");
			e.printStackTrace();
		}
	}

	/**
	 * Delays execution and events for a certain amount of time.
	 * -- Essentially just calls <code>java.awt.Robot.wait(int)</code>
	 * @param delay  time in milliseconds to wait (int)
	 */

	public void wait(int delay)
	{
		this.robot.delay(delay);
	}

	/**
	 * Gets the instance <code>Robot</code> variable for manual manipulation (unadvised)
	 * @return the class instance <code>Robot</code> variable (<code>java.awt.Robot</code>)
	 */

	public java.awt.Robot getRobot()
	{
		return this.robot;
	}

	/**
	 * Gets the color of the screen at the indicated point
	 * -- Essentially just calls <code>java.awt.Robot.getPixelColor(int, int)</code>
	 * @param point The point at which to sample the color (<code>java.awt.Point</code>)
	 * @return 		The color of the specified point (<code>java.awt.Color</code>)
	 */

	public java.awt.Color getColorAt(java.awt.Point point)
	{
		return this.robot.getPixelColor((int) point.getX(), (int) point.getY());
	}

	public void pressKey(int key) {
		this.pressKey(key, 0);
	}

	public void pressKey(int key, int delay) {
		this.robot.keyPress(key);
		this.robot.delay(Hack.DEFAULT_KEY_PRESS_DELAY);
		this.robot.delay(delay);
		this.robot.keyRelease(key);
	}

	/**
	 * Moves to mouse to the specified location, and adds the default time delay
	 * @param point The point to move the mouse to (<code>java.awt.Point</code>)
	 */

	public void moveMouseTo(java.awt.Point point)
	{
		this.moveMouseTo((int)point.getX(), (int)point.getY());
	}

	private void moveMouseTo(int x, int y)
	{
		if (!(x >= 0 && y >= 0 && x <= this.getScreenWidth() && y <= this.getScreenHeight())) return;
		this.robot.mouseMove(x, y);
		this.robot.delay(Hack.DEFAULT_MOUSE_ACTION_DELAY);
	}

	/**
	 * Fires the left click event at the current mouse position
	 */

	public void leftClick()
	{
		this.click(Hack.LEFT_MOUSE_BUTTON, this.getMouseX(), this.getMouseY(), 0, 0);
	}

	/**
	 * Fires the middle click event at the current mouse position
	 */

	public void middleClick()
	{
		this.click(Hack.MID_MOUSE_BUTTON, this.getMouseX(), this.getMouseY(), 0, 0);
	}

	/**
	 * Fires the right click event at the current mouse position
	 */

	public void rightClick()
	{
		this.click(Hack.RIGHT_MOUSE_BUTTON, this.getMouseX(), this.getMouseY(), 0, 0);
	}

	/**
	 * Moves mouse to and fires the left click event at the specific point
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 */

	public void leftClick(java.awt.Point point)
	{
		this.click(Hack.LEFT_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), 0, 0);
	}

	/**
	 * Moves mouse to and fires the middle click event at the specific point
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 */

	public void middleClick(java.awt.Point point)
	{
		this.click(Hack.MID_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), 0, 0);
	}

	/**
	 * Moves mouse to and fires the right click event at the specific point
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 */

	public void rightClick(java.awt.Point point)
	{
		this.click(Hack.RIGHT_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), 0, 0);
	}

	/**
	 * Moves mouse to and fires the left click event at the specific point, with specified length and wait duration
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 * @param delay The amount of time to click and hold the mouse button for, in milliseconds (int)
	 * @param wait	The amount of time to wait after clicking, in milliseconds (int)
	 */

	public void leftClick(java.awt.Point point, int delay, int wait)
	{
		this.click(Hack.LEFT_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), delay, wait);
	}

	/**
	 * Moves mouse to and fires the middle click event at the specific point, with specified length and wait duration
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 * @param delay The amount of time to click and hold the mouse button for, in milliseconds (int)
	 * @param wait	The amount of time to wait after clicking, in milliseconds (int)
	 */

	public void middleClick(java.awt.Point point, int delay, int wait)
	{
		this.click(Hack.MID_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), delay, wait);
	}

	/**
	 * Moves mouse to and fires the right click event at the specific point, with specified length and wait duration
	 * @param point The point to move to and click at (<code>java.awt.Point</code>)
	 * @param delay The amount of time to click and hold the mouse button for, in milliseconds (int)
	 * @param wait	The amount of time to wait after clicking, in milliseconds (int)
	 */

	public void rightClick(java.awt.Point point, int delay, int wait)
	{
		this.click(Hack.RIGHT_MOUSE_BUTTON, (int) point.getX(), (int) point.getY(), delay, wait);
	}

	private void click(int button, int x, int y, int delay, int wait)
	{
		if (!(button == Hack.LEFT_MOUSE_BUTTON || button == Hack.MID_MOUSE_BUTTON || button == Hack.RIGHT_MOUSE_BUTTON)) return;
		if (!(x >= 0 && y >= 0 && x <= this.getScreenWidth() && y <= this.getScreenHeight())) return;
		if (!(delay >= 0 && wait >= 0)) return;

		this.robot.mouseMove(x,y);
		this.robot.delay(Hack.DEFAULT_MOUSE_ACTION_DELAY);

		this.robot.mousePress(button);
		this.robot.delay(Hack.DEFAULT_MOUSE_ACTION_DELAY);
		this.robot.delay(delay);

		this.robot.mouseRelease(button);
		this.robot.delay(Hack.DEFAULT_MOUSE_ACTION_DELAY);
		this.robot.delay(wait);
	}

	/**
	 * Built-in logging function that formats and outputs data suitably for this class.
	 * Utilizes the <code>System.out.println()</code> function.
	 * Prints out with no module name and an indentation level of 1.
	 * example: <code>log("Test")</code> => ">>> Test"
	 * @param message The message to print out. (<code>java.lang.String</code>)
	 */

	public void log(String message)
	{
		this.log("", message, 1);
	}

	/**
	 * Built-in logging function that formats and outputs data suitably for this class.
	 * Utilizes the <code>System.out.println()</code> function.
	 * Prints out with specified module name and an indentation level of 1.
	 * example: <code>log("Module", "Test")</code> => ">>> [ Module ] Test"
	 * @param module  The name of the module that calls the function. (<code>java.lang.String</code>)
	 * @param message The message to print out. (<code>java.lang.String</code>)
	 */

	public void log(String module, String message)
	{
		this.log("", message, 1);
	}

	/**
	 * Built-in logging function that formats and outputs data suitably for this class.
	 * Utilizes the <code>System.out.println()</code> function.
	 * Prints out with specified module name and indentation level.
	 * example: <code>log("Module", "Test", 2)</code> => ">>>>>> [ Module ] Test"
	 * @param module  The name of the module that calls the function. (<code>java.lang.String</code>)
	 * @param message The message to print out. (<code>java.lang.String</code>)
	 * @param level	  The indentation level (int)
	 */

	public void log(String module, String message, int level) {
		level = (level > 0) ? level : 1;
		String indentString = "";

		for (int i = 0; i < level; i++) {
			indentString += ">>>";
		}

		String moduleString = (!module.equals("") && module.length() > 0) ? String.format("[ %s ] ", module) : "";
		System.out.println(String.format("%s %s %s", indentString, moduleString, message));
	}

	/**
	 * Gets the screen width
	 * @return The width of the screen (int)
	 */

	public int getScreenWidth()
	{
		return (int) this.getScreenSize().getWidth();
	}

	/**
	 * Gets the screen height
	 * @return The height of the screen (int)
	 */

	public int getScreenHeight()
	{
		return (int) this.getScreenSize().getHeight();
	}

	private java.awt.Dimension getScreenSize()
	{
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	}

	/**
	 * Gets the mouse X position
	 * @return The mouse X position (int)
	 */

	public int getMouseX()
	{
		return (int) this.getMouseLocation().getX();
	}

	/**
	 * Gets the mouse Y position
	 * @return The mouse Y position (int)
	 */

	public int getMouseY()
	{
		return (int) this.getMouseLocation().getY();
	}

	private java.awt.Point getMouseLocation()
	{
		return java.awt.MouseInfo.getPointerInfo().getLocation();
	}

	public class Key
	{
		public static final char UNDEFINED = java.awt.event.KeyEvent.CHAR_UNDEFINED;

		public static final int NUM_0 = java.awt.event.KeyEvent.VK_0;
		public static final int NUM_1 = java.awt.event.KeyEvent.VK_1;
		public static final int NUM_2 = java.awt.event.KeyEvent.VK_2;
		public static final int NUM_3 = java.awt.event.KeyEvent.VK_3;
		public static final int NUM_4 = java.awt.event.KeyEvent.VK_4;
		public static final int NUM_5 = java.awt.event.KeyEvent.VK_5;
		public static final int NUM_6 = java.awt.event.KeyEvent.VK_6;
		public static final int NUM_7 = java.awt.event.KeyEvent.VK_7;
		public static final int NUM_8 = java.awt.event.KeyEvent.VK_8;
		public static final int NUM_9 = java.awt.event.KeyEvent.VK_9;

		public static final int CHR_A = java.awt.event.KeyEvent.VK_A;
		public static final int CHR_B = java.awt.event.KeyEvent.VK_B;
		public static final int CHR_C = java.awt.event.KeyEvent.VK_C;
		public static final int CHR_D = java.awt.event.KeyEvent.VK_D;
		public static final int CHR_E = java.awt.event.KeyEvent.VK_E;
		public static final int CHR_F = java.awt.event.KeyEvent.VK_F;
		public static final int CHR_G = java.awt.event.KeyEvent.VK_G;
		public static final int CHR_H = java.awt.event.KeyEvent.VK_H;
		public static final int CHR_I = java.awt.event.KeyEvent.VK_I;
		public static final int CHR_J = java.awt.event.KeyEvent.VK_J;
		public static final int CHR_K = java.awt.event.KeyEvent.VK_K;
		public static final int CHR_L = java.awt.event.KeyEvent.VK_L;
		public static final int CHR_M = java.awt.event.KeyEvent.VK_M;
		public static final int CHR_N = java.awt.event.KeyEvent.VK_N;
		public static final int CHR_O = java.awt.event.KeyEvent.VK_O;
		public static final int CHR_P = java.awt.event.KeyEvent.VK_P;
		public static final int CHR_Q = java.awt.event.KeyEvent.VK_Q;
		public static final int CHR_R = java.awt.event.KeyEvent.VK_R;
		public static final int CHR_S = java.awt.event.KeyEvent.VK_S;
		public static final int CHR_T = java.awt.event.KeyEvent.VK_T;
		public static final int CHR_U = java.awt.event.KeyEvent.VK_U;
		public static final int CHR_V = java.awt.event.KeyEvent.VK_V;
		public static final int CHR_W = java.awt.event.KeyEvent.VK_W;
		public static final int CHR_X = java.awt.event.KeyEvent.VK_X;
		public static final int CHR_Y = java.awt.event.KeyEvent.VK_Y;
		public static final int CHR_Z = java.awt.event.KeyEvent.VK_Z;

		public static final int KEY_CMD = java.awt.event.KeyEvent.VK_META;
		public static final int KEY_ENTER = java.awt.event.KeyEvent.VK_ENTER;
		public static final int KEY_SHIFT = java.awt.event.KeyEvent.VK_SHIFT;

		public static final int UP = java.awt.event.KeyEvent.VK_UP;
		public static final int DOWN = java.awt.event.KeyEvent.VK_DOWN;
		public static final int LEFT = java.awt.event.KeyEvent.VK_LEFT;
		public static final int RIGHT = java.awt.event.KeyEvent.VK_RIGHT;

		public static final int SPACE = java.awt.event.KeyEvent.VK_SPACE;
	}
}
