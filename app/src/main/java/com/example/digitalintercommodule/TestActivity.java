/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.example.digitalintercommodule;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;
import android.widget.Toast;

public class TestActivity extends SerialPortActivity {
	private static final String TAG = "SerialPort";
	EditText mReception;
	String Result = "+DMOCONNECT";
	static EditText mInputText;

	private KeyReceiver keyreceiver;
	static final String PTT_DOWN_ACTION = "com.android.PTT.ACTION_DOWN";
	static final String PTT_UP_ACTION = "com.android.PTT.ACTION_UP";
	static final String CUSTOM_DOWN_ACTION = "android.action.CUSTOM_PRESSED";
	static final String CUSTOM_LONGPRESS_ACTION = "android.action.LONG_CUSTOM_PRESSED";
	static final String CAMERA_DOWN_ACTION = "android.action.CAMERA_PRESSED";
	static final String CAMERA_LONGPRESS_ACTION = "android.action.LONG_CAMERA_PRESSED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		mReception = (EditText) findViewById(R.id.EditTextReceive);

		Log.i(TAG, "TestActivity ---> onCreate");
		
		Button firewareversionBtn = (Button)findViewById(R.id.firewareversion);
		firewareversionBtn.setOnClickListener(firewareversionBtnListener);

		Button writeBtn = (Button)findViewById(R.id.write);
		writeBtn.setOnClickListener(writeBtnListener);

		Button DigitalIntercomSetBtn = (Button)findViewById(R.id.DigitalIntercomSet);
		DigitalIntercomSetBtn.setOnClickListener(DigitalIntercomSetBtnBtnListener);


		Button DigitalIntercomSwitchBtn = (Button)findViewById(R.id.DigitalIntercomSwitch);
		DigitalIntercomSwitchBtn.setOnClickListener(DigitalIntercomSwitchBtnListener);

		Button StartBtn = (Button)findViewById(R.id.Start);
		StartBtn.setOnClickListener(StartBtnListener);

		mInputText = (EditText) findViewById(R.id.EditTextInput);
		//mInputText.requestFocus();

