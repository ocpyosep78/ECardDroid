package com.cover.ecardroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CreateCard extends Activity implements OnItemClickListener {

	private ListView listCard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card);
		listCard = (ListView) findViewById(R.id.listCard);
		listCard.setTextFilterEnabled(true);
		String[] cardName = { "Birthday Cards", "Graduation Cards",
				"Anniversary Cards", "Congratulation Cards", "Sympathy Cards",
				"Custom Cards" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				cardName);

		listCard.setAdapter(adapter);
		listCard.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		TextView getSelect = (TextView) v;
		if (getSelect.getText().equals("Birthday Cards")) {
			sendCardId("Birthday");
		} else if (getSelect.getText().equals("Graduation Cards")) {
			sendCardId("Graduation");
		} else if (getSelect.getText().equals("Anniversary Cards")) {
			sendCardId("Anniversary");
		} else if (getSelect.getText().equals("Congratulation Cards")) {
			sendCardId("Congratulation");
		} else if (getSelect.getText().equals("Sympathy Cards")) {
			sendCardId("Sympathy");
		} else {
			sendCardId("Customs");
		}
	}

	void sendCardId(String id) {
		Intent intent = new Intent(getApplicationContext(), Editor.class);
		intent.putExtra("typeCard", id);
		startActivityForResult(intent, 0);
	}
}
