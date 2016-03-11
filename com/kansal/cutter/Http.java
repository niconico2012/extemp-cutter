/* Copyright (C) 2015 Nikhil Kansal - All Rights Reserved
 * This source code has only been provided as a proof of concept.
 * It is forbidden to compile an executable including any of this
 * code without express permission, nor it is permissible to modify
 * this code in any way for any reason.
 */

/*
 * This class controls all HTTP requests to extract article information.
 * It can be used as a general purpose class. I wrote it over 4 years ago.
 * This class was made incomplete for commit.
 */

package com.kansal.cutter;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

public class Http {
	static {
	    TrustManager[] trustAllCertificates = new TrustManager[] {
	        new X509TrustManager() {
	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                return null; // Not relevant.
	            }
	            @Override
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	            @Override
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                // Do nothing. Just allow them all.
	            }
	        }
	    };

	    HostnameVerifier trustAllHostnames = new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true; // Just allow them all.
	        }
	    };

	    try {
	        System.setProperty("jsse.enableSNIExtension", "false");
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCertificates, new SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
	    }
	    catch (GeneralSecurityException e) {
	        throw new ExceptionInInitializerError(e);
	    }
	}

	public final static String NO_INTERNET = "NO_INTERNET";
	public final static String NO_RESPONSE = "NO_RESPONSE";
	private String error = null;
	private String url = null;
	private String response = "";
	private String params[];
	private Map<String, String> cookies;
	private int tries = 0;

	public Http(String urlin, String[] args){
		this.url = urlin;
		this.params = args;
	}

	public Http(String urlin){
		this.url = urlin;
	}

	public Http(){

	}

	public Http url(String urlin) {
		this.url = urlin;
		return this;
	}

	public Http params(String[] args) {
		this.params = args;
		return this;
	}

	public Http clearResponse() {
		this.response = "";
		return this;
	}

	public Http post(){
		try {
			Connection con = Jsoup.connect(this.url);
			if (params != null){
				for (int i = 0; i < params.length; i += 2){
					con.data(params[i], params[i+1]);
				}
			}

			if (cookies != null) {
				for (Entry<String, String> cookie : cookies.entrySet()) {
				    con.cookie(cookie.getKey(), cookie.getValue());
				}
			}

			con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_4) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.21 Safari/536.11");
			Response res = con.method(Method.POST).execute();
			Document document = res.parse();

			this.response = document.toString();
		} catch (Exception e){
			this.response = Http.NO_RESPONSE;
		}

		return this;
	}

	public Http get(){
		try {
			Connection con = Jsoup.connect(this.url);
			con.timeout(20000);
			con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_4) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.21 Safari/536.11");
			con.header("Connection", "keep-alive");

			Response res = con.method(Method.GET).execute();
			Document document = res.parse();
			cookies = res.cookies();

			this.response = document.toString();
		} catch (Exception e){
			if ((this.tries++) < 4) {
				return this.get();
			}
		}

		return this;
	}

	public String response() {
		if (this.response != null && this.response != "") return this.response;
		return Http.NO_RESPONSE;
	}

	public String error(){
		return this.error;
	}
}
