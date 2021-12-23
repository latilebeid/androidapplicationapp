package com.example.projetandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projetandroid.adapters.AdapterUsers;
import com.example.projetandroid.models.Model_users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<Model_users> userslist;
    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup ViewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, ViewGroup, false);

        firebaseAuth = FirebaseAuth.getInstance();

        //init recycler view
        recyclerView = view.findViewById(R.id.user_recyclerview);

        //set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userslist = new ArrayList<>();

        //getAll users
        getAllUsers();

        return view;
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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_users model_users = ds.getValue(Model_users.class);
                    //get all users ecxept currently signed
           //         Toast.makeText(getActivity(), "e10", Toast.LENGTH_SHORT).show();
                    if (!model_users.getUid().equals(fuser.getUid())) {
             //           Toast.makeText(getActivity(), "e11", Toast.LENGTH_SHORT).show();

                        userslist.add(model_users);
                    }
                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userslist);
               //     Toast.makeText(getActivity(), "e12", Toast.LENGTH_SHORT).show();
                    //set adapter  to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged in user
            //mprofileTv.setText(user.getEmail());
        } else {
            //user not signed in ,go to main activity
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }
    // inflate options menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       setHasOptionsMenu(true);//to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //inflating menu
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
        // searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        //search listner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    SearchUsers(s);
                } else {
                    //search text empty
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    SearchUsers(s);
                } else {
                    //search text empty
                    getAllUsers();
                }
                return false;
            }
        });


    }

    private void SearchUsers(String query) {

        //get current user
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of databse named Users
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_users model_users = ds.getValue(Model_users.class);
                    //get all users ecxept currently signed
                    if (!model_users.getUid().equals(fuser.getUid())) {
                        if (model_users.getName().toLowerCase().contains(query.toLowerCase()) || model_users.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userslist.add(model_users);
                        }

                     //   userslist.add(model_users);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userslist);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    //set adapter  to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /* handle menu email clicks */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}