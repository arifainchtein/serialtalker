
package com.teleonome.serialtalker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.teleonome.framework.tools.SendOneCommandToArduino;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialTalker  {
	private static String buildNumber="";
	//Logger logger;
	String SerialPortID = "/dev/ttyUSB0";
	private static final String PORT_NAMES[] = { "/dev/tty.usbmodem641", "/dev/ttyACM0", "/dev/ttyAMA0", "/dev/ttyACM1","/dev/ttyUSB0","/dev/ttyUSB1","/dev/cu.usbmodem1411" };
	SerialPort serialPort;
	private BufferedReader input;

	private BufferedWriter output;

	private static final int TIME_OUT = 20000;
	private int DATA_RATE = 115200;
	InputStream serialPortInputStream = null;
	OutputStream serialPortOutputStream = null;
	String command="";
	BufferedReader reader=null;
	public SerialTalker() {
		System.out.println("before init");
		init();
		System.out.println("after init");
		String line="";
		try {
			reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output=  new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
			//
			// checking if there is anything in the serial port
			System.out.println("Checking for data in the Serial bus");
			if(reader.ready()) {
				
				do{
					line = reader.readLine();
					System.out.println(line);
				}while(reader.ready() );
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Ready to receive commands ");
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.println("Enter command, q to quit ");
			command = scanner.nextLine();

			System.out.println("command is " + command);
			if(command.equals("q")) {
				scanner.close();
				System.exit(0);
			}else {
				try {
					//command="Ping";
					System.out.println("sending " + command);


					output.write(command,0,command.length());
					//serialPortOutputStream.write( actuatorCommand.getBytes() );
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					output.flush();
					System.out.println("waiting for response ");
					if( command.equals("GetSensorData") ||
							command.equals("GetCommandCode") ||
							command.equals("AsyncData")



							) {

						line = reader.readLine();
						//if(!line.startsWith("Ok") && !line.startsWith("Failure") && !line.startsWith("Fault"))
						System.out.println(line);
					}else if( command.equals("Flush") ) {
						do{
							line = reader.readLine();
							System.out.println(line);
						}while(!line.equals("")  );

						line = reader.readLine();
						//if(!line.startsWith("Ok") && !line.startsWith("Failure") && !line.startsWith("Fault"))
						System.out.println(line);
					}else {
						do{
							line = reader.readLine();
							System.out.println(line);
						}while(!line.contains("Ok") && !line.contains("Failure") && !line.contains("Fault") );


					}


					//					String cleaned="";
					//					if(line.contains("Ok-")) {
					//						cleaned=line.substring(line.indexOf("Ok-"));;
					//					}else if(line.contains("Read fail") && line.contains("#")){
					//						cleaned=line.substring(line.lastIndexOf("fail")+4);
					//					}else {
					//						cleaned=line;
					//					}
					//				    System.out.println("cleaned:  " + cleaned);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
			System.out.println("wating 5sec  using datarate=" + DATA_RATE);
			//Thread.sleep(5000);
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
			System.out.println("opened port , sleeping another 1 sec " );
			Thread.sleep(1000);
			//	Thread.sleep(10000);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.enableReceiveThreshold(1);
			serialPort.enableReceiveTimeout(30000);
			serialPort.enableReceiveThreshold(0);
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams

			//serialPort.addEventListener(this);
			//serialPort.notifyOnDataAvailable(true);

			//serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |  SerialPort.FLOWCONTROL_RTSCTS_OUT);
			serialPort.setDTR(true);

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

	public BufferedReader getReader() throws IOException{
		input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
		return input;
	}



	/*
	public InputStream getReader() throws IOException{
		input = serialPort.getInputStream();
		return input;
	}
	 */

	public BufferedWriter getWriter() throws IOException{
		return  new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
		//return output;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("args.length=" + args.length);
		if(args.length==0) {
			new SerialTalker();
		}else if(args.length==2  && args[0].equals("-c")) {
			String command = args[1];
			SendOneCommandToArduino a = new SendOneCommandToArduino(command, false, null);
			// ****************
		}else if(args.length==3  && args[0].equals("-i")) {
			//
			// this is a file that contains commands
			String fileName = args[1];
			String outputFileName = args[2];
			ArrayList collectedResults;
			ArrayList<String> commands;
			try {
				commands = (ArrayList<String>) FileUtils.readLines(new File(fileName), Charset.defaultCharset());
				System.out.println("sending " + commands.size() + " to be processed");
				SendOneCommandToArduino a = new SendOneCommandToArduino(commands, false, null);
				collectedResults = a.getCommandExecutionResults();

				//				SendOneCommandToArduino a;
				//				Iterator<String> it = commands.iterator();
				//				String line,x;
				//				ArrayList<String> results;
				//				while(it.hasNext()) {
				//					line = (String) it.next();
				//					if(line.startsWith("$Delay")){
				//						int seconds = Integer.parseInt(line.substring(6));
				//						System.out.println("delaying " + seconds + " seconds");
				//						try {
				//							Thread.sleep(seconds*1000);
				//						} catch (InterruptedException e) {
				//							// TODO Auto-generated catch block
				//							e.printStackTrace();
				//						}
				//					}else {
				//						System.out.println("Sending:" + line);
				//						a = new SendOneCommandToArduino(line, false, null);
				//						results = a.getCommandExecutionResults();
				//						for(int i=0;i<results.size();i++) {
				//							x= results.get(i);
				//							System.out.println(x);
				//							collectedResults.append(x + System.lineSeparator());
				//						}
				//					}
				//					
				//				}
				FileUtils.writeLines(new File(outputFileName), collectedResults, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Done." );
			System.exit(0);
			// ****************
		}else if(args.length==3  && args[0].equals("-c") && args[2].equals("-v")) {

			String command = args[1];
			SendOneCommandToArduino a = new SendOneCommandToArduino(command, true, null);
			// ****************
		}else if(args.length==4  && args[0].equals("-c") && args[2].equals("-f")) {

			String command = args[1];
			String fileName = args[3];
			File file = new File(fileName);
			System.out.println("command=" + command + " fileName=" + fileName);
			SendOneCommandToArduino a = new SendOneCommandToArduino(command, false, file);
			// ****************
		}else if(args.length==5  && args[0].equals("-c") && args[2].equals("-f") && args[4].equals("-v")) {

			String command = args[1];
			String fileName = args[3];
			File file = new File(fileName);
			System.out.println("command=" + command + " fileName=" + fileName);
			SendOneCommandToArduino a = new SendOneCommandToArduino(command, true, file);
			// ****************
		}else {
			System.out.println("Bad options");
			System.exit(0);
		}
	}





}
