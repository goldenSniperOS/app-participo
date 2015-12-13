package ysiparticipo.itnovate.com.ysiparticipo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity
{
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.login );

    initUI();

    Backendless.setUrl( Defaults.SERVER_URL );
    Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

    Backendless.UserService.isValidLogin(new DefaultCallback<Boolean>(this) {
      @Override
      public void handleResponse(Boolean isValidLogin) {
        if (isValidLogin && Backendless.UserService.CurrentUser() == null) {
          String currentUserId = Backendless.UserService.loggedInUser();

          if (!currentUserId.equals("")) {
            Backendless.UserService.findById(currentUserId, new DefaultCallback<BackendlessUser>(LoginActivity.this, "Logging in...") {
              @Override
              public void handleResponse(BackendlessUser currentUser) {
                super.handleResponse(currentUser);
                Backendless.UserService.setCurrentUser(currentUser);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
              }
            });
          }
        }

        super.handleResponse(isValidLogin);
      }
    });
  }

  private void initUI()
  {
  }

  public void onLoginWithFacebookButtonClicked()
  {
    Map<String, String> facebookFieldMappings = new HashMap<String, String>();
    facebookFieldMappings.put("email", "fb_email");
    facebookFieldMappings.put("name", "name");
    facebookFieldMappings.put("locale", "locale");
    facebookFieldMappings.put("age_range", "age_range");
    facebookFieldMappings.put("gender", "gender");

    List<String> permissions = new ArrayList<String>();
    permissions.add( "email" );
    permissions.add("public_profile");
    WebView webView = (WebView) findViewById(R.id.webView);
    Backendless.UserService.loginWithFacebook(this, webView, facebookFieldMappings, permissions, new AsyncCallback<BackendlessUser>() {
      @Override
      public void handleResponse(BackendlessUser backendlessUser) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
      }

      @Override
      public void handleFault(BackendlessFault backendlessFault) {
        // failed to log in
      }
    });
  }

  public void onLoginWithTwitterButtonClicked()
  {
    Backendless.UserService.loginWithTwitter(LoginActivity.this, new SocialCallback<BackendlessUser>(LoginActivity.this) {
      @Override
      public void handleResponse(BackendlessUser backendlessUser) {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_facebook) {
      onLoginWithFacebookButtonClicked();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}