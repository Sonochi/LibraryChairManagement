package com.uuch.android_zxinglibrary;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.ImageUtil;
import com.uuch.android_zxinglibrary.client.ServiceManager;
import com.uuch.android_zxinglibrary.utils.CheckPermissionUtils;
import com.uuch.android_zxinglibrary.utils.NetConnect;
import com.uuch.android_zxinglibrary.utils.code_trans;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.R.attr.name;

/**
 * Created by Ribbit on 2017/9/23.
 */

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    public static final int LOGON_CODE = 100;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;
    String password = "95880288201091325707433253118984263478572987735494" +
            "68758875018579537757772163084478873699447306034466200616411960" +
            "574122434059469100235892702736860872901247123456";//DES password

    public Button button1 = null;
    public Button button2 = null;
    public Button button3 = null;
    public Button button4 = null;
    public Button btn_seek = null;
    public Button btn_myseat = null;
    public Button btn_order = null;
    public String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //错误做法，去掉"无法再main中进行网络编程的限制"---不规范做法
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        /**
         * 初始化组件
         */
        initView();
        //初始化权限
        initPermission();


        //读取数据库中登录的信息
        //暂时不做了吧，以后实现还得加入注销功能
        //没有信息就登录
        if(true){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,LogonActivity.class);
            startActivityForResult(intent,LOGON_CODE);
        }

    }

    /**
     * 初始化权限事件
     */
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了

        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }
    /**
     * 初始化组件
     */
    private void initView() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button4 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(new ButtonOnClickListener(button1.getId()));
        button2.setOnClickListener(new ButtonOnClickListener(button2.getId()));
        button4.setOnClickListener(new ButtonOnClickListener(button4.getId()));
        btn_seek = (Button) findViewById(R.id.btn_seek);
        btn_myseat = (Button) findViewById(R.id.btn_myseat);
        btn_order = (Button) findViewById(R.id.btn_order);
        btn_seek.setOnClickListener(new ButtonOnClickListener(btn_seek.getId()));
        btn_myseat.setOnClickListener(new ButtonOnClickListener(btn_myseat.getId()));
        btn_order.setOnClickListener(new ButtonOnClickListener(btn_order.getId()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    try {
                        byte[] decryResult = des.decrypt(code_trans.hexStr2ByteArr(result), password);
                        Toast.makeText(MainActivity.this, "正在占座，ID是："+new String(decryResult), Toast.LENGTH_LONG).show();
                        String str=new String(decryResult);
                        JSONObject jb=new JSONObject();
                        jb.put("uid",userId);
                        jb.put("sid",str);
                        getChair(jb);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

        /**
         * 选择系统图片并解析
         */
        else if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            try {
                                byte[] decryResult = des.decrypt(code_trans.hexStr2ByteArr(result), password);
                                Toast.makeText(MainActivity.this, "正在占座，ID是："+new String(decryResult), Toast.LENGTH_LONG).show();
                                String str=new String(decryResult);
                                JSONObject jb=new JSONObject();
                                jb.put("uid",userId);
                                jb.put("sid",str);
                                getChair(jb);
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == REQUEST_CAMERA_PERM) {
            Toast.makeText(this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }

        else if(requestCode == LOGON_CODE){
            //登录信息录入
            userId=data.getStringExtra("userId");
            // 服务按钮设置
            Button okButton = (Button) findViewById(R.id.btn_settings);
            okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ServiceManager.viewNotificationSettings(MainActivity.this);
                }
            });
            //启动服务
            ServiceManager serviceManager = new ServiceManager(this);
            serviceManager.setNotificationIcon(R.drawable.notification);
            serviceManager.startService();
            serviceManager.setAlias(userId);
            Log.i("asdasd",userId);
        }
    }


    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;


    /**
     * EsayPermissions接管权限处理逻辑
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask(int viewId) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            onClick(viewId);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }


    /**
     * 按钮点击监听
     */
    class ButtonOnClickListener implements View.OnClickListener{

        private int buttonId;

        public ButtonOnClickListener(int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button2) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            } else if (v.getId() == R.id.button4) {
                Intent intent = new Intent(MainActivity.this, ThreeActivity.class);
                startActivity(intent);
            }else if(v.getId() == R.id.btn_seek){
                //查找位子
                //找位子查询数据库，等待返回
                Intent intent = new Intent();
                intent.putExtra("userId", userId);
                intent.setClass(MainActivity.this,SeekSeat.class);
                startActivity(intent);
            }else if(v.getId() == R.id.btn_myseat){
                 //查看我的位子
                Intent intent = new Intent();
                intent.putExtra("userId", userId);
                intent.setClass(MainActivity.this,Myseat.class);
                startActivity(intent);
            }else if(v.getId() == R.id.btn_order){
                 //预约位子
                //如果时间不在晚上8点到9点钟，那就禁止进入这个界面
                Calendar ca=Calendar.getInstance();
                Log.i("time",String.valueOf(ca));
                Log.i("time","asd");
                Intent intent = new Intent();
                intent.putExtra("uid", userId);
                intent.setClass(MainActivity.this,Order.class);
                startActivity(intent);
            } else {
                cameraTask(buttonId);
            }
        }
    }


    /**
     * 按钮点击事件处理逻辑
     * @param buttonId
     */
    private void onClick(int buttonId) {
        switch (buttonId) {
            case R.id.button1:
                Intent intent = new Intent(getApplication(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    public void getChair(JSONObject jb){
        String result=NetConnect.doConnect(jb,"GetSeatServlet");
        String result2="";
        try {
            JSONObject json=new JSONObject(result);
            result2=json.getString("result");
        }catch (Exception e){
            e.printStackTrace();
        }

        if(result2.equals("1")){
            Toast.makeText(MainActivity.this, "占座成功", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "占座失败", Toast.LENGTH_LONG).show();
        }
    }

}
