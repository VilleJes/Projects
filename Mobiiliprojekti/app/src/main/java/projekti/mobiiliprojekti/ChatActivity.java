package projekti.mobiiliprojekti;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChatsFragment extends Fragment {
    private View PrivateChatsView;
    private RecyclerView chatsList;

    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.messages_drawer, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return PrivateChatsView;
    }

    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profileImage;
        TextView userName;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}

public class ChatActivity extends AppCompatActivity
{
    private String messageReceiverID, messageReceiverImage, messageSenderID;

    private TextView userName;
    private DrawerLayout chatDrawer;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, ContactRef;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference strRef = storage.getReference();
    private DatabaseReference UsersRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;
    private ImageView takaisinBtn;
    private ImageView lisaaViestejaBtn;
    private ImageView chatKuva;
    private ImageView backKuva;

    private final List<Messages> messagesList = new ArrayList<>();
    private final List<Contacts> contactsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager2;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private RecyclerView userContactsList;
    private String id;
    private String fromChatId;
    private String receiverName;
    private TextView receiver;
    private RelativeLayout chatLayout;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private final int count = 0;
    private final SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
    private final SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");

    private String saveCurrentTime, saveCurrentDate;
    private String getMessageReceiverImage;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("Tag","onCreate");

        Intent chatIntent = getIntent();
        id = "";
        fromChatId = "";
        id = chatIntent.getStringExtra("ID");
        fromChatId = chatIntent.getStringExtra("visit_user_id");
        receiverName = chatIntent.getStringExtra("name");

        if(receiverName == null) {
            receiverName = chatIntent.getStringExtra("visit_user_name");
            if(receiverName == null) {
                receiverName = "";
            }
        }

        Log.d("Tag",receiverName);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(messageSenderID);

        chatKuva = findViewById(R.id.chatProfiiliKuva);
        lisaaViestejaBtn = findViewById(R.id.chatLisääViestejä);
        takaisinBtn = findViewById(R.id.chatTakaisin);
        chatLayout = findViewById(R.id.chat_linear_layout);
        chatDrawer = findViewById(R.id.drawerChat_layout);
        backKuva = findViewById(R.id.backButton);
        receiver = findViewById(R.id.receiverNameBar);

        receiver.setText(receiverName);

        IntializeControllers();

        if(id == null) {
            Log.d("Tag", "id null");
            if(fromChatId == null) {
                Log.d("Tag","chatid null");
                messageReceiverID = "testID";
                chatLayout.setVisibility(View.GONE);
            }
            else {
                messageReceiverID = fromChatId;
            }
        }
        else {
            messageReceiverID = id;
        }
        //Picasso.get().load(messageReceiverImage).placeholder(R.drawable.ic_account_box).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });

        takaisinBtn.setOnClickListener(view -> {
            finish();
        });

        lisaaViestejaBtn.setOnClickListener(view -> {
            openDrawermenu(chatDrawer);
        });
        backKuva.setOnClickListener(view -> {
            closeDrawermenu(chatDrawer);
        });

        getMessageReceiverImage = "no image";

        strRef.child("ProfilePictures/"+messageReceiverID).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("Tag","picture");
                    getMessageReceiverImage = uri.toString();
                });

        strRef.child("ProfilePictures/"+messageSenderID).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("Tag","ownPicture");
                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(chatKuva);
                });

        content();
    }


    public void onClick_menu(View view) {
        PopupMenu popup = new PopupMenu(this, chatKuva);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.user:
                    handler.removeCallbacks(runnable);
                    Intent userIntent = new Intent(this,ProfiiliActivity.class);
                    startActivity(userIntent);
                    finish();
                    break;
                case R.id.chat:
                    handler.removeCallbacks(runnable);
                    finish();
                    startActivity(getIntent());
                    break;
                case R.id.logout:
                    handler.removeCallbacks(runnable);
                    mAuth.signOut();
                    Intent signOutIntent = new Intent(this, LoginActivity.class);
                    startActivity(signOutIntent);
                    finish();
                    break;
            }
            return false;
        });
        if(mAuth.getCurrentUser() != null) {
            popup.inflate(R.menu.menu_list);
            if (mAuth.getCurrentUser().getDisplayName() != null) {
                popup.getMenu().findItem(R.id.user).setTitle(mAuth.getCurrentUser().getDisplayName());
            } else {
                popup.getMenu().findItem(R.id.user).setTitle(mAuth.getCurrentUser().getEmail());
            }
            popup.show();
        }
        else if(mAuth.getCurrentUser() == null) {
            handler.removeCallbacks(runnable);
            Intent kirjauduIntent = new Intent(this, LoginActivity.class);
            startActivity(kirjauduIntent);
            finish();
        }
    }

    //TODO: tiedostojen lisäys

    private static void openDrawermenu(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawermenu(View view) {
        if (chatDrawer.isDrawerOpen(GravityCompat.START)) {
            chatDrawer.closeDrawer(GravityCompat.START);
        }
    }

    private void IntializeControllers()
    {


        SendMessageButton = findViewById(R.id.send_message_btn);
        SendFilesButton = findViewById(R.id.send_files_btn);
        MessageInputText = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        userContactsList = findViewById(R.id.chats_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager2 = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userContactsList.setLayoutManager(linearLayoutManager2);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    public void content() {
        Calendar calendar = Calendar.getInstance();
        saveCurrentDate = currentDate.format(calendar.getTime());
        saveCurrentTime = currentTime.format(calendar.getTime());
        refresh(5000);
    }

    private void refresh(int milliseconds) {

        runnable = () -> content();
        handler.postDelayed(runnable, milliseconds);
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsFragment.ChatsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("image"))
                                    {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();

                                    holder.userName.setText(retName);

                                    holder.itemView.setOnClickListener(view -> {
                                        handler.removeCallbacks(runnable);
                                        Intent fromChatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                        fromChatIntent.putExtra("visit_user_id", usersIDs);
                                        fromChatIntent.putExtra("visit_user_name", retName);
                                        fromChatIntent.putExtra("visit_image", retImage[0]);
                                        startActivity(fromChatIntent);
                                        finish();
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsFragment.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsFragment.ChatsViewHolder(view);
                    }
                };

        userContactsList.setAdapter(adapter);
        adapter.startListening();

    }


    private void SendMessage()
    {
        Log.d("Tag","sendmessage");
        String messageText = MessageInputText.getText().toString();
        String msgType = "text";
        String name;
        if(mAuth.getCurrentUser().getDisplayName() == null) {
            name = mAuth.getCurrentUser().getEmail();
        }
        else {
            name = mAuth.getCurrentUser().getDisplayName();
        }

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.d("Tag","Else");
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();
            Log.d("Tag","reference");
            String messagePushID = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push().getKey();

            Messages newMessage = new Messages(messageSenderID,messageText, msgType,messageReceiverID,messagePushID,saveCurrentTime,saveCurrentDate,name);

            Map<String, Object> postValues = newMessage.toMap();
            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, postValues);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, postValues);

            ContactRef.child(messageSenderID).child(messageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) {
                        Log.d("Tag", "newCOntact");
                        Contacts newContact = new Contacts(receiverName, getMessageReceiverImage);
                        Log.d("Tag", messageReceiverID);
                        Log.d("tag",getMessageReceiverImage);
                        ContactRef.child(mAuth.getCurrentUser().getUid()).child(messageReceiverID).setValue(newContact);
                        Log.d("tAg","newContactPushed");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Log.d("Tag","map");
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                    Log.d("Tag","Updating");
                }
            });
            Log.d("Tag","updatechildren");

        }
    }
}

