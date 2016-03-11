/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class /would/ control access to the program and verify the user's identity.
 * It has been left out for security reasons.
 */

package com.kansal.cutter;

import java.io.*;
import java.nio.file.*;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.DatatypeConverter;

public class Security {
    private static final String VALIDATION_URL = ""; // there would be a server URL here
    private static final String KEY_FILE = ""; //there would be a key location here
    private static final String ALGORITHM = ""; // there would be an algorithm here
    private static final byte[] KEY = new byte[]{}; // there would be a key here
    private static String getHardwareAddress() {
        // There would be an actual implementation here
        return "";
    }

    public static String getHardwareID() {
        // There would be an actual implementation here
        return "";
    }

    private static String encrypt(String data) {
        // there would be an actual implementation here
        return "";
    }

    private static String decrypt(String data) {
        // there would be an actual implementation here
        return "";
    }

    private static String readStoredKey() {
        // there would be an actual implementation here
        return "";
    }

    public static void storeKey(String key) {
        // there would be an actual implementation here
    }

    public static boolean validateMachineAccess(String key) {
        // there would be an actual implementation here
		return false;
    }

    private static String bytesToString(byte[] bytes) {
        // there would be an actual implementation here
        return "";
    }
}
