package com.example.controleremotolejos;

import android.app.*;
import android.os.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.*;
import android.bluetooth.*;
import android.view.*;
import android.view.View.OnClickListener;

import java.io.*;
import java.util.*;



public class MainActivity extends Activity implements OnSeekBarChangeListener {

    public static final int MENU_ABOUT = Menu.FIRST;
    public static final int MENU_QUIT = Menu.FIRST + 1;
    
    private TextView myNXT;
    private BluetoothSocket nxtBTsocket = null;
    private DataOutputStream nxtDos = null;
    
    Button botao_conectar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		VerticalSeekBar velocidade = (VerticalSeekBar)findViewById(R.id.seekBar_velocidade);
		velocidade.setMax(2000);
		velocidade.setProgress(1000);
		velocidade.setOnSeekBarChangeListener(this);
		
		SeekBar motor = (SeekBar)findViewById(R.id.seekBar_motor);
		motor.setMax(90);
		motor.setProgress(45);
		motor.setOnSeekBarChangeListener(this);
	}
	 
	public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
		if(seekBar == (SeekBar)findViewById(R.id.seekBar_velocidade)) {
			TextView tv = (TextView)findViewById(R.id.textView_velocidade2);
			int velo = (progress-1000)/10;
			tv.setText("Velocidade: " + velo + "m/s");
		}
		if(seekBar == (SeekBar)findViewById(R.id.seekBar_motor)) {
			TextView tv = (TextView)findViewById(R.id.textView_motor2);
			int velo = progress-45;
			tv.setText("Rotação motor: " + velo + "º");
		}
	}
	
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
	
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ABOUT, 0, "Sobre").setIcon(R.drawable.menu_info_icon);
        menu.add(0, MENU_QUIT, 0, "Fechar").setIcon(R.drawable.menu_quit_icon);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ABOUT:
                showAboutDialog();
                return true;         
            case MENU_QUIT:
               finish(); 
               return true;
        }
        return false;
    }
    
	private void showAboutDialog() 
    {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.aboutbox);
        dialog.show();
    }
	
	public void ButtonOnClick(View v) {
		switch (v.getId()) {
			case R.id.button_conectar:
				createNXTConnection();	
				break;
		}
	}
	
    private void createNXTConnection() {
        try {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
            BluetoothDevice nxtDevice = null;
         
            for (BluetoothDevice bluetoothDevice : bondedDevices)
            {
                if (bluetoothDevice.getName().equals(myNXT.getText().toString())) {
                    nxtDevice = bluetoothDevice;
                    break;
                }
            } 

            if (nxtDevice == null)
            {
                Toast toast = Toast.makeText(this, "Nenhum dispositivo foi pareado!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }             

            //nxtBTsocket = nxtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); NXT
            nxtBTsocket = nxtDevice.createRfcommSocketToServiceRecord(UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
            nxtBTsocket.connect();
            nxtDos = new DataOutputStream(nxtBTsocket.getOutputStream());
            Toast toast = Toast.makeText(this, "Conectado!", Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Problema em criar conexão com " + myNXT.getText().toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void destroyNXTConnection() {
        try {
            if (nxtBTsocket != null) {
                // send one close message 
                sendNXTcommand(1001,0);
                nxtBTsocket.close();
                nxtBTsocket = null;
            }
            nxtDos = null;            
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Problem at closing the connection", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sendNXTcommand(int command, int value) {
        if (nxtDos == null) {
            return;
        }

        try {
            nxtDos.writeInt(command);
            nxtDos.writeInt(value);
            nxtDos.flush();
        } catch (IOException ioe) { 
            Toast toast = Toast.makeText(this, "Problem at writing command", Toast.LENGTH_SHORT);
            toast.show();            
        }
    }
}