package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import dk.mobilepay.sdk.Country;
import dk.mobilepay.sdk.MobilePay;
import dk.mobilepay.sdk.ResultCallback;
import dk.mobilepay.sdk.model.FailureResult;
import dk.mobilepay.sdk.model.Payment;
import dk.mobilepay.sdk.model.SuccessResult;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {

    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();
    private final FirebaseDatabase dbRef = FirebaseDatabase.getInstance();
    private final DatabaseReference rootRef = dbRef.getReference();
    private ValueEventListener dateListener;
    private final DatabaseReference vuokraajaRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private final DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("Vuokralla olevat mökit/");
    private TextView korttiBtn;
    private TextView mobilepayBtn;
    private TextView nameText;
    private TextView hintaText;
    private TextView paivaText;
    private TextView lisaText;
    private TextView otsikkoText;
    private TextView osoiteText;
    private ImageView takaisinBtn;
    private ImageView menuImage;

    private int summa;
    private String vuokraajaID;
    private String vuokraaja;
    private String vuokraOtsikko;
    private ArrayList<String> paivat;
    private ArrayList<String> dbpaivat;
    private String osoite;
    private String image;
    private String vuokraajaNro;
    private String vuokraajaPosti;
    private String asiakasNro;
    private String asiakasPosti;
    private String mokkiID;
    private String otsikkoID;
    private int summaInt;

    private  Map<String, Object> postMap;
    private Map<String, Object> invoiceDetails = new HashMap<>();
    private String messagePushID;

    private boolean isMobilePayInstalled;
    int MOBILEPAY_PAYMENT_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mobilepayBtn = findViewById(R.id.toggleMobilepay);
        korttiBtn = findViewById(R.id.toggleKortti);

        hintaText = findViewById(R.id.textViewHintaMaarat);
        nameText = findViewById(R.id.textViewVuokraajaNimi);
        otsikkoText = findViewById(R.id.textViewMökki);
        paivaText = findViewById(R.id.textViewPaivaMaarat);
        osoiteText = findViewById(R.id.textViewOsoiteMaarat);
        takaisinBtn = findViewById(R.id.goBack);
        menuImage = findViewById(R.id.profiiliKuva);

        Intent fromMokki = getIntent();
        vuokraaja = fromMokki.getStringExtra("name");
        vuokraajaID = fromMokki.getStringExtra("id");
        summa = fromMokki.getIntExtra("hinta",0);
        vuokraOtsikko = fromMokki.getStringExtra("otsikko");
        osoite = fromMokki.getStringExtra("osoite");
        paivat = fromMokki.getStringArrayListExtra("paivat");
        dbpaivat = fromMokki.getStringArrayListExtra("dbpaivat");
        image = fromMokki.getStringExtra("image");
        otsikkoID = fromMokki.getStringExtra("otsikkoID");
        Log.d("tag", paivat.toString());
        Log.d("tag", dbpaivat.toString());
        Log.d("tag", otsikkoID);

        summaInt = paivat.size() * summa;
        hintaText.setText(String.valueOf(summaInt));
        nameText.setText(vuokraaja);
        otsikkoText.setText(vuokraOtsikko);

        if(paivat.size() == 1) {  paivaText.setText(paivat.get(0));  }
        else { paivaText.setText(paivat.get(0) + " - " + paivat.get(paivat.size() - 1));  }

        osoiteText.setText(osoite);

        MobilePay.getInstance().init("APPFI0000000000", Country.FINLAND);
        isMobilePayInstalled =  MobilePay.getInstance().isMobilePayInstalled(getApplicationContext());
        MOBILEPAY_PAYMENT_REQUEST_CODE = 1337;

        mobilepayBtn.setOnClickListener(v -> {
            payNow();
        });

        //paivat.clear();

        korttiBtn.setOnClickListener(v -> {

            //TESTIMIELINEN MAKSUHOMMA ETTEI TARVI MOBILEPAYLLA SÄÄTÄÄ
            Log.d("tag","tasksuccessfull");
            Toast.makeText(CheckoutActivity.this, "Maksu onnistui!", Toast.LENGTH_SHORT).show();
            Intent onnistuiIntent = new Intent(getApplicationContext(),TilausVahvistusActivity.class);
            //onnistuiIntent.putExtra("orderID",messagePushID);
            onnistuiIntent.putExtra("vuokranantaja",vuokraaja);
            onnistuiIntent.putExtra("otsikko",vuokraOtsikko);
            onnistuiIntent.putExtra("osoite",osoite);
            onnistuiIntent.putExtra("vuokraNro",vuokraajaNro);
            onnistuiIntent.putExtra("vuokraPosti",vuokraajaPosti);
            onnistuiIntent.putExtra("image",image);
            onnistuiIntent.putStringArrayListExtra("paivat",paivat);

            HashMap hashMap = new HashMap();
            hashMap.put("mDates", dbpaivat.toString());
            Log.d("TAG", "hashmap = " + hashMap);
            Log.d("TAG", "mokkiID = " + mokkiID);
            datesRef.child(otsikkoID).updateChildren(hashMap);

            startActivity(onnistuiIntent);
            finish();
            /*
            dateListener = datesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if(postSnapshot.child("osoite").getValue().toString().equals(osoite)) {
                            Log.d("snaspsoht",postSnapshot.child("osoite").getValue().toString());
                            String dateArray;
                            //dateArray = postSnapshot.child("mDates").getValue().toString();
                            String paivatArray = paivat.toString();
                            Log.d("paivararya",paivatArray);
                            dateArray = postSnapshot.child("mDates").getValue().toString();
                            Log.d("prefor", String.valueOf(dateArray));
                            paivatArray = paivatArray.replace("[","");
                            paivatArray = paivatArray.replace("]","");
                            dateArray = dateArray.replace("[","");
                            dateArray = dateArray.replace("]","");
                            String[] splitThisDate = paivatArray.split(",");
                            String [] splitDbDate = dateArray.split(",");

                            for(int a = 0; a < splitThisDate.length; a++) {
                                Log.d("tag",splitThisDate[a]);
                                for (int i = 0; i < splitDbDate.length; i++) {
                                    Log.d("tag",splitDbDate[i]);
                                    if(splitDbDate[i].matches(splitThisDate[a]+":1,")) {
                                        Log.d("if","if");
                                        splitDbDate[i] = splitDbDate[i].replace(splitThisDate[a]+":1,","VARATTU");
                                    }
                                }
                            }
                            Log.d("tag", Arrays.toString(splitDbDate));
                            MokkiItem mokkiItem = postSnapshot.getValue(MokkiItem.class);
                            mokkiItem.setDates(Arrays.toString(splitDbDate));
                            datesRef.child(postSnapshot.getKey()).setValue(mokkiItem);
                            datesRef.removeEventListener(dateListener);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            */

        });


        takaisinBtn.setOnClickListener(v -> {
            finish();
        });


        storageRef.child("ProfilePictures/"+currentUser.getUid()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(menuImage);
                })
                .addOnFailureListener(e -> {
                    menuImage.setImageResource(R.mipmap.ic_launcher);
                });
    }

    public void onClick_menu(View view) {
        PopupMenu popup = new PopupMenu(this, menuImage);
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

    public interface MyCallback {
        void onCallback(String value1, String value2, String value3, String value4);
    }

    public void readData(MyCallback myCallback) {
        vuokraajaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value1 = dataSnapshot.child(vuokraajaID).child("sahkoposti").getValue().toString();
                String value2 = dataSnapshot.child(vuokraajaID).child("numero").getValue().toString();
                String value3 = dataSnapshot.child(currentUser.getUid()).child("sahkoposti").getValue().toString();
                String value4 = dataSnapshot.child(currentUser.getUid()).child("numero").getValue().toString();
                myCallback.onCallback(value1,value2, value3, value4);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*protected void onStop() {
        super.onStop();
        finish();
    }*/

    private void payNow() {
        messagePushID = rootRef.child("Invoices").child(currentUser.getUid()).child(vuokraajaID).push().getKey();

        if (isMobilePayInstalled) {
            Payment payment = new Payment();
            payment.setProductPrice(BigDecimal.valueOf(summaInt));
            payment.setOrderId(messagePushID);
            Log.d("tag",vuokraajaID);
            Intent paymentIntent = MobilePay.getInstance().createPaymentIntent(payment);
            startActivityForResult(paymentIntent, MOBILEPAY_PAYMENT_REQUEST_CODE);

        }
        else {
            Intent intent = MobilePay.getInstance().createDownloadMobilePayIntent(getApplicationContext());
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Tag","onactivityresult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILEPAY_PAYMENT_REQUEST_CODE) {
            Log.d("Tag","code matches");
            // The request code matches our MobilePay Intent
            MobilePay.getInstance().handleResult(resultCode, data, new ResultCallback() {
                @Override
                public void onSuccess(SuccessResult result) {
                    Log.d("tag","onsuccess");
                    readData(new MyCallback() {
                        @Override
                        public void onCallback(String value1, String value2, String value3, String value4) {
                            vuokraajaPosti = value1;
                            vuokraajaNro = value2;
                            asiakasPosti = value3;
                            asiakasNro = value4;
                            rootRef.child("Vuokralla olevat mökit").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        if (postSnapshot.child("osoite").getValue().equals(osoite)) {
                                            mokkiID = postSnapshot.child("otsikkoID").getValue().toString();
                                            Log.d("tag", vuokraajaPosti);
                                            Log.d("tag", vuokraajaNro);
                                            Invoices newInvoice = new Invoices(messagePushID, vuokraOtsikko, osoite, mokkiID, image, currentUser.getUid(), asiakasNro, asiakasPosti, vuokraaja, vuokraajaID, vuokraajaPosti, vuokraajaNro, paivat, summa);
                                            postMap = newInvoice.toMap();
                                        }
                                    }
                                            invoiceDetails.put(currentUser.getUid() + "/" +  "Omat vuokraukset" + "/" + messagePushID, postMap);
                                            invoiceDetails.put(vuokraajaID + "/" + "Tilaukset" + "/" + messagePushID, postMap);
                                            rootRef.child("Invoices").updateChildren(invoiceDetails).addOnCompleteListener(new OnCompleteListener() {

                                                @Override
                                                public void onComplete(@NonNull Task task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Log.d("tag","tasksuccessfull");
                                                        Toast.makeText(CheckoutActivity.this, "Maksu onnistui!", Toast.LENGTH_SHORT).show();
                                                        Intent onnistuiIntent = new Intent(getApplicationContext(),TilausVahvistusActivity.class);
                                                        onnistuiIntent.putExtra("orderID",messagePushID);
                                                        onnistuiIntent.putExtra("vuokranantaja",vuokraaja);
                                                        onnistuiIntent.putExtra("otsikko",vuokraOtsikko);
                                                        onnistuiIntent.putExtra("osoite",osoite);
                                                        onnistuiIntent.putExtra("vuokraNro",vuokraajaNro);
                                                        onnistuiIntent.putExtra("vuokraPosti",vuokraajaPosti);
                                                        onnistuiIntent.putExtra("image",image);
                                                        onnistuiIntent.putStringArrayListExtra("paivat",paivat);

                                                        HashMap hashMap = new HashMap();
                                                        hashMap.put("mDates", dbpaivat.toString());
                                                        Log.d("TAG", "hashmap = " + hashMap);
                                                        Log.d("TAG", "mokkiID = " + mokkiID);
                                                        datesRef.child(otsikkoID).updateChildren(hashMap);

                                                        startActivity(onnistuiIntent);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(CheckoutActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                }
                //TODO: puhelinnumeron lisäys profiiliin
                @Override
                public void onFailure(FailureResult result) {
                    // The payment failed - show an appropriate error message to the user. Consult the MobilePay class documentation for possible error codes.
                    Toast.makeText(getApplicationContext(),result.getErrorMessage(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel(String orderId) {
                    Toast.makeText(getApplicationContext(),"OrderID: " + orderId + " was cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void onClick_Usermenu(View view) {
        PopupMenu popup = new PopupMenu(this, menuImage);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.user:
                    Intent profiiliIntent = new Intent(this,ProfiiliActivity.class);
                    startActivity(profiiliIntent);
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
    }

}