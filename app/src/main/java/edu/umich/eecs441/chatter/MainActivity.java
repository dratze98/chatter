package edu.umich.eecs441.chatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshContainer;

    private ArrayList<Chatt> chattArrayList;
    private ChattAdapter chattAdapter;

    private void refreshTimeline() {
        chattAdapter.clear();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://167.172.159.235/getchatts/";

        JsonObjectRequest getRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)  {
                        try {
                            JSONArray array = response.getJSONArray("chatts");
                            for (int i = 0; i < array.length(); i++) {
                                String username = array.getJSONArray(i).getString(0);
                                String message = array.getJSONArray(i).getString(1);
                                String timestamp = array.getJSONArray(i).getString(2);
                                chattAdapter.add(new Chatt(username, message, timestamp));
                            }
                        }
                        catch (JSONException e) {
                        }
                    }
                    },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        queue.add(getRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chattArrayList = new ArrayList<Chatt>();
        chattAdapter = new ChattAdapter(this, chattArrayList);
        ListView lView = (ListView) findViewById(R.id.chattListView);
        lView.setAdapter(chattAdapter);

        refreshContainer = (SwipeRefreshLayout) findViewById(R.id.refreshContainer);
        refreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
                refreshContainer.setRefreshing(false);
            }
        });

        // setup refreshContainer here later
        refreshTimeline();
        refreshContainer.setRefreshing(false);
    }


    public void postChatt(View view) {

        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }
}
