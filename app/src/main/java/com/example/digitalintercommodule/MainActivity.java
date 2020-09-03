package com.example.digitalintercommodule;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        final Button buttonSetup = (Button)findViewById(R.id.ButtonSetup);
		buttonSetup.setOnClickListener(SetupsBtnListener);

		final Button buttonTest = (Button)findViewById(R.id.ButtonTest);
		buttonTest.setOnClickListener(TestBtnListener);
		
        final Button buttonQuit = (Button)findViewById(R.id.ButtonQuit);
		buttonQuit.setOnClickListener(QuitBtnListener);
	}
	
	private View.OnClickListener SetupsBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(MainActivity.this, SerialPortPreferences.class));
		}
	};
	private View.OnClickListener TestBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(MainActivity.this, TestActivity.class));
		}
	};

	private View.OnClickListener QuitBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MainActivity.this.finish();
		}
	};


}




