package com.uuch.android_zxinglibrary.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ribbit on 2017/11/12.
 */

public class NetConnect {
    public static String doConnect(JSONObject jb,String servlet){
        String msg="";
                URL url = null;
                try {
                    url = new URL("http://192.168.43.15:8080/"+servlet);
                    URLConnection rulConnection = url.openConnection();// 此处的urlConnection对象实际上是根据URL的
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
                    // 设定请求的方法为"POST"，默认是GET
                    httpUrlConnection.setRequestMethod("POST");
                    // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
                    // http正文内，因此需要设为true, 默认情况下是false;
                    httpUrlConnection.setDoOutput(true);
                    // 设置是否从httpUrlConnection读入，默认情况下是true;
                    httpUrlConnection.setDoInput(true);
                    // Post 请求不能使用缓存
                    httpUrlConnection.setUseCaches(false);
                    httpUrlConnection.setRequestProperty("Content-type", "application/json");
                    httpUrlConnection.setRequestProperty("Accept-Charset", "utf-8");  //设置编码语言
                    httpUrlConnection.setConnectTimeout(2000);
                    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
                    // 所以在开发中不调用上述的connect()也可以)。
                    // 连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
                    httpUrlConnection.connect();
                    OutputStreamWriter outStrmW = new OutputStreamWriter(httpUrlConnection.getOutputStream());
                    // 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。
                    //ObjectOutputStream objOutputStrm = new ObjectOutputStream(outStrm);

                    // 向对象输出流写出数据，这些数据将存到内存缓冲区中
                   // objOutputStrm.writeObject(new String(jb.toString()));
                    outStrmW.write(jb.toString());
                    // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
                    //objOutputStrm.flush();
                    outStrmW.flush();
                    // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
                    // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
                    //objOutputStrm.close();
                    outStrmW.close();

                    Log.i("msg",String.valueOf(httpUrlConnection.getResponseCode()));
                    if (httpUrlConnection.getResponseCode() == 200) {
                        // 获取响应的输入流对象
                        InputStream is = httpUrlConnection.getInputStream();

                        // 创建字节输出流对象
                        ByteArrayOutputStream message = new ByteArrayOutputStream();
                        // 定义读取的长度
                        int len = 0;
                        // 定义缓冲区
                        byte buffer[] = new byte[1024];
                        // 按照缓冲区的大小，循环读取
                        while ((len = is.read(buffer)) != -1) {
                            // 根据读取的长度写入到os对象中
                            message.write(buffer, 0, len);
                        }

                        // 释放资源
                        is.close();
                        message.close();
                        // 返回字符串
                        msg = new String(message.toByteArray());
//                        Log.i("msg","得到回来信息");
//                        Log.i("msg",msg);
//                        JSONObject jo =new JSONObject(msg);
//                        Log.i("msg",jo.getString("userId"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

        return msg;
    }

}
