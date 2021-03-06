package com.example.psquared;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListenerDatabase extends AppCompatActivity {
    private FirebaseDatabase database;
    private RecyclerView users;
    private LinearLayoutManager linear;
    private FirebaseRecyclerAdapter adapter;
    private String filter;
    private boolean listeners;
    private boolean talkers;
    private boolean counselors;
    CheckBox talkerBox;
    CheckBox listenerBox;
    CheckBox counselorBox;
    private EditText filterBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_listener_database);
        database = FirebaseDatabase.getInstance();
        users = findViewById(R.id.Users);
        linear = new LinearLayoutManager(getApplicationContext());
        users.setLayoutManager(linear);
        users.setHasFixedSize(true);
        filter = "";
        filterBox = findViewById(R.id.filter);
        talkerBox = findViewById(R.id.talkerBox);
        listenerBox = findViewById(R.id.listenerBox);
        counselorBox = findViewById(R.id.counselorBox);
        fetch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        talkers = true;
        listeners = true;
        counselors = true;
        talkerBox.setChecked(true);
        listenerBox.setChecked(true);
        counselorBox.setChecked(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void backButton(View view) {
        Intent listenersToMain = new Intent(this, CounselorMain.class);
        startActivity(listenersToMain);
    }

    public void addListener(View view) {
        EditText editText = findViewById(R.id.filter);
        final String username = editText.getText().toString();
        DatabaseReference users = database.getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    if (username.equals(s.getKey())) {
                        final DatabaseReference child = database.getReference().child("Users").child(username);
                        child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the listener value from the firebase database
                                long value = dataSnapshot.getValue(long.class);
                                Integer v = new Integer((int) value);
                                if (v == 0) {
                                    child.setValue(1);
                                    Toast.makeText(getApplicationContext(), "Successfully converted to listener", Toast.LENGTH_SHORT).show();
                                } else if (v == 1) {
                                    Toast.makeText(getApplicationContext(), "User is already listener", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void removeListener(View view) {
        EditText editText = findViewById(R.id.filter);
        final String username = editText.getText().toString();
        final DatabaseReference users = database.getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    if (username.equals(s.getKey())) {
                        final DatabaseReference child = database.getReference().child("Users").child(username);
                        child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the listener value from the firebase database
                                long value = dataSnapshot.getValue(long.class);
                                Integer v = new Integer((int) value);
                                if (v == 0) {
                                    Toast.makeText(getApplicationContext(), "User is already talker", Toast.LENGTH_SHORT).show();
                                } else if (v == 1) {
                                    child.setValue(0);
                                    Toast.makeText(getApplicationContext(), "Successfully converted to talker", Toast.LENGTH_SHORT).show();
                                }
                                child.removeEventListener(this);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void applySearch(View v) {
        if (filterBox.getText() == null) {
            filter = "";
        } else {
            filter = filterBox.getText().toString();
        }
        adapter.stopListening();
        adapter.startListening();
    }

    public void applyFilters(View v) {
        talkers = talkerBox.isChecked();
        listeners = listenerBox.isChecked();
        counselors = counselorBox.isChecked();
        adapter.stopListening();
        adapter.startListening();
    }
    private void fetch() {

        Query query = database.getReference().child("Users");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>() {
                            String type;
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                                switch (snapshot.getValue().toString()) {
                                    case "0":
                                        type = "Talker";
                                        break;
                                    case "1":
                                        type = "Listener";
                                        break;
                                    case "2":
                                        type = "Counselor";
                                        break;
                                    default:
                                        type = "dummy";
                                        break;
                                }
                                return new User(snapshot.getKey(), type, "n/a");
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_item, parent, false);

                return new UserViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(UserViewHolder holder, final int position, User model) {
                holder.setAccountType(model.getType());
                holder.setUsername(model.getId());


                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
                holder.root.setVisibility(View.GONE);

                if ((model.getType().equals("Listener") && listeners
                    || model.getType().equals("Talker") && talkers
                    || model.getType().equals("Counselor") && counselors)
                    && (filter.equals("") || model.getId().indexOf(filter) >= 0)) {
                    holder.root.setVisibility(View.VISIBLE);
                } else {
                    holder.root.setVisibility(View.GONE);
                }

            }

        };
        users.setAdapter(adapter);
    }
    public class UserViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView username;
        public TextView accountType;
        public TextView status;
        public Button changeType;
        private View.OnClickListener buttonListener;
        private final String BUTTONTALKER = "convert to listener";
        private final String BUTTONLISTENER = "convert to talker";
        private DatabaseReference mRef;
        private DatabaseReference availableListeners;
        private DatabaseReference availableTalkers;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            username = itemView.findViewById(R.id.Username);
            accountType = itemView.findViewById(R.id.accountType);
            changeType = itemView.findViewById(R.id.changeStatus);
            status = itemView.findViewById(R.id.active);
            availableListeners = database.getReference("availableListeners");
            availableTalkers = database.getReference("availableTalkers");

            buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue().toString().equals("1")) {
                                changeType.setText(BUTTONTALKER);
                                accountType.setText("Talker");
                                mRef.setValue(0);

                            } else if (dataSnapshot.getValue().toString().equals("0")) {
                                changeType.setText(BUTTONLISTENER);
                                accountType.setText("Listener");
                                mRef.setValue(1);

                            } else {
                                Toast.makeText(getApplicationContext(), "an error has occurred", Toast.LENGTH_SHORT).show();

                            }
                            mRef.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            };
            changeType.setOnClickListener(buttonListener);

        }

        public void setUsername(String user) {
            final String id = user;
            final TextView v = status;
            username.setText(user);

            mRef = database.getReference("Users").child(username.getText().toString());
            availableListeners.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue().toString().equals(id)) {
                            v.setVisibility(View.VISIBLE);
                            v.setText("L");
                            v.setTextColor(getResources().getColor(R.color.activeColor));
                            return;
                        }
                    }
                    v.setVisibility(View.INVISIBLE);
                    v.setTextColor(getResources().getColor(R.color.textSecondaryColorDark));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            availableTalkers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue().toString().equals(id + "@jhu.edu")) {
                            v.setVisibility(View.VISIBLE);
                            v.setText("T");
                            v.setTextColor(getResources().getColor(R.color.activeColor));
                            return;
                        }
                    }
                    v.setVisibility(View.INVISIBLE);
                    v.setTextColor(getResources().getColor(R.color.textSecondaryColorDark));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setAccountType(String type) {
            accountType.setText(type);

            if(type.equals("Talker")) {
                changeType.setVisibility(View.VISIBLE);
                changeType.setText(BUTTONTALKER);
            } else if (type.equals("Listener")) {
                changeType.setVisibility(View.VISIBLE);
                changeType.setText(BUTTONLISTENER);
            } else {
                status.setVisibility(View.GONE);
                changeType.setVisibility(View.GONE);
            }
        }
    }

}