		keyreceiver = new KeyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PTT_DOWN_ACTION);
		intentFilter.addAction(PTT_UP_ACTION);
		intentFilter.addAction(CUSTOM_DOWN_ACTION);
		intentFilter.addAction(CUSTOM_LONGPRESS_ACTION);
		intentFilter.addAction(CAMERA_DOWN_ACTION);
		intentFilter.addAction(CAMERA_LONGPRESS_ACTION);
		registerReceiver(keyreceiver, intentFilter);

	}

	private View.OnClickListener firewareversionBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i;	
			int[] buffer = {0x68, 0x25, 0x01, 0x01, 0x95, 0xC8, 0x00, 0x01, 0x01, 0x10};
            mSerialPort.speaker_power_on(1);
				try {
					for(i = 0; i < buffer.length; i++) {
						Log.e(TAG, "buffer = " + buffer[i]);
						mOutputStream.write((byte)buffer[i]);
					}
					//mOutputStream.write('0x0d');
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
	};

	private View.OnClickListener writeBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i;	
			String ConvertString = null;
			CharSequence editText = mInputText.getText();

			if(editText == null || editText.length() == 0){
				mReception.setText("数据为空，写入失败");
			}
			else{
				ConvertString = editText.toString();
				Log.e(TAG, "ConvertString = " + ConvertString);

				try {
					mOutputStream.write(new String(ConvertString).getBytes());
					//mOutputStream.write('0x0d');
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
		}
	};

	private View.OnClickListener DigitalIntercomSetBtnBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i;
			int[] buffer = {0x68, 0x2E, 0x01, 0x01, 0x4F, 0xA2, 0x00, 0x13,
							0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x48, 0x6C,
							0xD9, 0x17, 0x48, 0x6C, 0xD9, 0x17, 0x01, 0x01,
							0x00, 0x00, 0x00, 0x10 };

				try {
					for(i = 0; i < buffer.length; i++) {
						Log.e(TAG, "buffer = " + buffer[i]);
						mOutputStream.write((byte)buffer[i]);
					}					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//mOutputStream.write('0x0d');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	};


	private View.OnClickListener DigitalIntercomSwitchBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i;
			int[] buffer = {0x68, 0x01, 0x01, 0x01, 0x95, 0xEC, 0x00, 0x01, 0x01, 0x10 };

			try {
				for(i = 0; i < buffer.length; i++) {
					Log.e(TAG, "buffer = " + buffer[i]);
					mOutputStream.write((byte)buffer[i]);
				}					try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//mOutputStream.write('0x0d');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private View.OnClickListener StartBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i;
			int[] buffer = {0x68, 0x06, 0x01, 0x01, 0x96, 0xE3, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10};

			//int[] buffer = {0x68, 0x25, 0x01, 0x01, 0x95, 0xC8, 0x00, 0x01, 0x01, 0x10};
            mSerialPort.speaker_power_on(1);
			try {
				for(i = 0; i < buffer.length; i++) {
					Log.e(TAG, "buffer = " + buffer[i]);
					mOutputStream.write((byte)buffer[i]);
				}					try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//mOutputStream.write('0x0d');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static String hexStringToString(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}



	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		Log.i(TAG, "TestActivity ---> onDataReceived");
		runOnUiThread(new Runnable() {
			public void run() {
					//String strReceive = convertHexToString(byteToHexString(buffer,size));
					//Log.i(TAG, "TestActivity ---> onDataReceived" + strReceive);
					//JudgeResult(strReceive);
				String HexString = bytesToHexString(buffer);
				Log.e("chw", "HexString.getBytes() = " + HexString);
				Log.e("chw", "HexString length =" + HexString.length());


					if (mReception != null) {  
                        mReception.append(HexString);
                        }  
			}
		});
	}


	private void start_transmit( ){
		int i;
		int[] buffer = {0x68, 0x06, 0x01, 0x01, 0x96, 0xE3, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10};
		//int[] buffer = {0x68, 0x25, 0x01, 0x01, 0x95, 0xC8, 0x00, 0x01, 0x01, 0x10};

		try {
			for(i = 0; i < buffer.length; i++) {
				Log.e(TAG, "buffer = " + buffer[i]);
				mOutputStream.write((byte)buffer[i]);
			}					try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//mOutputStream.write('0x0d');
		} catch (IOException e) {
			e.printStackTrace();
		}
	};


	private void stop_transmit( ){
		int i;
		//int[] buffer = {0x68, 0x06, 0x01, 0x01, 0x96, 0xE3, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10};
		int[] buffer = {0x68, 0x06, 0x01, 0xFF, 0x95, 0xE5, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10};

		try {
			for(i = 0; i < buffer.length; i++) {
				Log.e(TAG, "buffer = " + buffer[i]);
				mOutputStream.write((byte)buffer[i]);
			}					try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//mOutputStream.write('0x0d');
		} catch (IOException e) {
			e.printStackTrace();
		}
	};


	private class KeyReceiver extends BroadcastReceiver {

		private static final String TAG = "ControlDemo";

		static final String PTT_DOWN_ACTION = "com.android.PTT.ACTION_DOWN";
		static final String PTT_LONGPRESS_ACTION = "com.android.PTT.ACTION_UP";
		static final String CUSTOM_DOWN_ACTION = "android.action.CUSTOM_PRESSED";
		static final String CUSTOM_LONGPRESS_ACTION = "android.action.LONG_CUSTOM_PRESSED";
		static final String CAMERA_DOWN_ACTION = "android.action.CAMERA_PRESSED";
		static final String CAMERA_LONGPRESS_ACTION = "android.action.LONG_CAMERA_PRESSED";

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.w(TAG, " onReceive");
			String action = intent.getAction();

			if (action != null && action.equals(PTT_DOWN_ACTION)) {
				Log.w(TAG, "PTT_DOWN_ACTION onReceive");
				start_transmit();
			} else if (action != null && action.equals(PTT_UP_ACTION)) {
				Log.w(TAG, "PTT_UP_ACTION onReceive");
				stop_transmit();
			} else if (action != null && action.equals(CUSTOM_DOWN_ACTION)) {
				Log.w(TAG, "CUSTOM_DOWN_ACTION onReceive");
				Toast.makeText(context, "CUSTOM_DOWN_ACTION", Toast.LENGTH_SHORT).show();
			} else if (action != null && action.equals(CUSTOM_LONGPRESS_ACTION)) {
				Log.w(TAG, "CUSTOM_LONGPRESS_ACTION onReceive");
				Toast.makeText(context, "CUSTOM_LONGPRESS_ACTION", Toast.LENGTH_SHORT).show();
			} else if (action != null && action.equals(CAMERA_DOWN_ACTION)) {
				Log.w(TAG, "CAMERA_DOWN_ACTION onReceive");
				Toast.makeText(context, "CAMERA_DOWN_ACTION", Toast.LENGTH_SHORT).show();
			} else if (action != null && action.equals(CAMERA_LONGPRESS_ACTION)) {
				Log.w(TAG, "CAMERA_LONGPRESS_ACTION onReceive");
				Toast.makeText(context, "CAMERA_LONGPRESS_ACTION", Toast.LENGTH_SHORT).show();
			}
		}
	}




}

