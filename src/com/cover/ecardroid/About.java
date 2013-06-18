package com.cover.ecardroid;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity {
	WebView aboutView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		aboutView = (WebView) findViewById(R.id.webViewAbout);
		String about = "<html>"
				+ "<body>"
				+ "<strong>eCardDroid</strong> is a quick & easy way to send eCards using your Android device."
				+ "Share your eCards with friends and family with email, MMS, Facebook, Twitter, Bluetooth, Google Docs, Picasa, and other methods depending on your device installed apps and Android version."
				+ "<p>Copyright 2012 &copy; Cover</p>"
				+ "<p><a href='twitter.com/vinswilliam'>Vinsensius William 672009038</a></p>"
				+ "<p><a href='twitter.com/pratyaksa_ocsa'>Pratyaksa Ocsa N.S 672009143</a></p>"
				+ "<p><a href='twitter.com/and2erlangga'>Andreanus Erlangga S. 672009193</a></p>"
				+ "</body>" + "</html>";
		aboutView.loadData(about, "text/html", null);
	}
}
