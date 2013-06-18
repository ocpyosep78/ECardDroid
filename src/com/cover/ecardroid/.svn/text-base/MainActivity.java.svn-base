package com.cover.ecardroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	private ImageButton bCreate;
	private ImageButton bAbout;
	private ImageButton bExit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bCreate = (ImageButton) findViewById(R.id.ButtonCreate);
		bAbout = (ImageButton) findViewById(R.id.ButtonAbout);
		bExit = (ImageButton) findViewById(R.id.ButtonExit);

		bCreate.setOnClickListener(this);
		bAbout.setOnClickListener(this);
		bExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == bCreate) {
			Intent intent = new Intent(this, CreateCard.class);
			startActivity(intent);
		} else if (v == bAbout) {
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
		} else {
			Keluar();
		}
	}

	@Override
	public void onBackPressed() {
		Keluar();
	}

	public void Keluar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}