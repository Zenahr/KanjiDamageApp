package com.kanjidamage.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView cards;
    private List<Map<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcherHelper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = String.valueOf(s);
                updateSearchResults(keyword);
            }
        });

        cards = findViewById(R.id.cards);
        cards.setLayoutManager(new LinearLayoutManager(this));

        data = loadData();
    }

    private void updateSearchResults(String keyword) {
        cards.setAdapter(new DataAdapter(filter(keyword)));
    }

    private List<Map<String, String>> filter(String keyword) {
        List<Map<String, String>> result = new ArrayList<>();

        if (!keyword.isEmpty()) {
            for (Map<String, String> row : data) {
                if (row.get("label").contains(keyword)) {
                    result.add(row);
                }
            }
        }

        return result;
    }

    private List<Map<String, String>> loadData() {
        JSONObject json = loadJSONFromAsset();
        List<Map<String, String>> data = new ArrayList<>();

        try {
            JSONArray kanjis = json.getJSONArray("kanji");
            for (int i = 0; i < kanjis.length(); i++) {
                JSONObject kanji = kanjis.getJSONObject(i);
                Map<String, String> row = new HashMap<>();
                row.put("label", kanji.getString("kanji"));
                row.put("description", kanji.getString("meaning"));
                data.add(row);
            }

            JSONArray jukugos = json.getJSONArray("jukugo");
            for (int i = 0; i < jukugos.length(); i++) {
                JSONObject jukugo = jukugos.getJSONObject(i);
                Map<String, String> row = new HashMap<>();
                row.put("label", jukugo.getString("kanji"));
                row.put("description", jukugo.getString("meaning"));
                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public JSONObject loadJSONFromAsset() {
        try {
            InputStream is = getAssets().open("kanjidamage.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
