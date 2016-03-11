/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * A simple platform utilities class (determine OS to determine chrome binary location)
 */

package com.kansal.cutter;

class PlatformUtils {
	private static enum OSType {
		WINDOWS, MAC, LINUX, UNDETERMINED;
	}

	private static OSType osType = OSType.UNDETERMINED;

	public static boolean isWindows() {
		if (osType == OSType.UNDETERMINED) determineOS();
		return osType == OSType.WINDOWS;
	}

	public static boolean isLinux() {
		if (osType == OSType.UNDETERMINED) determineOS();
		return osType == OSType.LINUX;
	}

	public static boolean isMac() {
		if (osType == OSType.UNDETERMINED) determineOS();
		return osType == OSType.MAC;
	}

	private static void determineOS() {
		String prop = System.getProperty("os.name");
		if (prop.toLowerCase().contains("Windows".toLowerCase())) osType = OSType.WINDOWS;
		if (prop.toLowerCase().contains("Linux".toLowerCase())) osType = OSType.LINUX;
		if (prop.toLowerCase().contains("Mac".toLowerCase())) osType = OSType.MAC;
	}
}
