package projekti.mobiiliprojekti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    protected FirebaseAuth mAuth;
    protected FirebaseUser currentUser;
    GoogleSignInClient mGoogleSignInClient;
    private EditText editEmail, editPassword;
    private TextView textTervehdys, textKirjautumatta, textUnohdus;
    private Intent mokkiIntent, varmistusIntent;
    private String email;

    private DatabaseReference dbRef;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edittext_email);
        editPassword = findViewById(R.id.edittext_password);
        textTervehdys = findViewById(R.id.textview_tervehdys);
        textKirjautumatta = findViewById(R.id.textViewKirjautumatta);
        textUnohdus = findViewById(R.id.textViewUnohdus);
        varmistusIntent = new Intent(this, varmistusActivity.class);
        mokkiIntent = new Intent(this, Mokki_List.class);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        textKirjautumatta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser != null) {  mAuth.signOut(); }
                startActivity(mokkiIntent);
                finish();
            }
        });
        textUnohdus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEmail();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "creatUserWithGoogle:success");
                            startActivity(mokkiIntent);
                            finish();
                        } else {
                            Log.d("TAG", "creatUserWithGoogle:failed");
                        }
                    }
                });
            }

    private void resetEmail() {
        Log.e("Tag", "resetEmail");
        AlertDialog alert = new AlertDialog.Builder(this).create();
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alert.setMessage("Syötä sähköpostisi:");
        alert.setTitle("Salasanan resetointi");

        alert.setView(edittext);

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Muuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                email = edittext.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alert.dismiss();
                            Toast.makeText(getApplicationContext(), "Tarkista sähköpostisi!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Jokin meni pieleen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Peruuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });

        alert.show();
        Log.e("Tag", "FinishedPass");
    }

    public void clickGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void clickLuoTunnus(View view) {

        if (currentUser != null) {
            Log.d("TAG", "kirjaudutaan ulos ekaksi ");
            mAuth.signOut();
            Intent i = new Intent(this, LuoTunnus.class);
            startActivity(i);
        } else {
            Log.d("TAG", "null käyttäjä, luodaan uusi");
            Intent i = new Intent(this, LuoTunnus.class);
            startActivity(i);
        }
    }

    public void clickKirjaudu(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        //updateUI(currentUser);

        if (email.matches("") || password.matches("")) {
            Log.d("TAG", "tyhjä salis tai tyhjä email");
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "creatUserWithEmail:success");
                        if (currentUser != null) {
                            if (currentUser.isEmailVerified()) {
                                Log.d("TAG", "emaili on vahvistettu");
                                startActivity(mokkiIntent);
                                finish();
                            } else {
                                Log.d("TAG", "emaili ei ole vahvistettu");
                                startActivity(varmistusIntent);
                                finish();
                            }
                        } else {
                            Log.d("TAG", "user ei null ja emaili on vahvistettu");
                            startActivity(mokkiIntent);
                            finish();
                        }
                    } else {
                        Log.d("TAG", "signUserWithEmail:failed");
                        Toast.makeText(getApplicationContext(),
                                "Tarkista Sähköposti tai Salasana", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    /*private void updateUI(FirebaseUser user) {
        if (currentUser != null) {
            currentUser.reload();
            Log.d("TAG", "DEBUGuser ei ole null");

            //tarkistetaan onko käyttäjä email/passu vai google useri
            String strProvider = FirebaseAuth.getInstance().
                    getAccessToken(false).getResult().getSignInProvider();
            if (strProvider.equals("password")) {

                textTervehdys.setText("Terve " + currentUser.getDisplayName());
                Log.d("TAG", "email/passu käyttäjä");
                editEmail.setText(currentUser.getEmail());

            } else if (strProvider.equals("google.com")) {
                Log.d("TAG", "google käyttäjä");
                textTervehdys.setText("Terve " + currentUser.getDisplayName() + "\n(google käyttäjä)");
                editEmail.setText("PAINA GOOGLE NAPPIA");
            }
            if (!currentUser.isEmailVerified()) {
                Log.d("TAG", "DEBUGemaili ei ole vahvistettu");
            }
        } else {
            Log.d("TAG", "DEBUGuser on null");
        }
    }*/
}