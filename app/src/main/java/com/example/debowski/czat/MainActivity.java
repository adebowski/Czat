package com.example.debowski.czat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String IP="ip";
    public static String NICK="nick";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ipAddres = (EditText)findViewById(R.id.edit_IP);
        final EditText nickName = (EditText)findViewById(R.id.edit_NickName);
        Button buttonStart= (Button)findViewById(R.id.buttonStart);


        buttonStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (TextUtils.isEmpty(ipAddres.getText()) || TextUtils.isEmpty(nickName.getText()))
                {
                    DisplayWarning("Please, type information");
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, chatActivity.class);
                    intent.putExtra(IP, ipAddres.getText().toString());
                    intent.putExtra(NICK, nickName.getText().toString());
                    startActivity(intent);
                }
            }
        });


    }
    public void DisplayWarning(String sText){
        Toast.makeText(this, sText, Toast.LENGTH_SHORT).show();
    }
}
