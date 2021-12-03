package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;

public class ProfiiliActivity extends AppCompatActivity {
    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();
    private final StorageReference profileRef = storageRef.child("ProfilePictures");
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    private ImageView profiiliKuva;

    TextView txtNimi;
    TextView txtEmail;
    TextView txtSalasana;
    ImageView imageView;
    TextView lataaButton;
    TextView poistaTiliBtn;
    TextView btnPuhelin;

    ImageView goBack;

    boolean PASSWORD_CHANGE = false;
    boolean EMAIL_CHANGE = false;
    boolean DELETE_ACCOUNT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiili);
        txtNimi = findViewById(R.id.textNiminayta);
        txtEmail = findViewById(R.id.textEmail);
        txtSalasana = findViewById(R.id.textPass);
        lataaButton = findViewById(R.id.buttonLataa);
        profiiliKuva = findViewById(R.id.profiiliKuva);
        poistaTiliBtn = findViewById(R.id.buttonPoista);

        imageView = findViewById(R.id.imageAvatar);
        lataaButton.setVisibility(View.GONE);
        txtEmail.setText(currentUser.getEmail());
        txtNimi.setText(currentUser.getDisplayName());
        goBack = findViewById(R.id.goBack);
        btnPuhelin = findViewById(R.id.buttonPuhelin);


        if(currentUser.getDisplayName() != null) {
            txtNimi.setText("Hei " + currentUser.getDisplayName());
        }
        else {
            txtNimi.setText("Hei " + currentUser.getEmail());
        }
        storageRef.child("ProfilePictures/"+currentUser.getUid()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                        Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(imageView);
                        Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(profiiliKuva);
                })
            .addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(),"Ei lisättyä kuvaa",Toast.LENGTH_LONG).show();
                imageView.setImageResource(R.mipmap.ic_launcher);
                profiiliKuva.setImageResource(R.mipmap.ic_launcher);
            });

        goBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(this,Mokki_List.class);
            startActivity(backIntent);
            finish();
        });

        txtEmail.setOnClickListener(v -> {
            EMAIL_CHANGE = true;
            reAuthenticate();
        });
        txtSalasana.setOnClickListener(v -> {
            PASSWORD_CHANGE = true;
            reAuthenticate();
        });
        imageView.setOnClickListener(v -> selectImage());
        lataaButton.setOnClickListener(v -> {
            uploadImage();
            lataaButton.setVisibility(View.GONE);
        });

        poistaTiliBtn.setOnClickListener(v -> {
            DELETE_ACCOUNT = true;
            reAuthenticate();
        });

        btnPuhelin.setOnClickListener(v -> {
            syotaNumero();
        });
    }

    private void syotaNumero() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setMessage("Syötä numerosi: ");
        alert.setTitle("Puhelinnumeron muutos");

        alert.setView(edittext);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää numero", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbRef.child("Users").child(currentUser.getUid()).child("numero").setValue(edittext.getText().toString());
                Toast.makeText(getApplicationContext(), "Puhelinnumero lisätty!", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Palaa takaisin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Valitse kuva..."),PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(getContentResolver().openInputStream(filePath), null, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;
                    if (imageHeight >= 1) {
                        if (imageWidth >= 1) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                            imageView.setImageBitmap(bitmap);
                            lataaButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Virhe kuvan kanssa, syötä toinen kuva", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Virhe kuvan kanssa, syötä toinen kuva", Toast.LENGTH_SHORT).show();
                    }
                Log.e("TAg", String.valueOf(imageWidth));
                Log.e("Tag", String.valueOf(imageHeight));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void uploadImage() {
        if (getfileExtension(filePath).equals("jpg") || getfileExtension(filePath).equals("png") || getfileExtension(filePath).equals("jpeg")) {
            Log.e("Tag", getfileExtension(filePath));
            if (filePath != null) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Lataus käynnissä...");
                progressDialog.show();

                profileRef.child("/" + currentUser.getUid()).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        storageRef.child("ProfilePictures/"+currentUser.getUid()).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(imageView);
                                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(profiiliKuva);
                                });
                        Toast.makeText(getApplicationContext(), "Profiilikuva päivitetty!", Toast.LENGTH_LONG).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Virhe kuvan päivityksessä", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressDialog.setMessage("Ladattu " + (int) progress + "%");
                            }
                        });
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Virheellinen tiedostomuoto",Toast.LENGTH_SHORT).show();
        }
    }

    private void setEmail() {
        Log.e("Tag","setEmail");
        AlertDialog alert = new AlertDialog.Builder(this).create();
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alert.setMessage("Syötä uusi sähköposti:");
        alert.setTitle("Sähköpostin muutos");

        alert.setView(edittext);

        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Muuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentUser.updateEmail(edittext.getText().toString());
                Toast.makeText(getApplicationContext(),"Email updated!",Toast.LENGTH_SHORT).show();
                EMAIL_CHANGE = false;
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE,"Palaa takaisin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
        Log.e("Tag","FinishedEmail");

    }
    private void setPass() {
        Log.e("Tag","setPass");
        AlertDialog alert = new AlertDialog.Builder(this).create();
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setMessage("Syötä uusi salasana:");
        alert.setTitle("Salasanan muutos");

        alert.setView(edittext);

        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Muuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentUser.updatePassword(edittext.getText().toString());
                Toast.makeText(getApplicationContext(),"Password updated!",Toast.LENGTH_SHORT).show();
                PASSWORD_CHANGE = false;
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE,"Palaa takaisin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
        Log.e("Tag","FinishedPass");
    }

    private void deleteAccount() {
        profileRef.child(currentUser.getUid()).delete();
        dbRef.child("Users").child(currentUser.getUid()).removeValue();
        dbRef.child("Contacts").child(currentUser.getUid()).removeValue();
        dbRef.child("Messages").child(currentUser.getUid()).removeValue();
        dbRef.child("Vuokralla olevat mökit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = (String) postSnapshot.child("vuokraajaID").getValue();
                    String location = postSnapshot.getKey();
                    if(String.valueOf(key).equals(currentUser.getUid())) {
                        dbRef.child("Vuokralla olevat mökit").child(location).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        currentUser.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Tili poistettu.", Toast.LENGTH_SHORT).show();
                update();
            }
        });
    }

    private void update() {
        Intent startActivity2 = new Intent(this,LoginActivity.class);
        startActivity(startActivity2);
        finish();
    }


    public void onClick_Usermenu(View view) {
        PopupMenu popup = new PopupMenu(this, profiiliKuva);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.user:
                    finish();
                    startActivity(getIntent());
                    break;
                case R.id.chat:
                    Intent chatIntent = new Intent(this,ChatActivity.class);
                    startActivity(chatIntent);
                    finish();
                    break;
                case R.id.logout:
                    mauth.signOut();
                    Intent signOutIntent = new Intent(this, LoginActivity.class);
                    startActivity(signOutIntent);
                    finish();
                    break;
            }
            return false;
        });
        if(currentUser != null) {
            popup.inflate(R.menu.menu_list);
            if (currentUser.getDisplayName() != null) {
                popup.getMenu().findItem(R.id.user).setTitle(currentUser.getDisplayName());
            } else {
                popup.getMenu().findItem(R.id.user).setTitle(currentUser.getEmail());
            }
            popup.show();
        }
        else if(currentUser == null) {
            Intent kirjauduIntent = new Intent(this, LoginActivity.class);
            startActivity(kirjauduIntent);
            finish();
        }
    }

    private void reAuthenticate() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        AlertDialog alert = new AlertDialog.Builder(this).create();
        final EditText newEmail = new EditText(this);
        newEmail.setHint("Sähköposti");
        newEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(newEmail);
        final EditText newPass = new EditText(this);
        newPass.setHint("Salasana");
        newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPass);

        alert.setMessage("Varmista kirjautumalla uudestaan");
        alert.setTitle("Varmistus");

        alert.setView(layout);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Tunnistaudu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(newEmail.getText().toString(),newPass.getText().toString());
                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.e("Tag", "User re-authenticated");
                            if(PASSWORD_CHANGE) {
                                setPass();
                                PASSWORD_CHANGE = false;
                            }
                            else if (EMAIL_CHANGE) {
                                setEmail();
                                EMAIL_CHANGE = false;
                            }
                            else if(DELETE_ACCOUNT) {
                                deleteAccount();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Wrong email / password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,"Palaa takaisin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }
}