package com.example.dhanuja.cpool;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatvwActivity extends AppCompatActivity {

    ImageButton send;
    private ListView listOfMessage;
    private TextView messageText, messageUser, messageTime,sendTime,CurrentTime,activenumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<String> list ;
    ArrayAdapter<String> adapter;
    DatabaseReference dref;
    ChatMessage chatMessage;
    private int CurrentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatvw);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        messageText = (TextView) findViewById(R.id.message_text);
        messageUser = (TextView) findViewById(R.id.message_user);
        sendTime = (TextView) findViewById(R.id.message_time);
        listOfMessage = (ListView) findViewById(R.id.list_of_messages);
        send = (ImageButton)findViewById(R.id.sendbtn);
        activenumber = (TextView)findViewById(R.id.activeusers);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//Active Users display - START
        FirebaseAuth firebaseAuthvw = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabasevw = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferencevw = firebaseDatabasevw.getReference("ActivevwUsers");

        databaseReferencevw.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActiveUsers activeUsers = dataSnapshot.getValue(ActiveUsers.class);
                CurrentNumber = activeUsers.getNumberactive();
                activenumber.setText("People active in this Chatroom : "+CurrentNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatvwActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
//Active Users display - END
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        dref = firebaseDatabase.getReference("ChatvwActivity");
        list = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.message_text,list);
        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.message_user,list);
        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.message_time,list);

        listOfMessage.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("ChatvwActivity").push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                input.setText("");
            }
        });


        chatMessage = new ChatMessage();

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                adapter.notifyDataSetChanged();

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    chatMessage = ds.getValue(ChatMessage.class);

                    String user = new String(chatMessage.getMessageUser().toString());
                    String message = new String(chatMessage.getMessageText().toString());
                    String time = new String(chatMessage.getSendTime().toString());

                    String total1=new String("      " + user + " \n" + message + "\n                    " + time);
                    list.add(total1);

                }

                listOfMessage.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatvwActivity.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart(){

        FirebaseAuth firebaseAuthvw = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabasevw = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferencevw = firebaseDatabasevw.getReference("ActivevwUsers");

        databaseReferencevw.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActiveUsers activeUsers = dataSnapshot.getValue(ActiveUsers.class);
                CurrentNumber = activeUsers.getNumberactive();
                ActiveUsers newdata = new ActiveUsers(CurrentNumber + 1);
                databaseReferencevw.setValue(newdata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatvwActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        super.onStart();
        Toast.makeText(ChatvwActivity.this,"You have entered the Chatroom", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStop(){
        FirebaseAuth firebaseAuthvw = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabasevw = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferencevw = firebaseDatabasevw.getReference("ActivevwUsers");

        databaseReferencevw.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActiveUsers activeUsers = dataSnapshot.getValue(ActiveUsers.class);
                CurrentNumber = activeUsers.getNumberactive();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatvwActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        ActiveUsers newdata = new ActiveUsers(CurrentNumber - 1);
        databaseReferencevw.setValue(newdata);

        Toast.makeText(ChatvwActivity.this,"You have left the Chatroom", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

}