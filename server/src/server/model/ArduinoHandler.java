package server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.model.devices.Device;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ArduinoHandler {
	// Singleton begin
	private static ArduinoHandler sInstance = null;
	private List<byte[]> mMessageList = null;
	private List<Device> mCallbackList = null;
	private int mReadLen = 0;
	
	public static ArduinoHandler getInstance() {
		if(sInstance == null) {
			sInstance = new ArduinoHandler();
		}
		return sInstance;
	}
	// Singleton end
	SerialPort mSerial = null;
	public ArduinoHandler() {
		mReadLen = 0;
		mMessageList = new ArrayList<byte[]>();
		mCallbackList = new ArrayList<Device>();
		mSerial = new SerialPort(C.Config.ARDUINO_PORT_NAME); 
        try {
        	mSerial.openPort();//Open port
        	mSerial.setParams(C.Config.ARDUINO_PORT_READ, 8, 1, 0);//Set params
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
	}
	
	public void serialRead() {
		try {
			if(mSerial.getInputBufferBytesCount() >= 2 || mReadLen > 0) {
				//Read data, if 2 bytes available 
			    byte lenMsg[] = mSerial.readBytes(2);
			    mReadLen = (lenMsg[1] << 8) + lenMsg[0];
		        if(mSerial.getInputBufferBytesCount() >= mReadLen) {
			        byte msg[] = mSerial.readBytes(mReadLen);
			        mReadLen = 0;
			        execResponse(msg);
		        }
			}
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	public void serialWrite() {
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
	
	public void update(int dt) {
		serialWrite();
		serialRead();
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
		mCallbackList.add(dev);
		dev.lock();
	}
	
	void execResponse(byte[] msg) {
		byte header = msg[0];
		int pins = (int)((header>>2) & 0x03);
		int values[] = new int[pins];
		for(int i=0;i<pins; i++) {
			values[i] = (int)(msg[(i+1)*2] & 0xFF);
		}
		Device caller = mCallbackList.get(0);
		mCallbackList.remove(0);
		caller.onOperationResponse(values);
		caller.unlock();
	}
}
