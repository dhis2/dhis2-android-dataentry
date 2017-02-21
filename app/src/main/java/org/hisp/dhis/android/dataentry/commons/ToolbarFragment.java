package org.hisp.dhis.android.dataentry.commons;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class ToolbarFragment extends BaseFragment {
    private static final String ARG_TITLE = "arg:title";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Unbinder unbinder;

    public static ToolbarFragment create(@NonNull String title) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);

        ToolbarFragment toolbarFragment = new ToolbarFragment();
        toolbarFragment.setArguments(arguments);
        return toolbarFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toolbar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(getArguments().getString(ARG_TITLE, ""));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
