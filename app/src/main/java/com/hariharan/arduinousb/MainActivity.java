package com.hariharan.arduinousb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    Button startButton, sendButton, clearButton, stopButton;

    TextView logTextView;
    EditText logEditText;

    TextView speedTextView;
    TextView turnTextView;

    EditText durationEditText;

    Button directionButton, leftButton, rightButton;

    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    private int speedNumber = 0;
    private int turnNumber = 0;
    private int directionNumber = 0; // 0 = forward 2 = backward
    private int durationNumber = 1000;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(logTextView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(logTextView,"Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }

        ;
    };

    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);

        seekBar = (SeekBar)findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                speedTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

    logEditText = (EditText) findViewById(R.id.editTextLog);
        logTextView = (TextView) findViewById(R.id.textViewLog);

        speedTextView = (TextView) findViewById(R.id.speedTextView);
        turnTextView = (TextView) findViewById(R.id.turnTextView);

        directionButton = (Button) findViewById(R.id.buttonDirection);

        leftButton = (Button) findViewById(R.id.buttonLeftTurn);
        rightButton = (Button) findViewById(R.id.buttonRightTurn);

        durationEditText = (EditText) findViewById(R.id.editTextDuration);

        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        // Sets the default Speed Value Text
        speedTextView.setText(Integer.toString(speedNumber));
        updateTurnUItext();
        durationEditText.setText(Integer.toString(durationNumber));
    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        logTextView.setEnabled(bool);

        // Vehicle control buttons
//        directionButton.setEnabled(bool);
//        backwardButton.setEnabled(bool);
//        forewardButton.setEnabled(bool);
//        rightButton.setEnabled(bool);
//        leftButton.setEnabled(bool);
//        durationEditText.setEnabled(bool);
    }

    public void onClickStart(View view) {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    public void onClickSend(View view) {

        speedNumber = seekBar.getProgress();

        String data =
//                logEditText.getText().toString() +
                speedNumber+
                ":" +
                directionNumber +
                "&" +
                turnNumber +
                ":" +
                durationNumber; // Duration
        serialPort.write(data.getBytes());
        tvAppend(logTextView, "\nData Sent : " + data + "\n");

        Toast.makeText(this, "Sent: " + data,
                Toast.LENGTH_LONG).show();
    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(logTextView,"\nSerial Connection Closed! \n");

    }

    public void onClickClear(View view) {
        logTextView.setText(" ");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

    // Vehicle Control Code

    public void onClickDirection(View view) {
        if (directionNumber == 0){
            directionNumber = 1;
            directionButton.setText("Backward");

        } else {
            directionNumber = 0;
            directionButton.setText("Forward");
        }
    }

    public void onClickLeft(View view) {
        if (turnNumber > 0){
            turnNumber -= 1;
        }
        updateTurnUItext();
    }

    public void onClickRight(View view) {
        if(turnNumber < 2){
            turnNumber += 1;
        }
        updateTurnUItext();
    }

    private void updateTurnUItext(){
        if (turnNumber == 0){
            turnTextView.setText("Forward");
        } else if (turnNumber == 1) {
            turnTextView.setText("Left");
        } else {
            turnTextView.setText("Right");
        }
    }

    public void onClickUpdateDuration (View view){

        if (Integer.parseInt(durationEditText.getText().toString()) < 10000){
            durationNumber = Integer.parseInt(durationEditText.getText().toString());
        }
    }
}
