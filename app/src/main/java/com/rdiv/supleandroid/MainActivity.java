package com.rdiv.supleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {

    Button btnBDD;
    TextView tvInfo;
    String respuesta = "";
    double spet, deg, gus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBDD = findViewById(R.id.btnBDD);
        tvInfo = findViewById(R.id.tvInfo);

        ConsumirAPI();

        btnBDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrearTabla();
            }
        });
    }

    public void ConsumirAPI() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=-32.91711542962828&lon=-60.779945923290136&appid=bd5e378503939ddaee76f12ad7a97608";
        OkHttpClient cliente = new OkHttpClient();
        Request get = new Request.Builder()
                .url(url)
                .build();
        cliente.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {

                        respuesta = responseBody.string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject(respuesta);

                                    JSONArray lista = json.getJSONArray("list");

                                    for (int i = 0; i < lista.length(); i++) {
                                        JSONObject weatherItem = lista.getJSONObject(i);
                                        String dtTxt = weatherItem.getString("dt_txt");

                                        if ("2024-02-23 15:00:00".equals(dtTxt)) {
                                            JSONObject wind = weatherItem.getJSONObject("wind");

                                            spet = wind.getDouble("speed");
                                            deg = wind.getDouble("deg");
                                            gus = wind.optDouble("gust", 0.0);

                                            tvInfo.setText("speed: " + spet + "\n" +
                                                    "deg: " + deg + "\n" +
                                                    "gust: " + gus + "\n");

                                            break;
                                        }
                                    }
                                    Log.i("data", respuesta);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    Log.i("data", responseBody.string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void CrearTabla() {
        BDD admin = new BDD(this, "tabladatos.db", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        if (spet != 0 && deg != 0 && gus != 0) {
            ContentValues datosRegistrar = new ContentValues();
            datosRegistrar.put("speed", spet);
            datosRegistrar.put("deg", deg);
            datosRegistrar.put("gust", gus);

            bd.insert("SupleMovil", null, datosRegistrar);

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show();
        }
    }

}
