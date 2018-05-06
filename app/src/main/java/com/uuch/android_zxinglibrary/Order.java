package com.uuch.android_zxinglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.utils.NetConnect;

import org.json.JSONObject;

/**
 * Created by Ribbit on 2017/9/23.
 */
public class Order extends AppCompatActivity {
    Button btn_seek_mohu=null;
    Button btn_seek_jingque=null;
    EditText edit_order_id=null;
    String uid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        uid=getIntent().getStringExtra("uid");
        btn_seek_mohu=(Button)findViewById(R.id.btn_seek_mohu);
        btn_seek_jingque=(Button)findViewById(R.id.btn_seek_jingque);
        edit_order_id=(EditText)findViewById(R.id.edit_order_id);
        btn_seek_jingque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_order_id.equals("")){
                    Toast.makeText(Order.this,"请填写准确的座位ID",Toast.LENGTH_SHORT);
                }else{
                    try{
                    JSONObject jb=new JSONObject();
                        jb.put("uid",uid);
                        jb.put("sid",edit_order_id.getText().toString());
                        OrderSeat(jb);
                        btn_seek_jingque.setClickable(false);
                        btn_seek_mohu.setClickable(false);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(Order.this,"json出错了",Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        btn_seek_mohu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject jb=new JSONObject();
                    jb.put("uid",uid);
                    jb.put("sid","000");
                    OrderSeat(jb);
                    btn_seek_jingque.setClickable(false);
                    btn_seek_mohu.setClickable(false);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(Order.this,"json出错了",Toast.LENGTH_SHORT);
                }
            }
        });
    }
    public void OrderSeat(JSONObject jb){
        try{
            String str= NetConnect.doConnect(jb,"OrderSeatServlet");
            JSONObject jb2=new JSONObject(str);
            String result=jb2.getString("result");
            if("1".equals(result)){
                //释放成功
                Toast.makeText(Order.this,"预约成功",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Order.this,"你已经预约过了。",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Order.this,"请保证网络连接通畅。",Toast.LENGTH_LONG).show();
        }
    }
}
