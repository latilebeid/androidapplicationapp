package com.example.projetandroid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    RecyclerView recyclerView ;
    AdapterUsers adapterUsers;
    List<Model_users> userslist;
    public UsersFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup ViewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, ViewGroup, false);

        //init recycler view
        recyclerView = view.findViewById(R.id.user_recyclerview);
        //set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userslist = new ArrayList<>();

        //getAll users

        getAllUsers();

        return view ;

    }

    private void getAllUsers() {
        //get current user
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of databse named Users
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Model_users model_users = ds.getValue(Model_users.class);
                    //get all users ecxept currently signed
                    if(!model_users.getUid().equals(fuser.getUid())){
                        userslist.add(model_users);
                    }
              //adapter
                    adapterUsers = new AdapterUsers(getActivity(),userslist);

              //set adapter  to recycler view
              recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
