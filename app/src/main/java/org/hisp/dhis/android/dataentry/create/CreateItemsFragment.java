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

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.FontTextView;
import org.hisp.dhis.android.dataentry.selection.SelectionArgument;
import org.hisp.dhis.android.dataentry.selection.SelectionDialogFragment;
import org.hisp.dhis.android.dataentry.selection.SelectionViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class CreateItemsFragment extends BaseFragment implements CreateItemsView {

    private static final String ARG_CREATE = "arg:create";
    public static final String ARG_SELECTIONS = "arg:selectionStates";
    public static final String TAG_SELECTION_DIALOG_FRAGMENT = "tag:selectionDialogFragment";
    public static final int FIRST_SELECTION = 0;
    public static final int SECOND_SELECTION = 1;
    private static final int[] REQUEST_CODES = {FIRST_SELECTION, SECOND_SELECTION}; //request codes for the
    // DialogFragment. index should match the card view index.
    public static final int SELECTORS_COUNT = 2;

    @BindView(R.id.text_selection1)
    FontTextView selectionTextView1;

    @BindView(R.id.text_selection2)
    FontTextView selectionTextView2;

    @BindView(R.id.imagebutton_cancel1)
    ImageButton cancelButton1;

    @BindView(R.id.imagebutton_cancel2)
    ImageButton cancelButton2;

    @BindView(R.id.fab_create)
    FloatingActionButton create;

    @Inject
    CreateItemsPresenter presenter;

    @Inject
    CreateItemsNavigator navigator;

    SelectionStateModel state1;
    SelectionStateModel state2;

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

        isNull(getArguments().<CreateItemsArgument>getParcelable(ARG_CREATE), "CreteArgument must be supplied");

        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new CreateItemsModule(getArguments().getParcelable(ARG_CREATE),
                        (CreateItemsActivity) getActivity()))
                .inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_SELECTIONS,
                new ArrayList<>(Arrays.asList(this.state1, this.state2))
        );
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

        if (savedInstanceState != null) {
            List<SelectionStateModel> selectionStates = savedInstanceState.getParcelableArrayList(ARG_SELECTIONS);
            isNull((selectionStates == null || selectionStates.size() < SELECTORS_COUNT),
                    "SelectionStateModels must be supplied");
            this.state1 = selectionStates.get(FIRST_SELECTION);
            this.state2 = selectionStates.get(SECOND_SELECTION);
        } else {
            ArrayList<SelectionStateModel> states = initSelectionStates(getArguments().getParcelable(ARG_CREATE));
            state1 = states.get(FIRST_SELECTION);
            state2 = states.get(SECOND_SELECTION);
        }
        //TODO: restore the states set text labels and hints
        selectionTextView1.setText(state1.name());
        selectionTextView1.setHint(state1.label());
        selectionTextView2.setText(state2.name());
        selectionTextView2.setHint(state2.label());

    }

    @NonNull
    private ArrayList<SelectionStateModel> initSelectionStates(CreateItemsArgument createArgument) {
        SelectionArgument.Type selection1Type;
        SelectionArgument.Type selection2Type;
        String selection1Label;
        String selection2Label;

        if (createArgument.type() == CreateItemsArgument.Type.EVENT ||
                createArgument.type() == CreateItemsArgument.Type.ENROLLMENT_EVENT) {
            selection2Type = SelectionArgument.Type.PROGRAM_STAGE;
            selection2Label = getString(R.string.program_stage);
        } else if (createArgument.type() == CreateItemsArgument.Type.TEI ||
                createArgument.type() == CreateItemsArgument.Type.ENROLLMENT) {
            selection2Type = SelectionArgument.Type.PROGRAM;
            selection2Label = getString(R.string.program);
        } else {
            throw new IllegalStateException("Unknown CreateItemsArgument type.");
        }
        selection1Type = SelectionArgument.Type.ORGANISATION;
        selection1Label = getString(R.string.organisation);

        return new ArrayList<>(Arrays.asList(
                SelectionStateModel.createEmpty(selection1Label, selection1Type),
                SelectionStateModel.createEmpty(selection2Label, selection2Type)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SelectionDialogFragment.RESULT_CODE) {
            SelectionViewModel model = data.getParcelableExtra(SelectionDialogFragment.SELECTION_RESULT);

            if (requestCode == REQUEST_CODES[FIRST_SELECTION]) {
                setSelection(FIRST_SELECTION, model.uid(), model.name());
            } else if (requestCode == REQUEST_CODES[SECOND_SELECTION]) {
                setSelection(SECOND_SELECTION, model.uid(), model.name());
            }
        }
    }

    @Override
    public void navigateNext(@NonNull String uid) {
        Timber.d("Navigating to next: " + uid);
        navigator.navigateTo(uid);
    }

    @NonNull
    @Override
    public Observable<SelectionStateModel> selectionChanges(int id) {
        if (id == FIRST_SELECTION) {
            return RxTextView.afterTextChangeEvents(selectionTextView1).map(event -> state1);
        } else if (id == SECOND_SELECTION) {
            return RxTextView.afterTextChangeEvents(selectionTextView1).map(event -> state1);
        } else {
            throw new IllegalStateException("No such selection view: " + id);
        }
    }

    @NonNull
    @Override
    public SelectionStateModel getSelectionState(int id) {
        if (id == FIRST_SELECTION) {
            return state1;
        } else if (id == SECOND_SELECTION) {
            return state2;
        } else {
            throw new IllegalStateException("No such selection state: " + id);
        }
    }

    @Override
    public void setSelection(int id, @NonNull String uid, @NonNull String name) {
        if (id == FIRST_SELECTION) {
            Timber.d("Set text of cardView " + id + " to " + name);
            selectionTextView1.setText(name);
            state1 = SelectionStateModel.createModifiedSelection(uid, name, state1);
            Timber.d("State : " + state1.toString());
        } else {
            Timber.d("Set text of cardView " + id + " to " + name);
            selectionTextView2.setText(name);
            state2 = SelectionStateModel.createModifiedSelection(uid, name, state2);
            Timber.d("State : " + state2.toString());
        }
    }

    private void showDialog(int id, @NonNull String parentUid) {
        SelectionStateModel model;
        if (id == FIRST_SELECTION) {
            model = state1;
        } else if (id == SECOND_SELECTION) {
            model = state2;
        } else {
            throw new IllegalStateException("Called with non existing id.");
        }
        SelectionArgument arg = SelectionArgument.create(parentUid, model.label(), model.type());
        SelectionDialogFragment dialog = SelectionDialogFragment.create(arg);
        dialog.setTargetFragment(this, REQUEST_CODES[id]);
        dialog.show(getFragmentManager(), TAG_SELECTION_DIALOG_FRAGMENT);
    }

    @Override
    public void showDialog1(@NonNull String parentUid) {
        this.showDialog(FIRST_SELECTION, parentUid);
    }

    @Override
    public void showDialog2(@NonNull String parentUid) {
        this.showDialog(SECOND_SELECTION, parentUid);
    }

    @NonNull
    @Override
    public Observable<Object> selection1ClickEvents() {
        return RxView.clicks(selectionTextView1);
    }

    @NonNull
    @Override
    public Observable<Object> selection2ClickEvents() {
        return RxView.clicks(selectionTextView2);
    }

    @NonNull
    @Override
    public Observable<Object> selection1ClearEvent() {
        return RxView.clicks(cancelButton1);
    }

    @NonNull
    @Override
    public Observable<Object> selection2ClearEvent() {
        return RxView.clicks(cancelButton2);
    }

    @NonNull
    @Override
    public Observable<Pair<String, String>> createButtonClick() {
        return RxView.clicks(create).map(event -> Pair.create(state1.uid(), state2.uid()));
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
