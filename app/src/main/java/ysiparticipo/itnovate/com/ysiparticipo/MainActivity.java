package ysiparticipo.itnovate.com.ysiparticipo;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ysiparticipo.itnovate.com.ysiparticipo.Adapters.MainAdapter;

public class MainActivity extends AppCompatActivity {
    BackendlessUser current;
    private ArrayList<MainData> ListaCategoria = new ArrayList<>();
    private String[] internalCategorias = new String[]{
        "ARTE",
        "CINE",
        "CONCIERTO",
        "ESPACIOS-PUBLICOS",
        "PLAN-LECTOR",
        "TEATRO",
    };
    private VolleyS volley;
    protected RequestQueue fRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyS.getInstance(this);
        ListaCategoria.add(new MainData("ARTE",R.drawable.ic_it_rojo_lg));
        ListaCategoria.add(new MainData("CINE",R.drawable.ic_it_rojo_lg));
        ListaCategoria.add(new MainData("CONCIERTO",R.drawable.ic_it_rojo_lg));
        ListaCategoria.add(new MainData("ESPACIO PÚBLICOS",R.drawable.ic_it_rojo_lg));
        ListaCategoria.add(new MainData("PLAN LECTOR",R.drawable.ic_it_rojo_lg));
        ListaCategoria.add(new MainData("TEATRO",R.drawable.ic_it_rojo_lg));
        fRequestQueue = volley.getRequestQueue();
        setContentView(R.layout.activity_main);
        current = Backendless.UserService.CurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String appVersion = "v1";
        Backendless.initApp(this, "F89F55EE-64AD-32BF-FFE1-96C258DA8800", "C7F9D9B6-6A7E-6FCF-FF7E-B1D7272E9900", appVersion);
        ArrayAdapter arrayAdapter = new MainAdapter(this, R.layout.layout_main_item,ListaCategoria);
        ListView listView = (ListView) findViewById(R.id.listaCategorias);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cat = internalCategorias[position];
                Intent intent = new Intent(MainActivity.this, CategoriaActivity.class);
                intent.putExtra("categoria",cat);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