class KeyReceiver extends BroadcastReceiver {

	private static final String TAG = "ControlDemo";

	static final String SOS_DOWN_ACTION = "android.action.SOS_PRESSED";
	static final String SOS_LONGPRESS_ACTION = "android.action.LONG_SOS_PRESSED";
	static final String CUSTOM_DOWN_ACTION = "android.action.CUSTOM_PRESSED";
	static final String CUSTOM_LONGPRESS_ACTION = "android.action.LONG_CUSTOM_PRESSED";
	static final String CAMERA_DOWN_ACTION = "android.action.CAMERA_PRESSED";
	static final String CAMERA_LONGPRESS_ACTION = "android.action.LONG_CAMERA_PRESSED";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.w(TAG, " onReceive");
		String action = intent.getAction();

		if (action != null && action.equals(SOS_DOWN_ACTION)){
			Log.w(TAG, "SOS_DOWN_ACTION onReceive");
			Toast.makeText(context,"SOS_DOWN_ACTION",Toast.LENGTH_SHORT).show();
		}else if (action != null && action.equals(SOS_LONGPRESS_ACTION)){
			Log.w(TAG, "SOS_LONGPRESS_ACTION onReceive");
			Toast.makeText(context,"SOS_LONGPRESS_ACTION",Toast.LENGTH_SHORT).show();
		}else if (action != null && action.equals(CUSTOM_DOWN_ACTION)){
			Log.w(TAG, "CUSTOM_DOWN_ACTION onReceive");
			Toast.makeText(context,"CUSTOM_DOWN_ACTION",Toast.LENGTH_SHORT).show();
		}else if (action != null && action.equals(CUSTOM_LONGPRESS_ACTION)){
			Log.w(TAG, "CUSTOM_LONGPRESS_ACTION onReceive");
			Toast.makeText(context,"CUSTOM_LONGPRESS_ACTION",Toast.LENGTH_SHORT).show();
		}else if (action != null && action.equals(CAMERA_DOWN_ACTION)){
			Log.w(TAG, "CAMERA_DOWN_ACTION onReceive");
			Toast.makeText(context,"CAMERA_DOWN_ACTION",Toast.LENGTH_SHORT).show();
		}else if (action != null && action.equals(CAMERA_LONGPRESS_ACTION)){
			Log.w(TAG, "CAMERA_LONGPRESS_ACTION onReceive");
			Toast.makeText(context,"CAMERA_LONGPRESS_ACTION",Toast.LENGTH_SHORT).show();
		}
	}

}