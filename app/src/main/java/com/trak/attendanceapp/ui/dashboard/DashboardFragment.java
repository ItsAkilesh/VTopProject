package com.trak.attendanceapp.ui.dashboard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trak.attendanceapp.Klass;
import com.trak.attendanceapp.R;
import com.trak.attendanceapp.Student;
import com.trak.attendanceapp.databinding.FragmentDashboardBinding;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment {

    public String docId;
    public Klass record;
    public String email;
    public String currentDate;
    public String classname;
    public String coursecode;
    public View root ;
    public ListView studentsListView;
    public ArrayList<Student> students = new ArrayList<>();
    public ArrayList<String> studentDocIds = new ArrayList<>();
    ArrayAdapter<Student> adapter;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
         root = binding.getRoot();

         studentsListView = root.findViewById(R.id.studentList);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        email = account.getEmail();
        Bundle bundle = getArguments();
        if(bundle != null )
            docId = bundle.getString("docId", null);


        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, students);
        studentsListView.setAdapter(adapter);
        studentsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        studentsListView.setOnItemClickListener((parent, v, pos, id) -> {
            CheckedTextView checkedV = (CheckedTextView) v;
            Student s = (Student) studentsListView.getItemAtPosition(pos);
            s.setMarked(!checkedV.isChecked());
            checkedV.setChecked(!checkedV.isChecked());

        });

        root.findViewById(R.id.addStudent).setOnClickListener((view)->{
            // make Modal to insert class values
            addStudent();
            refreshArray();

        });

        root.findViewById(R.id.mark).setOnClickListener(v ->{
            markAttendance();

        });

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        Button datepickerButton = root.findViewById(R.id.datepicker);
        datepickerButton.setOnClickListener(v -> {
            materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
        });
        materialDatePicker.addOnPositiveButtonClickListener(selection ->{
            currentDate = materialDatePicker.getHeaderText();
            datepickerButton.setText(currentDate);
            refreshArray();
        });





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    void refreshArray(){

        if(docId != null ){

        db.collection(email)
                .document(docId)
                .get()
                .addOnCompleteListener((task) ->{
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        record = (Klass) document.toObject(Klass.class);

                        classname = record.getName();
                        TextView cn = root.findViewById(R.id.courseName);
                        coursecode = record.getCourseCode();
                        cn.setText(classname);
                        TextView cc = root.findViewById(R.id.courseCode);
                        cc.setText(coursecode);


                        ArrayList<Student> studentList  =  currentDate == null  || !record.getAttendances().containsKey(currentDate)
                                ? (ArrayList<Student>) record.getStudents().stream().map(s -> { s.setMarked(false); return s;}).collect(Collectors.toList())
                                : record.getAttendances().get(currentDate);


                        if(studentList != null ){
                            students.clear();
                            students.addAll(studentList);
                            for(int i=0;i< students.size(); i++ )
                                studentsListView.setItemChecked(i,students.get(i).getMarked() );
                            adapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getContext(), "No students to display", Toast.LENGTH_SHORT).show();



                        Toast.makeText(getContext(), "Fetched students data", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Error Getting classList", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    void addStudent(){
      final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_student_dialog);

         EditText name = dialog.findViewById(R.id.name);
         EditText regid = dialog.findViewById(R.id.regid);

         dialog.findViewById(R.id.submit)
                 .setOnClickListener(view ->{
                     // Add a new document with a generated id.
                     Student s = new Student(name.getText().toString(), regid.getText().toString(), false);
                     students.add(s);
                     record.setStudents(students);
                     record.getAttendances().forEach((k,v)-> v.add(s) );
                     adapter.notifyDataSetChanged();

                     db.collection(email)
                         .document(docId)
                         .set(record)
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
    void markAttendance(){

        record.setStudents(record.getStudents());
        Map<String, ArrayList<Student>> newAttendances =  record.getAttendances() == null? new HashMap<>() : record.getAttendances();
        newAttendances.put(currentDate, students);
        record.setAttendances(newAttendances);
        db.collection(email)
                .document(docId)
                .set(record)
                .addOnSuccessListener( (docRef)-> {
                    refreshArray();
                    Toast.makeText(getContext(), "Marked attendance!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener( e -> {
                    Toast.makeText(getContext(), "Error marking attendance!", Toast.LENGTH_SHORT).show();
                });

    }
}