package com.uuch.android_zxinglibrary;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.utils.NetConnect;

import org.json.JSONObject;


/**
 * Created by Ribbit on 2017/9/23.
 */
public class Myseat extends AppCompatActivity {

    TextView textView_number;
    TextView textView_building;
    TextView textView_room;
    String username;
    Button btn_temp=null;
    Button btn_rel=null;
    Button btn_goto=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myseat);
        username=getIntent().getStringExtra("userId");
         textView_number=(TextView) findViewById(R.id.textView4);
         textView_building=(TextView)findViewById(R.id.textView6);
         textView_room=(TextView)findViewById(R.id.textView8);
        btn_rel=(Button)findViewById(R.id.button_rel);
        btn_temp=(Button)findViewById(R.id.btn_temp);
        btn_goto=(Button)findViewById(R.id.btn_goto);
        queryInfo();
        btn_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动导航功能
                Toast.makeText(Myseat.this,"功能等待完善……",Toast.LENGTH_LONG).show();
            }
        });
        btn_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView_number.getText().equals("没找到位子")) {
                    Toast.makeText(Myseat.this, "您还没有位子呢", Toast.LENGTH_LONG).show();
                } else {
                    //释放位子
                    new AlertDialog.Builder(Myseat.this).setTitle("确认要放弃这个位子吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("是，不要了", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作
                                    RelSeat();
                                }
                            })
                            .setNegativeButton("我点错了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“返回”后的操作,这里不设置没有任何操作
                                }
                            }).show();
                }
            }
        });

        //临时走开下
        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView_number.getText().equals("没找到位子")){
                    Toast.makeText(Myseat.this,"你没有位子不能暂离",Toast.LENGTH_LONG).show();
                }else{
                    TempOut();
                }
            }
        });
    }

    public void queryInfo(){
        JSONObject jb=new JSONObject();
        try{
            jb.put("userId",username);
            String str=NetConnect.doConnect(jb,"QuerySeatServlet");
            JSONObject jb2=new JSONObject(str);
            String result=jb2.getString("result");
            if("1".equals(result)){
                //有位子
                textView_number.setText(jb2.getString("number"));
                textView_building.setText(jb2.getString("bname"));
                textView_room.setText(jb2.getString("rname"));
            }else{
                //没位子
                Toast.makeText(Myseat.this,"您还没有位子，快找一个吧。",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Myseat.this,"请保证网络连接通畅。",Toast.LENGTH_LONG).show();
        }
    }

    public void RelSeat(){
        JSONObject jb=new JSONObject();
        try{
            jb.put("userId",username);
            String str=NetConnect.doConnect(jb,"RelSeatServlet");
            JSONObject jb2=new JSONObject(str);
            String result=jb2.getString("result");
            if("1".equals(result)){
                //释放成功
                Toast.makeText(Myseat.this,"释放成功。",Toast.LENGTH_LONG).show();
                textView_number.setText("没找到位子");
                textView_building.setText("没找到位子");
                textView_room.setText("没找到位子");
            }else{
                //释放失败
                Toast.makeText(Myseat.this,"释放失败。",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Myseat.this,"请保证网络连接通畅。",Toast.LENGTH_LONG).show();
        }
    }

    public void TempOut(){
        JSONObject jb=new JSONObject();
        try{
            jb.put("userId",username);
            String str=NetConnect.doConnect(jb,"TempOutServlet");
            JSONObject jb2=new JSONObject(str);
            String result=jb2.getString("result");
            if("1".equals(result)){
                //释放成功
                Toast.makeText(Myseat.this,"暂离成功，记得半小时回来扫码哦。",Toast.LENGTH_LONG).show();
            }else if("2".equals(result)){
                //释放失败
                Toast.makeText(Myseat.this,"你已经暂离了，不要重复点击。",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Myseat.this,"暂离失败。",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Myseat.this,"请保证网络连接通畅。",Toast.LENGTH_LONG).show();
        }
    }
}
