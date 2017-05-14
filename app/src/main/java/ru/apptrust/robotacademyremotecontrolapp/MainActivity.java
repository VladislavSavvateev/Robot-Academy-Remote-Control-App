package ru.apptrust.robotacademyremotecontrolapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothSocket socket;
    InputStream is;
    OutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_connect:
                final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                if (!ba.isEnabled()) {
                    Toast.makeText(this, "Пожалуйста, включите Bluetooth!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(i);
                    break;
                }
                ba.startDiscovery();
                final LayoutInflater li = LayoutInflater.from(this);
                View v = li.inflate(R.layout.connect_layout, null);
                final LinearLayout lv = (LinearLayout) v.findViewById(R.id.list_devices);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setView(v);
                adb.setTitle("Выберите устройство");
                final AlertDialog dialog = adb.create();
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (BluetoothDevice bd: ba.getBondedDevices()) {
                            final View item_view = li.inflate(R.layout.bluetooth_item, null);
                            item_view.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    dialog.hide();
                                    final BluetoothDevice device = (BluetoothDevice) v.getTag();
                                    AlertDialog.Builder pdb = new AlertDialog.Builder(MainActivity.this);
                                    pdb.setTitle("Соединяемся...");
                                    pdb.setMessage("Пожалуйста, подождите...");
                                    final AlertDialog pd = pdb.create();
                                    pd.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (socket != null) {
                                                    socket.close();
                                                    is.close();
                                                    os.close();
                                                    socket = null;
                                                    is = null;
                                                    os = null;
                                                }
                                                ba.cancelDiscovery();
                                                BluetoothSocket bs = device.createRfcommSocketToServiceRecord(UUID.fromString(getString(R.string.uuid)));
                                                bs.connect();
                                                socket = bs;
                                                is = bs.getInputStream();
                                                os = bs.getOutputStream();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                DataOutputStream dos = new DataOutputStream(baos);
                                                //dos.writeShort();
                                                makeToast("Подключено!");
                                            } catch (Exception ex) {
                                                makeToast(ex.getMessage());
                                            } finally {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pd.hide();
                                                    }
                                                });
                                            }
                                        }
                                    }).start();
                                    return false;
                                }
                            });
                            TextView mName = (TextView) item_view.findViewById(R.id.bluetooth_item_name);
                            TextView mID = (TextView) item_view.findViewById(R.id.bluetooth_item_id);
                            mName.setText(bd.getName());
                            mID.setText(bd.getAddress());
                            item_view.setTag(bd);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lv.addView(item_view);
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
        return true;
    }

    void makeToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

