package com.saurav.ourlife.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.saurav.ourlife.Activities.HomeActivity;
import com.saurav.ourlife.Helper.FragmentHelper;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.R;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView dashboardDate;
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
        memoriesCard = (MaterialCardView) view.findViewById(R.id.memoriesCard);
        experienceCard = (MaterialCardView) view.findViewById(R.id.experienceCard);
        bucketlistCard = (MaterialCardView) view.findViewById(R.id.bucketlistCard);
        quizCard = (MaterialCardView) view.findViewById(R.id.quizCard);

        // set ClickListener to Cards
        memoriesCard.setOnClickListener(this);
        experienceCard.setOnClickListener(this);
        bucketlistCard.setOnClickListener(this);
        quizCard.setOnClickListener(this);

        //set date for events card
        dashboardDate = view.findViewById(R.id.dashboard_date);
        dashboardDate.setText(GenericHelper.getCurrentDateAsString(""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memoriesCard:
                /*if(!GenericHelper.hasPermission(getActivity(), GenericHelper.PERMISSIONS_ALL)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            GenericHelper.PERMISSIONS_ALL,
                            GenericHelper.PERMISSIONS_CODE);
                } else {
                    ((HomeActivity) getActivity()).initGallery();
                }*/
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
