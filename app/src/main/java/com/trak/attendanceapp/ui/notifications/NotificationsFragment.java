package com.trak.attendanceapp.ui.notifications;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.trak.attendanceapp.HomeActivity;
import com.trak.attendanceapp.MainActivity;
import com.trak.attendanceapp.R;
import com.trak.attendanceapp.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    Button signout;
    GoogleSignInOptions gso;
    ImageView displayPicture;
    TextView text_account,email;
    GoogleSignInClient gsc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeActivity home = (HomeActivity) getActivity();
        signout = root.findViewById(R.id.signout);
        text_account = (TextView) root.findViewById(R.id.text_account);
        email = (TextView) root.findViewById(R.id.email);
        displayPicture = (ImageView) root.findViewById(R.id.displayPicture);
        signout.setBackgroundColor(getResources().getColor(R.color.danger));
        signout.setTextColor(getResources().getColor(R.color.white));

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(),gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account!=null){
            String name = account.getDisplayName();
        }

        signout.setOnClickListener(v -> home.SignOut());

        text_account.setText(home.name);
        email.append(home.email);

        Glide.with(this).load(home.displayPhoto).apply(RequestOptions.circleCropTransform()).into(displayPicture);


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}