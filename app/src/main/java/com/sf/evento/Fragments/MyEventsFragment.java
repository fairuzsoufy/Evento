package com.sf.evento.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sf.evento.Adapters.MyEventsAdapter;
import com.sf.evento.Adapters.MyGoingEventsAdapter;
import com.sf.evento.R;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {


    public MyEventsFragment() {
        // Required empty public constructor
    }

    View view;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    TextView checking;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_my_events,container,false);
        checking= view.findViewById(R.id.checking);

        db = FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.MyEventsRecycler);
        CollectionReference MyEvents = db.collection("events");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = MyEvents.whereEqualTo("creatorId", user.getUid());
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() > 0) {
                                List<DocumentSnapshot> ds = task.getResult().getDocuments();
                                MyEventsAdapter myEventsAdapter = new MyEventsAdapter(db, ds);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MyEventsFragment.this.getContext(), RecyclerView.VERTICAL,false));
                                recyclerView.setAdapter(myEventsAdapter);
                            }
                            else
                            {
                                checking.setVisibility(View.VISIBLE);

                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error getting documents: "+ e.getMessage());
                    }
                });
        return view;
    }
}
