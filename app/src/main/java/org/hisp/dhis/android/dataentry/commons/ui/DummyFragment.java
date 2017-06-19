package org.hisp.dhis.android.dataentry.commons.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class DummyFragment extends BaseFragment {

    public static final String DUMMY_STRING_ARG = "DUMMY_STRING_ARG";

    @BindView(R.id.dummy_text)
    TextView dummyText;
    private Unbinder unbinder;

    public DummyFragment() {
        // empty constructor
    }

    public static DummyFragment newInstance(String dummyText) {
        DummyFragment dummyFragment = new DummyFragment();
        Bundle arguments = new Bundle();
        arguments.putString(DUMMY_STRING_ARG, dummyText);
        dummyFragment.setArguments(arguments);
        return dummyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null && getArguments().containsKey(DUMMY_STRING_ARG)) {
            dummyText.setText(getArguments().getString(DUMMY_STRING_ARG));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}