package ysiparticipo.itnovate.com.ysiparticipo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    BackendlessUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView educacion_cultura =(ImageView)findViewById(R.id.educacion_cultura);
        current = Backendless.UserService.CurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void abrirActivity(View view){
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
    }

}
