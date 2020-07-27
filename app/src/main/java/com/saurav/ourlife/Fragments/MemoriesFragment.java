package com.saurav.ourlife.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.saurav.ourlife.Activities.HomeActivity;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.R;

public class MemoriesFragment extends Fragment implements View.OnClickListener {

    View view;
    private CardView birthdayMemories, romanceMemories, vacationMemories, otherMemories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_memories, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // defining hooks for Cards
        birthdayMemories = view.findViewById(R.id.birthdayMemories);
        romanceMemories = view.findViewById(R.id.romanceMemories);
        vacationMemories = view.findViewById(R.id.vacationMemories);
        otherMemories = view.findViewById(R.id.otherMemories);

        // set ClickListener to Cards
        birthdayMemories.setOnClickListener(this);
        romanceMemories.setOnClickListener(this);
        vacationMemories.setOnClickListener(this);
        otherMemories.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment albumsFragment = new GalleryAlbumsFragment();
        Bundle data = new Bundle();

        switch (v.getId()) {
            case R.id.birthdayMemories:
                data.putString("memoriesCategory", "Birthdays");
                albumsFragment.setArguments(data);

                ((HomeActivity) getActivity()).loadFragment(albumsFragment, "BIRTHDAYS");
                break;

            case R.id.romanceMemories:
                Toast.makeText(getActivity(), "Development in Progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.vacationMemories:
                data.putString("memoriesCategory", "Vacation");
                albumsFragment.setArguments(data);

                ((HomeActivity) getActivity()).loadFragment(albumsFragment, "VACATION");
                break;

            case R.id.otherMemories:
                Toast.makeText(getActivity(), "Development in Progress", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}