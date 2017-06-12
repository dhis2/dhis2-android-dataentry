package org.hisp.dhis.android.dataentry.create;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.FontTextView;
import org.hisp.dhis.android.dataentry.reports.ReportsModule;

import javax.inject.Inject;

import butterknife.BindView;

public class CreateItemsFragment extends BaseFragment implements CreateItemsView {

    private static final String ARG_CREATE = "arg:create";


    @BindView(R.id.text_picker1)
    FontTextView firstText;

    @BindView(R.id.imagebutton_cancel1)
    ImageButton firstCancel;

    @BindView(R.id.text_picker2)
    FontTextView secondText;

    @BindView(R.id.imagebutton_cancel2)
    ImageButton secondCancel;

    @BindView(R.id.fab_create)
    FloatingActionButton create;

    @Inject
    CreateItemsPresenter presenter;


    public static Fragment create(CreateItemsArgument createArgument) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_CREATE, createArgument);

        CreateItemsFragment fragment = new CreateItemsFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        CreateItemsArgument argument = getArguments().getParcelable(ARG_CREATE);

        if(argument == null) {
            throw new IllegalArgumentException("CreteArgument must be supplied");
        }

/*
        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new CreateItemsModule(argument))
                .inject(this);
*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);
    }

   /* @Override
    public void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
//        presenter.onAttach(this);
    }*/
}
