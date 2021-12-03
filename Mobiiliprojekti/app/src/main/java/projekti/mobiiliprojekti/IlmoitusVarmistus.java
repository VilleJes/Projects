package projekti.mobiiliprojekti;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class IlmoitusVarmistus extends AppCompatActivity {

    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();

    private String eOtsikko;
    private int eHinta;
    private String eOsoite;
    private String eHuoneet;
    private int eNeliot;
    private String eLammitys;
    private String eVesi;
    private String eSauna;
    private String eKuvaus;
    private String eOtsikkoID;
    private String eVuokraaja;
    private String MokkiKuva;
    private String eID;
    private String UID;
    private List<String> varausDates;
    //private String varausDates;
    private String dates;

    private boolean deleteImage;

    private ImageView sImageUpload;

    private DatabaseReference dbMokki;
    private DatabaseReference dbVarmistamatonMokki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilmoitus_varmistus);

        deleteImage = false;

        dbMokki = FirebaseDatabase.getInstance().getReference("Vuokralla olevat mökit/");
        dbVarmistamatonMokki = FirebaseDatabase.getInstance().getReference("Varmistamattomat mökit/" + currentUser.getUid());

        TextView bTakaisinIlmoitukseen = findViewById(R.id.bTakaisinIlmoitukseen);
        TextView bAsetaVuokralle = findViewById(R.id.bAsetaVuokralle);


        Intent varmistaIntent = getIntent();
        eOtsikko = varmistaIntent.getStringExtra("eOtsikko");
        eHinta = varmistaIntent.getIntExtra("eHinta",0);
        eOsoite = varmistaIntent.getStringExtra("eOsoite");
        eHuoneet = varmistaIntent.getStringExtra("eHuoneet");
        eNeliot = varmistaIntent.getIntExtra("eNeliot",0);
        eLammitys = varmistaIntent.getStringExtra("eLammitys");
        eVesi = varmistaIntent.getStringExtra("eVesi");
        eSauna = varmistaIntent.getStringExtra("eSauna");
        eKuvaus = varmistaIntent.getStringExtra("eKuvaus");
        UID = varmistaIntent.getStringExtra("eUID");
       //varausDates = varmistaIntent.getStringExtra("dates");
        varausDates = (ArrayList<String>)getIntent().getSerializableExtra("dates");
        Log.d("listat", String.valueOf(varausDates));



        TextView sOtsikko = findViewById(R.id.sOtsikko);
        TextView sHinta = findViewById(R.id.sHinta);
        TextView sOsoite = findViewById(R.id.sOsoite);
        TextView sHuoneet = findViewById(R.id.sHuoneet);
        TextView sNeliot = findViewById(R.id.sNeliot);
        TextView sLammitys = findViewById(R.id.sLammitys);
        TextView sVesi = findViewById(R.id.sVesi);
        TextView sSauna = findViewById(R.id.sSauna);
        TextView sDates = findViewById(R.id.textViewDates);
        TextView sKuvaus = findViewById(R.id.sKuvaus);
        sImageUpload = findViewById(R.id.ImageViewUpload);

        sOtsikko.setText(eOtsikko);
        sHinta.setText(String.valueOf(eHinta));
        sOsoite.setText(eOsoite);
        sHuoneet.setText(eHuoneet);
        sNeliot.setText(String.valueOf(eNeliot));
        sLammitys.setText(eLammitys);
        sVesi.setText(eVesi);
        sSauna.setText(eSauna);
        String tmpStr = varausDates.get(0);
        tmpStr = tmpStr.substring(0, tmpStr.length() - 2);
        String tmpStr2 = varausDates.get(varausDates.size() - 1);
        tmpStr2 = tmpStr2.substring(0, tmpStr2.length() - 2);
        sDates.setText(tmpStr + " - " + tmpStr2);
        sKuvaus.setText(eKuvaus);

        dates = varausDates.toString();


        storageRef.child("Mökkien kuvia/"  + currentUser.getUid() + UID).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).fit().centerCrop().into(sImageUpload);
                    MokkiKuva = uri.toString();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),"Ei lisättyä kuvaa",Toast.LENGTH_LONG).show();
                });


        bTakaisinIlmoitukseen.setOnClickListener(view -> {
            Intent takaisinIlmoitukseen = new Intent(this, LaitaVuokralle.class);

            takaisinIlmoitukseen.putExtra("eOtsikko",eOtsikko);
            takaisinIlmoitukseen.putExtra("eHinta", eHinta);
            takaisinIlmoitukseen.putExtra("eOsoite", eOsoite);
            takaisinIlmoitukseen.putExtra("eHuoneet",eHuoneet);
            takaisinIlmoitukseen.putExtra("eNeliot",eNeliot);
            takaisinIlmoitukseen.putExtra("eLammitys",eLammitys);
            takaisinIlmoitukseen.putExtra("eVesi",eVesi);
            takaisinIlmoitukseen.putExtra("eSauna",eSauna);
            takaisinIlmoitukseen.putExtra("eKuvaus",eKuvaus);
            //takaisinIlmoitukseen.putExtra("dates", String.valueOf(varausDates));

            deleteMokkiKuva();

            startActivity(takaisinIlmoitukseen);
        });

        bAsetaVuokralle.setOnClickListener(v -> addMokki());

    }

    private void deleteMokkiKuva()
    {
        storageRef.child("Mökkien kuvia/"  + currentUser.getUid() + UID).delete();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(deleteImage != true){
            deleteMokkiKuva();
        }
    }

    private void addMokki()
    {
        deleteImage = true;
        eVuokraaja = currentUser.getDisplayName();
        eID = currentUser.getUid();
        eOtsikkoID = UID;

        eOtsikkoID = dbMokki.push().getKey();

        MokkiItem mokki = new MokkiItem(MokkiKuva, eOtsikko, eHinta, eOsoite, eHuoneet, eNeliot, eLammitys,
                eVesi, eSauna, eKuvaus, eOtsikkoID, eVuokraaja, eID, dates);

        dbMokki.child(eOtsikkoID).setValue(mokki);

        dbVarmistamatonMokki.removeValue();

        Toast.makeText(this, "Mökki lisätty vuokralle", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Mokki_List.class);
        startActivity(intent);
    }


}