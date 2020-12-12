package com.example.ninjagoldgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Leaderboard extends AppCompatActivity {

    private ListView lvLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        lvLeaderboard = findViewById(R.id.lvLeaderboard);

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        lvLeaderboard.setAdapter(adapter);


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("scores");
        Query ref = FirebaseDatabase.getInstance().getReference().child("scores").orderByChild("score").limitToLast(10);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long i = snapshot.getChildrenCount();
                list.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Model model = snap.getValue(Model.class);
                    String txt = "TOP " + i + " -> Name: " + model.getName() + " - Score: " + model.getScore();
                    list.add(txt);
                    i--;
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     }
}