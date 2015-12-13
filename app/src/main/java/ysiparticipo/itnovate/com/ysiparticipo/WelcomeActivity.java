package ysiparticipo.itnovate.com.ysiparticipo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView educacion_cultura =(ImageView)findViewById(R.id.educacion_cultura);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
    }

    public void abrirActivity(View view){
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
    }

}
