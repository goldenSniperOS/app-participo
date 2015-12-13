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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
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
                           toolbar.setTitle(response.getString("EVENTO"));
                            TextView lugar = (TextView) findViewById(R.id.lugar);
                            lugar.setText(response.getString("LUGAR"));
                            TextView hora = (TextView) findViewById(R.id.hora);
                            hora.setText(response.getString("FECHA") + "-" + response.getString("HORA"));
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
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.eventos);
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
                    current = Backendless.UserService.CurrentUser();
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
                                                            Toast.makeText(getApplication(),"url: "+response,Toast.LENGTH_LONG).show();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    VolleyLog.e("Error: ", error.getMessage());
                                                }
                                            });
                                            setSupportActionBar(toolbar);
                                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                            imageView.setImageResource(R.drawable.eventos);
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



}
