package ysiparticipo.itnovate.com.ysiparticipo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ysiparticipo.itnovate.com.ysiparticipo.Adapters.EventAdapter;

public class CategoriaActivity extends AppCompatActivity {
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    private ArrayAdapter candidatoAdapter;
    private int total  = 0;
    private int pageCurrent = 1;
    private ArrayList<Event> listaEventos = new ArrayList<>();
    private int pageTotal = 1;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        String categoria = intent.getStringExtra("categoria");
        setContentView(R.layout.activity_categoria);
        listaEventos.clear();
        listView = (ListView) findViewById(R.id.listView);
        String urlpost = "http://172.16.107.194/api-participo/home/eventos/"+ categoria +"/"+ pageCurrent;
        JsonObjectRequest req = new JsonObjectRequest(urlpost,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        total = response.getInt("total");
                        pageTotal = response.getInt("pages");
                        JSONArray jsonArray = response.getJSONArray("data");
                        for(int i = 0; i< jsonArray.length();i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Event event = new Event();
                            event.setID(obj.getInt("ID"));
                            event.setFECHA(obj.getString("FECHA"));
                            event.setLUGAR(obj.getString("LUGAR"));
                            event.setHORA(obj.getString("HORA"));
                            event.setEVENTO(obj.getString("EVENTO"));
                            listaEventos.add(event);
                        }
                        EventAdapter eventAdapter = new EventAdapter(CategoriaActivity.this,R.layout.event_item,listaEventos);
                        listView.setAdapter(eventAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoriaActivity.this,EventDetailsActivity.class);
                intent.putExtra("item",listaEventos.get(position).getID());
                startActivity(intent);
            }
        });
        addToQueue(req);
    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            onPreStartConnection();
            fRequestQueue.add(request);
        }
    }

    public void onPreStartConnection() {
        this.setProgressBarIndeterminateVisibility(true);
    }

    public void onConnectionFinished() {
        this.setProgressBarIndeterminateVisibility(false);
    }

}
