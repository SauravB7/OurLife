package com.saurav.ourlife.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.saurav.ourlife.Activities.HomeActivity;
import com.saurav.ourlife.Helper.FragmentHelper;
import com.saurav.ourlife.Helper.Utils;
import com.saurav.ourlife.R;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView dashboardDate, dashboardEvent;
    private MaterialCardView memoriesCard, experienceCard, bucketlistCard, quizCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FragmentHelper.updateToolbarBG(getActivity(), R.color.colorBackground);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // defining hooks for Cards
        memoriesCard = view.findViewById(R.id.memoriesCard);
        experienceCard = view.findViewById(R.id.experienceCard);
        bucketlistCard = view.findViewById(R.id.bucketlistCard);
        quizCard = view.findViewById(R.id.quizCard);

        // set ClickListener to Cards
        memoriesCard.setOnClickListener(this);
        experienceCard.setOnClickListener(this);
        bucketlistCard.setOnClickListener(this);
        quizCard.setOnClickListener(this);

        //set date for events card
        dashboardEvent = view.findViewById(R.id.dashboard_event);
        dashboardDate = view.findViewById(R.id.dashboard_date);
        dashboardDate.setText(Utils.getCurrentDateAsString(""));

        if(dashboardDate.getText().toString().equalsIgnoreCase("28 July, 2020")) {
            dashboardEvent.setText("HAPPY BIRTHDAY..!!");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memoriesCard:
                ((HomeActivity) getActivity()).loadFragment(new MemoriesFragment(), "MEMORIES");
                break;

            case R.id.experienceCard:
                Toast.makeText(getActivity(), "Development in Progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bucketlistCard:
                Toast.makeText(getActivity(), "Development in Progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.quizCard:
                Toast.makeText(getActivity(), "Development in Progress", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
