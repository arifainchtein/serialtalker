package com.teleonome.serialtalker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class OneCommandExecution {

	private static String buildNumber="";
	//Logger logger;
	String SerialPortID = "/dev/ttyUSB0";
	private static final String PORT_NAMES[] = { "/dev/tty.usbmodem641", "/dev/ttyACM0", "/dev/ttyAMA0", "/dev/ttyACM1","/dev/ttyUSB0","/dev/cu.usbmodem1411" };
	SerialPort serialPort;
	private BufferedReader input;
	
	private BufferedWriter output;

	private static final int TIME_OUT = 20000;
	private int DATA_RATE = 9600;
	InputStream serialPortInputStream = null;
	OutputStream serialPortOutputStream = null;
	public OneCommandExecution(String command) {
		// TODO Auto-generated constructor stub
		
		init();
		
		BufferedWriter oneCommandOutput=null;
		BufferedReader reader=null;
		// **************
		try {
			oneCommandOutput=  new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
				try {
					System.out.println("sending " + command);			
					oneCommandOutput.write(command,0,command.length());
					//serialPortOutputStream.write( actuatorCommand.getBytes() );
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oneCommandOutput.flush();
					System.out.println("waiting for response ");
					
					String line = reader.readLine();
					System.out.println("the response is:   " + line);
					String cleaned="";
					if(line.contains("Ok-")) {
						cleaned=line.substring(line.indexOf("Ok-"));;
					}else if(line.contains("Read fail") && line.contains("#")){
						cleaned=line.substring(line.lastIndexOf("fail")+4);
					}else {
						cleaned=line;
					}
				    System.out.println("cleaned:  " + cleaned);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	

public void init() {
	// TODO Auto-generated method stub
	Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

	CommPortIdentifier portId = null;
	
	CommPortIdentifier currPortId=null;
	while (portId == null && portEnum.hasMoreElements()) {
		currPortId = (CommPortIdentifier) portEnum.nextElement();
		//System.out.println("currPortId=" + currPortId.getName());
		System.out.println("looking for ports, currPortId=" + currPortId);

		for (String portName : PORT_NAMES) {
			if ( currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName) ){
				// Try to connect to the Arduino on this port
				portId = currPortId;
				break;
			}
		}
	}
	if (portId == null) {
		System.out.println("Could not find COM port.");
		System.exit(0);
		
	}
	System.out.println("Found COM Port.");
	try {
		//
		// get the data rate for the arduno ie get the DeneWord , get the dene that represents the arduino
		System.out.println("using datarate=" + DATA_RATE);
		serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
		serialPort.disableReceiveTimeout();
		serialPort.enableReceiveThreshold(1);
		serialPort.setSerialPortParams(DATA_RATE,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		// open the streams

		//serialPort.addEventListener(this);
		//serialPort.notifyOnDataAvailable(true);

		///serialPort..write().write(InetAddress.getLocalHost().toString().t());
		serialPortInputStream = serialPort.getInputStream();
		serialPortOutputStream = serialPort.getOutputStream();

		if (serialPortInputStream == null) {
			System.out.println("serialPortInputStream is null.");
		}

		if (serialPortOutputStream == null) {
			System.out.println("serialPortOutputStream is null.");
			
	}

		


		//
		// SKIP configure pins
		//int sensorNumberOfReadsPerPulse = 10;
		// output.write(longToBytes(sensorNumberOfReadsPerPulse));


		System.out.println("finished initializing" );

	} catch (Exception e) {

		// TODO Auto-generated catch block
		StringWriter sw = new StringWriter();
		e.printStackTrace( new PrintWriter( sw )    );
		String callStack = sw.toString();
		System.out.println(callStack);
		
	}
}
}
