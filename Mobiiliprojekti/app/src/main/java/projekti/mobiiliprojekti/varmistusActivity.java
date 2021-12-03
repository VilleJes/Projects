package projekti.mobiiliprojekti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class varmistusActivity extends AppCompatActivity  {
    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private TextView checkBtn;
    private int count = 0;
    private final Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varmistus);
        checkBtn = findViewById(R.id.checkBtn);
        Log.e("Tag","oncreate");
        checkBtn.setOnClickListener(View -> {
            backtoLogin();
        });
        content();
    }

    public void backtoLogin() {
        handler.removeCallbacks(runnable);
        Intent setIntent = new Intent(varmistusActivity.this, LoginActivity.class);
        startActivity(setIntent);
        finish();
    }

    public boolean checkVerificationStatus() {
        currentUser.reload();
        Log.e("Tag", "Verifying");
        Log.e("Tag", String.valueOf(currentUser.isEmailVerified()));
        if (currentUser.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Email verification complete!", Toast.LENGTH_LONG).show();
            Intent setIntent = new Intent(varmistusActivity.this, LoginActivity.class);
            startActivity(setIntent);
            finish();
            return true;
        }
        return false;
    }

    //Ajastus jolla varmennus tsekataan 2 sekunnin välein
    public void content() {
        count++;
        refresh(2000);
    }
    private void refresh(int milliseconds) {

        runnable = () -> content();
        handler.postDelayed(runnable, milliseconds);
        if(checkVerificationStatus()) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        Intent setIntent = new Intent(this, LoginActivity.class);
        startActivity(setIntent);
        finish();
    }
}