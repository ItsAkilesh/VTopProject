package com.trak.attendanceapp.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trak.attendanceapp.HomeActivity;
import com.trak.attendanceapp.R;
import com.trak.attendanceapp.databinding.FragmentHomeBinding;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trak.attendanceapp.ui.dashboard.DashboardFragment;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HomeFragment extends Fragment {


    ArrayList<String> classes;
    ArrayList<String> classDocIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String email;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView classList = root.findViewById(R.id.classList);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        email = account.getEmail();


        root.findViewById(R.id.addClass).setOnClickListener((view)->{
            // make Modal to insert class values
            createClass();

        });

        classes = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, classes);
        classList.setAdapter(adapter);
        classList.setOnItemClickListener(
            (parent, view, position, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("docId", classDocIds.get(position));
                NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_navigation_home_to_navigation_dashboard, bundle);

            }
        );


        refreshArray();


        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    void createClass() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_class_dialog);

         EditText className = dialog.findViewById(R.id.className);
         EditText courseCode = dialog.findViewById(R.id.courseCode);

         dialog.findViewById(R.id.classSubmitButton)
                 .setOnClickListener(view ->{
                     // Add a new document with a generated id.
                     Map<String, Object> data = new HashMap<>();
                     data.put("name", className.getText().toString());
                     data.put("courseCode", courseCode.getText().toString());

                     db.collection(email)
                         .add(data)
                         .addOnSuccessListener( (docRef)-> {
                             refreshArray();
                             Toast.makeText(getContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                         })
                         .addOnFailureListener( e -> {
                             Toast.makeText(getContext(), "Error adding!", Toast.LENGTH_SHORT).show();
                         });
                     dialog.dismiss();
                 });
         dialog.show();



    }
   void refreshArray( ){
         db.collection(email)
                        .get()
                        .addOnCompleteListener((task) ->{
                            if (task.isSuccessful()) {
                                classes.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> hash = document.getData();
                                    String listItem =  (String) hash.get("courseCode")  + " - " + (String) hash.get("name");
                                    classes.add(listItem);
                                    classDocIds.add(document.getId());
                                }
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "got some data", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "Error Getting classList", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

}