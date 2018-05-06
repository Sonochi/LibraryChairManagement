package com.uuch.android_zxinglibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.utils.NetConnect;

import org.json.JSONObject;

/**
 * Created by Ribbit on 2017/9/23.
 */

public class LogonActivity extends AppCompatActivity {
    private EditText userName, password;
    private CheckBox rem_pw;
    private Button btn_login;
    private String userNameValue,passwordValue;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);
        //获得实例对象
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.et_zh);
        password = (EditText) findViewById(R.id.et_mima);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        //auto_login = (CheckBox) findViewById(R.id.cb_auto);
        btn_login = (Button) findViewById(R.id.btn_login);


        //判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            userName.setText(sp.getString("USER_NAME", ""));
            password.setText(sp.getString("PASSWORD", ""));
            //判断自动登陆多选框状态
            //暂时不做..
        }



          // 登录监听事件
        btn_login.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameValue = userName.getText().toString();
                passwordValue = password.getText().toString();
                boolean flg=false;
                //发送数据给服务器
                try{
                    JSONObject jb=new JSONObject();
                    jb.put("name",userNameValue);
                    jb.put("password",passwordValue);
                    String result=NetConnect.doConnect(jb,"LogonServlet");
                    JSONObject jb2=new JSONObject(result);
                    flg=(jb2.getBoolean("result"));
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (flg) {
                    Toast.makeText(LogonActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    //登录成功和记住密码框为选中状态才保存用户信息
                    if (rem_pw.isChecked()) {
                        //记住用户名、密码、
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("USER_NAME", userNameValue);
                        editor.putString("PASSWORD", passwordValue);
                        editor.commit();
                    }
                    //跳转界面
                    Intent intent = new Intent();
                    //
                    intent.putExtra("userId",userNameValue);
                    setResult(RESULT_OK, intent);
                    finish();

                } else if(userName.getText().toString().equals("")){
                    Toast.makeText(LogonActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
                } else if(password.getText().toString().equals("")){
                    Toast.makeText(LogonActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LogonActivity.this, "用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                }
            }
        });


        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rem_pw.isChecked()) {
                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();
                }else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }
        });

    }
}
