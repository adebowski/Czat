package com.example.debowski.czat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class chatActivity extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String nick;
    String ip;
    int qos = 0;
    EditText messageEditText;
    MqttClient sampleClient=null;

    ListView chatListView;

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            listItems.add("["+msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            adapter.notifyDataSetChanged();
            chatListView.setSelection(listItems.size()-1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageEditText = (EditText)findViewById(R.id.editText);
        chatListView = (ListView) findViewById(R.id.listChat);
        TextView nickName= (TextView)findViewById(R.id.textMyNick);
        Button SEND= (Button)findViewById(R.id.buttonSend);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        chatListView.setAdapter(adapter);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);

        nickName.setText(nick.toString());

        SEND.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                try {
                    MqttMessage message = new MqttMessage(messageEditText.getText().toString().getBytes());
                    message.setQos(qos);
                    sampleClient.publish(nick, message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();
    }


    public void DisplayWarning(String sText){
        Toast.makeText(this, sText, Toast.LENGTH_SHORT).show();
    }


    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", s);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                    //messageEditText.setText("");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
                //TODO
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
