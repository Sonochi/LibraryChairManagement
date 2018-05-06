package com.uuch.android_zxinglibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.uuch.android_zxinglibrary.utils.code_trans;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Created by Ribbit on 2017/9/23.
 */

public class ThreeActivity extends BaseActivity {

    public EditText editText = null;
    public Button button = null;
    public Button button1 = null;
    public ImageView imageView = null;

    public Bitmap mBitmap = null;
    String password = "958802882010913257074332531189842634785" +
            "729877354946875887501857953775777216308447887369944" +
            "7306034466200616411960574122434059469100235892702736860872901247123456";

    //较大的rsa会影响程序网络访问速度，暂时放弃，较小的容易被攻破。
    public int public_key=1184;//RSA
    public int private_key=2021;//RSA
    public int mod_key=2923;//RSA


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {

        editText = (EditText) findViewById(R.id.edit_content);
        button = (Button) findViewById(R.id.button_content);
        button1 = (Button) findViewById(R.id.button1_content);
        imageView = (ImageView) findViewById(R.id.image_content);
        /**
         * 保存图片到本地，用作测试
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String textContent = editText.getText().toString();
//                if (TextUtils.isEmpty(textContent)) {
//                    Toast.makeText(ThreeActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                editText.setText("");
//                mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//                imageView.setImageBitmap(mBitmap);
                if(mBitmap==null){
                    Toast.makeText(ThreeActivity.this, "请你先生成二维码!", Toast.LENGTH_SHORT).show();
                }else{
                    saveImageToGallery(getApplicationContext(),mBitmap);
                }
            }
        });




        /**
         * 生成不带logo的二维码图片
         */
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textContent = editText.getText().toString();
                //加密
                byte[] result = des.encrypt(textContent.getBytes(),password);
                String str_result= code_trans.byteArr2HexStr(result);
                if (TextUtils.isEmpty(textContent)) {
                    Toast.makeText(ThreeActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                editText.setText(str_result);
                mBitmap = CodeUtils.createImage(str_result, 400, 400, null);
                imageView.setImageBitmap(mBitmap);
            }
        });
    }
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
