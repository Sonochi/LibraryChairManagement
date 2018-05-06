package com.uuch.android_zxinglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.utils.NetConnect;

import org.json.JSONObject;

/**
 * Created by Ribbit on 2017/9/23.
 */
public class SeekSeat extends AppCompatActivity {
    String uid="";
    String sid="";
    TextView tv;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_seat);
        uid=getIntent().getStringExtra("userId");
        tv=(TextView) findViewById(R.id.textView_seekResult) ;
        button=(Button)findViewById(R.id.btn_getSeatOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uid.equals("")||sid.equals("")){
                    Toast.makeText(SeekSeat.this, "没有合适的位子", Toast.LENGTH_SHORT).show();
                }else{
                    GetNearSeat();
                    button.setClickable(false);
                }
            }
        });
        //定位GPS
        //模拟一下给个数据150当做是绝对距离
        GetNearSeatInfo();
    }

    public void GetNearSeat(){
        JSONObject jb=new JSONObject();
        try{
            jb.put("uid",uid);
            jb.put("sid",sid);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(SeekSeat.this, "json信息转化错误", Toast.LENGTH_SHORT).show();
        }
        String result=NetConnect.doConnect(jb,"GetSeatServlet");
        String result2="";
        try {
            JSONObject json=new JSONObject(result);
            result2=json.getString("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result2.equals("1")){
            Toast.makeText(SeekSeat.this, "占座成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(SeekSeat.this, "占座失败", Toast.LENGTH_SHORT).show();
        }

    }

    public void GetNearSeatInfo(){
        JSONObject jb=new JSONObject();
        try{
            jb.put("location","200");//测试随便找个距离
            jb.put("uid",uid);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(SeekSeat.this, "json信息转化错误", Toast.LENGTH_SHORT).show();
        }
        String result=NetConnect.doConnect(jb,"GetNearSeatInfo");
        String result2="";
        try {
            JSONObject json=new JSONObject(result);
            if(json.getString("bname").equals("")||
                    json.getString("rname").equals("")||
                    json.getString("sid").equals("")||
                    json.getString("number").equals("")){
                tv.setText("没找到合适的位子");
            }else{
                result2="帮你找到了在"+json.getString("bname")+"的"+json.getString("rname")+"教室里的"+
                        json.getString("number")+"号位子。";
                sid=json.getString("sid");
                tv.setText(result2);
            }
        }catch (Exception e){
            e.printStackTrace();
            tv.setText("没找到合适的位子");
        }

    }



}
