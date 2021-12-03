package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TilausVahvistusActivity extends AppCompatActivity {


    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();
    private final FirebaseDatabase dbRef = FirebaseDatabase.getInstance();
    private final DatabaseReference rootRef = dbRef.getReference();

    private TextView tOtsikko;
    private TextView tOsoite;
    private TextView tNimi;
    private TextView tNro;
    private TextView tSposti;
    private TextView tPaivat;
    //private TextView tOrderID;
    private TextView goChat1;
    private TextView goChat2;
    private TextView goChat3;
    private ImageView tImage;
    private ImageView tBack;

    private String nimi;
    private String osoite;
    private String otsikko;
    private String nro;
    private String sposti;
    //private String orderID;
    private ArrayList<String> paivat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilaus_vahvistus);

        paivat = new ArrayList<String>();
        tSposti = findViewById(R.id.textViewTilausSahkoposti);
        tNimi = findViewById(R.id.textViewTilausMyyjä);
        tOsoite = findViewById(R.id.textViewTilausOsoite);
        tNro = findViewById(R.id.textViewTilausPuhNro);
        tOtsikko = findViewById(R.id.textViewTilausOtsikko);
        tPaivat = findViewById(R.id.textViewTilausPaivat);
        //tOrderID = findViewById(R.id.textViewTilausNumero);
        goChat1 = findViewById(R.id.textViewTilausMyyjä);
        goChat2 = findViewById(R.id.textViewTilausSahkoposti);
        goChat3 = findViewById(R.id.textViewTilausPuhNro);

        tImage = findViewById(R.id.profiiliKuva);
        tBack = findViewById(R.id.goBack);

        Intent onnistuiIntent = getIntent();
        sposti = onnistuiIntent.getStringExtra("vuokraPosti");
        nimi = onnistuiIntent.getStringExtra("vuokranantaja");
        otsikko = onnistuiIntent.getStringExtra("otsikko");
        nro = onnistuiIntent.getStringExtra("vuokraNro");
        osoite = onnistuiIntent.getStringExtra("osoite");
        //orderID = onnistuiIntent.getStringExtra("orderID");
        paivat = onnistuiIntent.getStringArrayListExtra("paivat");
        Log.d("TAG", paivat.toString());

        //tOrderID.setText("Tilausnumero " + orderID);
        tSposti.setText(sposti);
        tNimi.setText(nimi);
        tOsoite.setText("Mökin osoite on " + osoite);
        tOtsikko.setText("Mökki: " + otsikko);

        //TODO
        if(paivat.size() == 1) {  tPaivat.setText(paivat.get(0));  }
        else { tPaivat.setText(paivat.get(0) + " - " + paivat.get(paivat.size() - 1));  }


        /*
        if(nro.equals("123")) {
            tNro.setVisibility(View.GONE);
            Log.d("tag","if-yes");
        }
        else {
            tNro.setText(nro);
        }
         */
        storageRef.child("ProfilePictures/"+currentUser.getUid()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(tImage);
                })
                .addOnFailureListener(e -> {
                    tImage.setImageResource(R.mipmap.ic_launcher);
                });

        tBack.setOnClickListener(v -> {
            finish();
        });

        goChat1.setOnClickListener(v -> {
          Intent chatIntent = new Intent(this,ChatActivity.class);
          rootRef.child("Users").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                      Log.d("tag",postSnapshot.getValue().toString());
                      String mEmail = postSnapshot.child("sahkoposti").getValue().toString();
                      Log.d("tag",mEmail);
                      if(mEmail.equals(sposti)) {
                          Log.d("tag","equals");
                          chatIntent.putExtra("ID",postSnapshot.child("name").getValue().toString());
                          chatIntent.putExtra("name",nimi);
                          startActivity(chatIntent);
                          finish();
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
        });
        goChat2.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this,ChatActivity.class);
            rootRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Log.d("tag",postSnapshot.getValue().toString());
                        String mEmail = postSnapshot.child("sahkoposti").getValue().toString();
                        Log.d("tag",mEmail);
                        if(mEmail.equals(sposti)) {
                            Log.d("tag","equals");
                            chatIntent.putExtra("ID",postSnapshot.child("name").getValue().toString());
                            chatIntent.putExtra("name",nimi);
                            startActivity(chatIntent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        goChat3.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this,ChatActivity.class);
            rootRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Log.d("tag",postSnapshot.getValue().toString());
                        String mEmail = postSnapshot.child("sahkoposti").getValue().toString();
                        Log.d("tag",mEmail);
                        if(mEmail.equals(sposti)) {
                            Log.d("tag","equals");
                            chatIntent.putExtra("ID",postSnapshot.child("name").getValue().toString());
                            chatIntent.putExtra("name",nimi);
                            startActivity(chatIntent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    public void onClick_menu(View view) {
        PopupMenu popup = new PopupMenu(this, tImage);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.user:
                    Intent userIntent = new Intent(this,ProfiiliActivity.class);
                    startActivity(userIntent);
                    break;
                case R.id.chat:
                    startActivity(getIntent());
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
        if(mauth.getCurrentUser() != null) {
            popup.inflate(R.menu.menu_list);
            if (mauth.getCurrentUser().getDisplayName() != null) {
                popup.getMenu().findItem(R.id.user).setTitle(mauth.getCurrentUser().getDisplayName());
            } else {
                popup.getMenu().findItem(R.id.user).setTitle(mauth.getCurrentUser().getEmail());
            }
            popup.show();
        }
        else if(mauth.getCurrentUser() == null) {
            Intent kirjauduIntent = new Intent(this, LoginActivity.class);
            startActivity(kirjauduIntent);
            finish();
        }
    }

    public void etusivulle(View view) {
        Intent i = new Intent(this, Mokki_List.class);
        startActivity(i);
        finish();
    }
}