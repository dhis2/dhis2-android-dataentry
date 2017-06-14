package org.hisp.dhis.android.dataentry.create;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.FontTextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import io.reactivex.Observable;
import timber.log.Timber;

public class CreateItemsFragment extends BaseFragment implements CreateItemsView {

    private static final String ARG_CREATE = "arg:create";
    public static final int FIRST_CARDVIEW = 0;
    public static final int SECOND_CARDVIEW = 1;
    public static final int CREATE = 2;

    @BindViews({R.id.text_picker1, R.id.text_picker2})
    List<FontTextView> textViews;

    @BindViews({R.id.imagebutton_cancel1, R.id.imagebutton_cancel2})
    List<ImageButton> cancelButtons;

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

        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new CreateItemsModule(argument))
                .inject(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: handle results from SelectionDialogFragments:

    }

    @Override
    public void showDialog(int id) {
        //TODO: show the dialog fragment on isClick.
        Timber.d("Show dialog for " + id);
    }

    @Override
    public void createItem() {
        Timber.d("Create item clicked!");
    }

    @NonNull
    @Override
    public Observable<Object> cardViewClickEvent(int id) {
        return RxView.clicks(textViews.get(id));
    }

    @NonNull
    @Override
    public Observable<Object> cardViewClearEvent(int id) {
        return RxView.clicks(cancelButtons.get(id));
    }

    @NonNull
    @Override
    public Observable<Object> createButtonEvent() {
        return RxView.clicks(create);
    }

    @Override
    public void setCardViewText(int id, @NonNull String text) {
        TextView t = textViews.get(id);
        if (t != null) {
            Timber.d("Set text of cardView " + id + " to " + text);
            t.setText(text);
        }
    }

    @Override
    public void setCardViewsHintsEnrollment() {
        textViews.get(FIRST_CARDVIEW).setHint(R.string.program);
        textViews.get(SECOND_CARDVIEW).setHint(R.string.program_stage);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onAttach(this);
    }
}
