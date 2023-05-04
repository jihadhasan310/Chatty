package dm2e.jihad.chatty.chat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import dm2e.jihad.chatty.R;
import dm2e.jihad.chatty.autentication.AuthActivity;
import dm2e.jihad.chatty.model.UserAdpter;
import dm2e.jihad.chatty.model.Users;

public class ChatActivity extends AppCompatActivity {
    ImageView logout;
    FirebaseAuth auth;
    ImageView setbut,callbut;

    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        logout=findViewById(R.id.id_logout);
        setbut=findViewById(R.id.settingBut);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");
        callbut=findViewById(R.id.callBut);
        usersArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(ChatActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                  

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    // comprueba si esta iniciado sesion o no
        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(ChatActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
        logout();

        callbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.jitsi_layout);


                Button no,si;
                si = dialog.findViewById(R.id.id_jitsi_aceptar);
                no = dialog.findViewById(R.id.id_jitsi_cancelar);
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText room=dialog.findViewById(R.id.id_jitsi_room);
                        String roomString=room.getText().toString();
                        // conectar a jisi
                        if (roomString.isEmpty()) {
                            room.setError("No ha introducido nombre de la Sala");

                        } else {


                             FirebaseDatabase database = FirebaseDatabase.getInstance();
                             DatabaseReference  reference = database.getReference().child("user").child(auth.getUid());

                             reference.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {

                                   String  nombre_usuario = dataSnapshot.child("userName").getValue(String.class);
                                     try {
                                         JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
                                         userInfo.setDisplayName(nombre_usuario);

                                         JitsiMeetConferenceOptions options
                                                 = new JitsiMeetConferenceOptions.Builder()
                                                 .setServerURL(new URL("https://meet.jit.si"))
                                                 .setRoom(roomString)
                                                 .setAudioMuted(true)
                                                 .setVideoMuted(true)
                                                 .setFeatureFlag("notifications.enabled", false)
                                                 .setFeatureFlag("welcomepage.enable", false)
                                                 .setUserInfo(userInfo)

                                                 .build();

                                         JitsiMeetActivity.launch(ChatActivity.this, options);
                                         System.out.println("ok");
                                     } catch (MalformedURLException e) {
                                         e.printStackTrace();
                                     }

                                     // Hacer lo que necesites con el email obtenido
                                     Log.d(TAG, "El nombre del usuario es: " + nombre_usuario);
                                 }

                                 @Override
                                 public void onCancelled(DatabaseError error) {
                                     // Aquí se ejecuta el código si ocurre un error al leer los datos del usuario
                                     Log.w(TAG, "Error al leer los datos del usuario.", error.toException());
                                 }
                             });

                        }

                    }

                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }

        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void logout() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.dialog_layout);
                Button no,si;
                si = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ChatActivity.this,AuthActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });
    }







}