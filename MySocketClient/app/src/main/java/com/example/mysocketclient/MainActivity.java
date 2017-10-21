package com.example.mysocketclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    EditText ip;//输入IP地址
    EditText editText;//编辑想要发送消息的内容
    TextView textView;//展现发送消息的内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = (EditText) findViewById(R.id.etIp);
        editText = (EditText) findViewById(R.id.etContent);
        textView = (TextView) findViewById(R.id.texView);

        findViewById(R.id.btnconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();//为了代码看起来更加简洁，这里创建一个方法，具体的实现方法在下面的类中实现
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();//同上
            }
        });
    }
    //-----------------------------------------------------------------------上面是和界面相关的，下面的是和代码相关的
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    public void connect(){

            AsyncTask<Void,String,Void> read = new AsyncTask<Void, String, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try{
                        socket = new Socket(ip.getText().toString(),12345);
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        publishProgress("@success");
                        writer.write(editText.getText().toString()+"\n");
                        writer.flush();
                    }catch (UnknownHostException e1){
                        Toast.makeText(MainActivity.this,"无法建立连接",Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        Toast.makeText(MainActivity.this,"无法建立连接",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        String line;
                        while ((line = reader.readLine())!=null){
                            publishProgress(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;

                }

                @Override
                protected void onProgressUpdate(String... values) {
                    if (values[0].equals("@success")){
                        Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();

                    }

                    textView.append("别人说:"+values[0]+"\n");
                    super.onProgressUpdate(values);
                }

            };
            read.execute();
    }
    public void send(){
            try {
                textView.append("我说:"+editText.getText().toString()+"\n");
                writer.write(editText.getText().toString()+"\n");
                editText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
