package ysiparticipo.itnovate.com.ysiparticipo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ysiparticipo.itnovate.com.ysiparticipo.Adapters.EventAdapter;

public class EventDetailsActivity extends AppCompatActivity {
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    Toolbar toolbar;
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    BackendlessUser current;
    private Integer item;
    Bitmap bitmap;
    private Event currentEvent;
    private double latitud;
    private double longitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        current = Backendless.UserService.CurrentUser();
        if(current == null){
            finish();
        }
        Intent intent = getIntent();
        item = intent.getIntExtra("item",1);
        setContentView(R.layout.activity_event_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(view);
            }
        });
        String urlpost = "http://172.16.107.194/api-participo/home/evento/"+ item;
        JsonObjectRequest req = new JsonObjectRequest(urlpost,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            toolbar.setTitle(response.getString("CATEGORIA"));
                            TextView EVENTO = (TextView) findViewById(R.id.textView);
                            EVENTO.setText(response.getString("EVENTO"));
                            TextView lugar = (TextView) findViewById(R.id.textView4);
                            lugar.setText(response.getString("LUGAR"));
                            TextView hora = (TextView) findViewById(R.id.textView3);
                            hora.setText(response.getString("FECHA") + " / " + response.getString("HORA"));
                            latitud = response.getDouble("Latitud");
                            longitud = response.getDouble("Longitud");
                            ImageView map = (ImageView) findViewById(R.id.imageView2);
                            Picasso.with(EventDetailsActivity.this)
                                    .load("https://maps.googleapis.com/maps/api/staticmap?center=" + response.getDouble("Latitud") + "," + response.getDouble("Longitud") + "&zoom=17&size=600x200&maptype=roadmap" +
                                            "&markers=color:red%7Clabel:C%7C" + response.getDouble("Latitud") + "," + response.getDouble("Longitud") +
                                            "&key=AIzaSyDQ7U4smLTUnkZ3QMoclnGO58DyF23A2fQ")
                                    .into(map);
                            LinearLayout layout =(LinearLayout)findViewById(R.id.cabecera);
                            layout.setBackgroundResource(R.drawable.eventos);
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
        setSupportActionBar(toolbar);
        addToQueue(req);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(),current.getEmail()+"",Toast.LENGTH_LONG).show();
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    MaterialDialog dialog = new MaterialDialog.Builder(EventDetailsActivity.this)
                            .title("Yo SI participo, de este evento")
                            .customView(R.layout.camera_make, true)
                            .positiveText("YO SI PARTICIPO")
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                                    Date date = new Date();
                                    String nameFile = current.getEmail()+"_"+ dateFormat.format(date) +".png";
                                    Backendless.Files.Android.upload( bitmap, Bitmap.CompressFormat.PNG, 100, nameFile , "media", new AsyncCallback<BackendlessFile>()
                                    {
                                        @Override
                                        public void handleResponse( final BackendlessFile backendlessFile )
                                        {

                                            final String url = backendlessFile.getFileURL();
                                            HashMap<String, String> mRequestParams = new HashMap<String, String>();
                                            mRequestParams.put("id_usuario", current.getEmail());
                                            mRequestParams.put("nombre",current.getProperty("name").toString());
                                            mRequestParams.put("id_evento",item+"");
                                            mRequestParams.put("categoria","CULTURA");
                                            mRequestParams.put("foto", url);
                                            JSONObject obj = new JSONObject();

                                            try {
                                                obj.put("id_usuario", current.getEmail());
                                                obj.put("nombre",current.getProperty("name").toString());
                                                obj.put("id_evento", item+"");
                                                obj.put("categoria", "CULTURA");
                                                obj.put("foto", url);
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            String urlpost = "http://172.16.107.194/api-participo/home/participacion";
                                            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlpost,
                                                    obj,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try {
                                                                Toast.makeText(getApplication(),"url: "+response.getString("resultado"),Toast.LENGTH_LONG).show();
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
                                            setSupportActionBar(toolbar);
                                            addToQueue(req);
                                        }

                                        @Override
                                        public void handleFault( BackendlessFault backendlessFault )
                                        {

                                        }
                                    });
                                }
                            }).build();
                    ImageView imageView = (ImageView) dialog.getCustomView().findViewById(R.id.imageView10);
                    ContentResolver cr = getContentResolver();
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);
                        imageView.setImageBitmap(bitmap);
                        dialog.show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load" + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
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

    public void abrirMapa(View view){
        Intent intent = new Intent(EventDetailsActivity.this,MapsActivity.class);
        intent.putExtra("latitud",latitud);
        intent.putExtra("longitud", longitud);
        startActivity(intent);;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.home){
            finish();
            return true;
        }
        if (id == R.id.action_sharing) {
            new MaterialDialog.Builder(this)
                    .title("yo SI participo")
                    .content("Escribe un mensaje para compartir")
                    .inputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .inputMaxLength(50)
                    .positiveText("Compartir")
                    .input("", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, input.toString());
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
