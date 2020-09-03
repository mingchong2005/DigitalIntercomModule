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

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;


import android.content.SharedPreferences;
import com.example.digitalintercommodule.utils.SerialPort;
import com.example.digitalintercommodule.utils.SerialPortFinder;
import android.util.Log;


public class SerialPortApplication extends android.app.Application {
	private static final String TAG = "SerialPort";

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		
		Log.i(TAG, "SerialPortApplication ---> getAllDevicesPath");
		
		if (mSerialPort == null) {
			//package="android.serialport.sample"
			/* Read serial port parameters */
			//set the serial port parameter in the file SerialPortPreferences.java
			SharedPreferences sp = getSharedPreferences("com.example.serialporttest_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
			//if ( (path.length() == 0) || (baudrate == -1)) {
			//	throw new InvalidParameterException();
			//}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		
		Log.i(TAG, "SerialPortApplication ---> closeSerialPort");
		if (mSerialPort != null) {
			mSerialPort.close_interphone_module();
			mSerialPort = null;
		}
	}
}
