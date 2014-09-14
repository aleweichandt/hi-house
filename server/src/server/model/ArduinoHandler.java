package server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.model.devices.Device;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ArduinoHandler implements SerialPortEventListener{
	// Singleton begin
	private static ArduinoHandler sInstance = null;
	private List<byte[]> mMessageList = null;
	
	public static ArduinoHandler getInstance() {
		if(sInstance == null) {
			sInstance = new ArduinoHandler();
		}
		return sInstance;
	}
	// Singleton end
	SerialPort mSerial = null;
	public ArduinoHandler() {
		mMessageList = new ArrayList<byte[]>();
		mSerial = new SerialPort(C.Config.ARDUINO_PORT_NAME); 
        try {
        	mSerial.openPort();//Open port
        	mSerial.setParams(C.Config.ARDUINO_PORT_READ, 8, 1, 0);//Set params
        	//Add SerialPortEventListener
            int mask = SerialPort.MASK_RXCHAR;
            mSerial.setEventsMask(mask);
            mSerial.addEventListener(this);
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		//TODO really check for response and deliver result
		 if(event.isRXCHAR()){//If data is available
             if(event.getEventValue() == 10){//Check bytes count in the input buffer
                 //Read data, if 10 bytes available 
                 try {
                     byte buffer[] = mSerial.readBytes(10);
                 }
                 catch (SerialPortException ex) {
                     System.out.println(ex);
                 }
             }
         }
	}
	
	public void update(int dt) {
		if(!mMessageList.isEmpty()) {
			byte message[] = mMessageList.get(0);
			try {
				if(mSerial.isOpened())
					mSerial.writeBytes(message);
			} catch (SerialPortException e) {
				e.printStackTrace();
			} finally {
				mMessageList.remove(0);
			}
		}
	}
	
	public void addOperation(Device dev, boolean readOperation) {
		int values[] = {0,0,0};
		addOperation(dev, readOperation, values);
	}
	
	public void addOperation(Device dev, boolean readOperation, int[] values) {
		int vtype = dev.getValueType();
		int pins = dev.getPinsAmount();
		
		if(values.length < pins) {
			return; //not enougth values, cant continue
		}
		int msgLen = 1 + (2 * pins) + 1;
		byte buffer[] = new byte[2 + msgLen];
		Arrays.fill(buffer, (byte)0x00);
		//put length
		int id = 0;
		buffer[id++] = (byte)(msgLen & 0xFF);
		buffer[id++] = (byte)((msgLen >> 8) & 0xFF);
		//make header
		byte header = 0;
		header |= (((readOperation?0:1) & 0xFF) << 6);
		header |= (((vtype >> 1) & 0x01) << 5);
		header |= ((vtype & 0x01) << 4);
		header |= (((pins >> 1) & 0x01) << 3);
		header |= ((pins & 0x01) << 2);
		buffer[id++] = header;
		
		for(int i=0;i<pins; i++) {
			buffer[id++] = (byte)(dev.getPin(i) & 0xFF);
			buffer[id++] = (byte)(values[i] & 0xFF);
		}
		mMessageList.add(buffer);
	}
}
