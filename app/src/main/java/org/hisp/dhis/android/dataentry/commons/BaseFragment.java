package org.hisp.dhis.android.dataentry.commons;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.user.UserComponent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    @Nullable
    private Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // check if instance of fragment is leaked or not
        ((DhisApp) getActivity().getApplicationContext())
                .refWatcher().watch(this);

        // unbind butterknife
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected void bind(@NonNull Object target, @NonNull android.view.View view) {
        unbinder = ButterKnife.bind(target, view);
    }

    protected UserComponent getUserComponent() {
        return ((Components) getActivity().getApplicationContext()).userComponent();
    }
}
