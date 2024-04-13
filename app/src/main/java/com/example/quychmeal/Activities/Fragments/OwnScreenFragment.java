package com.example.quychmeal.Activities.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.quychmeal.Activities.CookingActivity;
import com.example.quychmeal.Activities.EditProfileActivity;
import com.example.quychmeal.R;

public class OwnScreenFragment extends Fragment {

    Button btnCook;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_own_screen, container, false);

        btnCook = view.findViewById(R.id.btnCook);

        handleNavigate();


        return view;
    }

    private void handleNavigate() {
        startActivity(new Intent(getActivity(), CookingActivity.class));
    }
}