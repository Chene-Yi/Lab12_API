package com.example.lab12_api;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 2. 定義 Button 的點擊事件
        findViewById(R.id.btn_search).setOnClickListener(v -> {
            // 3. 定義目標請求的 URL
            String URL = "https://tools-api.italkutalk.com/java/lab12";

            // 4. 建立 Request 的請求物件,並設定 URL
            Request request = new Request.Builder().url(URL).build();

            // 5. 建立 OkHttpClient 物件
            OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

            // 6. 透過 okHttpClient 的變數,使用 newCall()發送請求,再透過 enqueue()接收回傳
            okHttpClient.newCall(request).enqueue(new Callback() {
                // 7. 當發送成功後,要執行的方法
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.code() == 200) {// 判斷回應狀態碼是否為200及回傳是否有值
                        if(response.body() != null) return;
                        // 使用 Gson 將 JSON 字串轉換為 Data 物件
                        Data data = new Gson().fromJson(response.body().string(), Data.class);

                        // 建立一個字串陣列，用來儲存伺服器返回的資料
                        final String[] items = new String[data.result.results.length];

                        // 將資料填入字串陣列中
                        for (int i = 0; i < items.length; i++) {
                            items[i] = "\n列車即將進入:" + data.result.results[i].Station +
                                    "\n列車行駛目的地:" + data.result.results[i].Destination;
                        }

                        // 由於 API 請求屬於背景執行緒, 所以 OkHttp 請求會發生在背景執行緒
                        // 因此需要透過 runOnUiThread 切換執行緒回主執行緒
                        runOnUiThread(() -> {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("台北捷運列車到站站名")
                                    .setItems(items, null)
                                    .show(); // 使用 Dialog 顯示伺服器回傳的資料
                        });
                    } else if (!response.isSuccessful()) {
                        Log.e("伺服器錯誤", response.code() + " " + response.message());
                    } else {
                        Log.e("其他錯誤", response.code() + " " + response.message());
                    }
                }

                // 8. 當發送失敗後,要執行的方法
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (e.getMessage() != null) {
                        Log.e("查詢失敗", e.getMessage());
                    }
                }
            });
        });
    }
}