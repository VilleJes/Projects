package projekti.mobiiliprojekti;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LuoTunnus extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected FirebaseUser currentUser;
    private EditText editEmail, editPassword, editConfirmPassword;
    private TextView tietosuojaText;
    private String tietosuojaString;
    private CheckBox chkSuoja;
    private boolean IS_SUOJA_READ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luotunnus_layout);

        Log.d("TAG", "MOI LUOTUNNUS ACTIVI");
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser  = mAuth.getCurrentUser();
        IS_SUOJA_READ = false;

        chkSuoja = findViewById(R.id.checkBoxSuoja);
        tietosuojaText = findViewById(R.id.tietosuojaText);
        tietosuojaString = getString(R.string.tietosuojaContext);

        tietosuojaText.setOnClickListener(view -> {
            tietosuojaStart();
        });

        chkSuoja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(IS_SUOJA_READ == false) {
                    chkSuoja.setChecked(false);
                    tietosuojaText.setTextColor(Color.RED);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void tietosuojaStart() {
        LayoutInflater inflater= LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.tietosuoja_layout, null);

        TextView textview= view.findViewById(R.id.textmsg);
        textview.setText(tietosuojaString);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Tietosuojalauseke");
        alertDialog.setMessage(tietosuojaString);
        alertDialog.setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();
        IS_SUOJA_READ = true;
        tietosuojaText.setTextColor(Color.GRAY);
    }

    public void clickSignUp(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Salasanat eivät täsmää", Toast.LENGTH_SHORT).show();

        } else if (email.matches("") || password.matches("")) {
            Log.d("TAG", "virhe");

        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())  {
                        Toast.makeText(getApplicationContext(), "Käyttäjä luotu", Toast.LENGTH_SHORT).show();
                        currentUser = mAuth.getCurrentUser();
                        currentUser.reload();

                        //jos email/passu käyttäjällä ei ole nimeä, splitataan displayNameksi sähköpostin nimi
                        if(currentUser.getDisplayName() == null) {
                            String cutattu = currentUser.getEmail();
                            cutattu = cutattu.split("@")[0];
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(cutattu).build();
                            currentUser.updateProfile(profileUpdate);
                        }

                        if(!currentUser.isEmailVerified()) {
                            vahvistaEmail();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Käyttäjän luonnissa virhe", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void vahvistaEmail() {
        currentUser = mAuth.getCurrentUser();
        Intent varmistusIntent = new Intent(this, varmistusActivity.class);
        if(null != currentUser) {
            currentUser.reload();
            if(currentUser.getEmail()!=null) {
                if(!currentUser.isEmailVerified()) {
                    /* Send Verification Email */
                    currentUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            /* Check Success */
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "Verification Email Sent To: " + currentUser.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(varmistusIntent);
                                finish();
                            } else {
                                Log.e("TAG", "sendEmailVerification", task.getException());
                                Toast.makeText(getApplicationContext(),
                                        "Failed To Send Verification Email!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(currentUser.isEmailVerified()) {
                    Toast.makeText(getApplicationContext(), "Nykyinen käyttäjä on vahvistettu", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}