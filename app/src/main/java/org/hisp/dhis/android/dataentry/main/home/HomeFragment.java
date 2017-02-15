package org.hisp.dhis.android.dataentry.main.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.BaseFragment;

import java.util.List;

public class HomeFragment extends BaseFragment implements HomeView {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((Components) getActivity().getApplicationContext()).mainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void swapData(List<String> homeEntities) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showUnexpectedError(String message) {

    }

    @Override
    public void showError(String message) {

    }
}
