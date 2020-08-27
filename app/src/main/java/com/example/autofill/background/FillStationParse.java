package com.example.autofill.background;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.autofill.network.HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class FillStationParse extends AsyncTask<String, Void, String> {
    public String returnmessage1 = "";
    private static final String TAG = "FillStationParse成功";
    public Response delegate1 = null;

    private int gettime() {
        long timeStamp = System.currentTimeMillis();
        return (int) (timeStamp / 1000);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        //Log.d(TAG, "doInBackground: 返回的打卡次数信息："+strings);
        String jsonFinalString = "";
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("xh",strings[0]);
            jsonParam.put("sign", "c4cb4739a0b923820scc509a6f75849b");
            jsonParam.put("timestamp", gettime());

            String s0 = jsonParam.toString();
            String s1 = new String(s0.getBytes(), StandardCharsets.UTF_8);
            //base64 编码一下
            byte[] data_encode = s1.getBytes();
            String stringbase64 = Base64.encodeToString(data_encode, Base64.DEFAULT);
            //构建查询数据
            JSONObject jsonParam1 = new JSONObject();
            jsonParam1.put("key", stringbase64);
            jsonFinalString = jsonParam1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpPost checkPostedData = new
                HttpPost("https://we.cqu.pt/api/mrdk/get_mrdk_flag.php",jsonFinalString);
        Log.d(TAG, "doInBackground: "+jsonFinalString);
        return checkPostedData.PostData();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if(s!=null) {
                JSONObject jsonObject = new JSONObject(s);
                String resultArray = jsonObject.getString("data");
                JSONObject jsonObjectData = new JSONObject(resultArray);
                int count = 100;
                count = jsonObjectData.getInt("count");
                if (count != 100) {
                    try {
                        if (count == 0)
                            returnmessage1 = "TODAYNOFILL";
                        else if(count == 1){
                            returnmessage1 = "TODAYHASFILL";
                        }else {
                            returnmessage1 = "TODAYHASFILL";
                        }
                    } catch (Exception e) {
                        returnmessage1 = "UNKNOWN_ERROR";
                        e.printStackTrace();
                    }
                } else {
                    returnmessage1 = "NULL_ERROR";
                }
            }else {
                Log.d(TAG, "成功失败，检查打卡信息时返回的s为空");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "onPostExecute: 执行完检查打卡次数");
        delegate1.onPostFinish(returnmessage1);
    }
}