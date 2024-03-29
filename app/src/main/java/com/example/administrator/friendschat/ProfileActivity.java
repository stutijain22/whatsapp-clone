package com.example.administrator.friendschat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiveUserID,senderUserID,Current_State;
    private CircleImageView userProfileImage;
    private TextView userProfileName,userProfileStatus;
    private Button SendMessageRequestButton,DeclineMessageRequestButton;
    private DatabaseReference UserRef,ChatRequestRef,ContactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiveUserID=getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileImage=(CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName=(TextView) findViewById(R.id.visit_user_name);
        userProfileStatus=(TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton=(Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton=findViewById(R.id.decline_message_request_button);
        Current_State="new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo()
    {
        UserRef.child(receiveUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
              if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
              {
                  String userImage=dataSnapshot.child("image").getValue().toString();
                  String userName=dataSnapshot.child("name").getValue().toString();
                  String userStatus=dataSnapshot.child("status").getValue().toString();

                  Picasso.get().load(userImage).placeholder(R.drawable.th).into(userProfileImage);
                  userProfileName.setText(userName);
                  userProfileStatus.setText(userStatus);

                  ManageChatRequests();
              }
              else
              {
                  String userName=dataSnapshot.child("name").getValue().toString();
                  String userStatus=dataSnapshot.child("status").getValue().toString();

                  userProfileName.setText(userName);
                  userProfileStatus.setText(userStatus);

                  ManageChatRequests();
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//////send message button use
    private void ManageChatRequests()
    {
        ChatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
              if (dataSnapshot.hasChild(receiveUserID))
              {
                  String request_type = dataSnapshot.child(receiveUserID).child("request_type").getValue().toString();
                  if (request_type.equals("sent"))
                  {
                      Current_State="request_sent";
                      SendMessageRequestButton.setText("Cancel Chat Request");
                  }
                  else if (request_type.equals("received"))
                  {
                      Current_State="request received";
                      SendMessageRequestButton.setText("Accept Chat Request");
                      DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                      DeclineMessageRequestButton.setEnabled(true);
                      DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              CancelChatRequest();
                          }
                      });
                  }
              }
              else
              {
                  ContactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          if (dataSnapshot.hasChild(receiveUserID))
                          {
                              Current_State="friends";
                              SendMessageRequestButton.setText("Remove this Contact");
                          }
                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {

                      }
                  });
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (!senderUserID.equals(receiveUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequestButton.setEnabled(false);
                    if (Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (Current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }
                }
            });
        }
        else
        {
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {
        ContactsRef.child(senderUserID).child(receiveUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    ContactsRef.child(receiveUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SendMessageRequestButton.setEnabled(true);
                                Current_State="new";
                                SendMessageRequestButton.setText("Send Message");

                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                DeclineMessageRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptChatRequest()
    {
        ContactsRef.child(senderUserID).child(receiveUserID).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiveUserID).child(senderUserID).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                ChatRequestRef.child(senderUserID).child(receiveUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(receiveUserID).child(senderUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_State="friends";
                                                                                    SendMessageRequestButton.setText("Remove this Contact");

                                                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest()
    {
        ChatRequestRef.child(senderUserID).child(receiveUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiveUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SendMessageRequestButton.setEnabled(true);
                                Current_State="new";
                                SendMessageRequestButton.setText("Send Message");

                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                DeclineMessageRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiveUserID).child("request_type").setValue("send")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ChatRequestRef.child(receiveUserID).child(senderUserID).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                         if (task.isSuccessful())
                                         {
                                             SendMessageRequestButton.setEnabled(true);
                                             Current_State="request_sent";
                                             SendMessageRequestButton.setText("Cancel Chat Request");
                                         }
                                        }
                                    });
                        }
                    }
                });
    }
}