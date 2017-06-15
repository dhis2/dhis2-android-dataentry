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

public class CreateItemsFragment extends BaseFragment implements CreateItemsView {

    private static final String ARG_CREATE = "arg:create";
    public static final String ARG_SELECTIONS = "arg:selectionStates";
    public static final int FIRST_SELECTION = 0;
    public static final int SECOND_SELECTION = 1;
    private static final int[] REQUEST_CODES = {FIRST_SELECTION, SECOND_SELECTION}; //request codes for the
    // DialogFragment. index should match the card view index.

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

    CreateItemsArgument argument;

    SelectionStateModel selectionState1;
    SelectionStateModel selectionState2;

    public static Fragment create(CreateItemsArgument createArgument) {
        Bundle arguments = new Bundle();

        arguments.putParcelable(ARG_CREATE, createArgument);
        arguments.putParcelableArrayList(ARG_SELECTIONS, initSelectionStates(createArgument));

        CreateItemsFragment fragment = new CreateItemsFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @NonNull
    private static ArrayList<SelectionStateModel> initSelectionStates(CreateItemsArgument createArgument) {
        SelectionArgument.Type selection1Type;
        SelectionArgument.Type selection2Type;
        int selection1LabelId;
        int selection2LabelId;

        if (createArgument.type() == CreateItemsArgument.Type.ENROLMENT_EVENT) {
            selection1Type = SelectionArgument.Type.PROGRAM;
            selection2Type = SelectionArgument.Type.PROGRAM_STAGE;
            selection1LabelId = R.string.program;
            selection2LabelId = R.string.program_stage;
        } else if (createArgument.type() == CreateItemsArgument.Type.TEI ||
                createArgument.type() == CreateItemsArgument.Type.EVENT ||
                createArgument.type() == CreateItemsArgument.Type.ENROLLMENT) {
            selection1Type = SelectionArgument.Type.ORGANISATION;
            selection2Type = SelectionArgument.Type.PROGRAM;
            selection1LabelId = R.string.organisation;
            selection2LabelId = R.string.program;
        } else {
            throw new IllegalStateException("Unknown CreateItemsArgument type.");
        }
        return new ArrayList<>(Arrays.asList(
                SelectionStateModel.createEmpty(selection1LabelId, selection1Type),
                SelectionStateModel.createEmpty(selection2LabelId, selection2Type)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.argument = getArguments().getParcelable(ARG_CREATE);
        List<SelectionStateModel> selectionStates = getArguments().getParcelableArrayList(ARG_SELECTIONS);
        if(argument == null) {
            throw new IllegalArgumentException("CreteArgument must be supplied");
        } else if (selectionStates == null) {
            throw new IllegalArgumentException("SelectionStateModels must be supplied");
        }
        selectionState1 = selectionStates.get(FIRST_SELECTION);
        selectionState2 = selectionStates.get(SECOND_SELECTION);

        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new CreateItemsModule(argument))
                .inject(this);
    }

    //TODO: see if this would work. (test after hooking up the selectors and repositories.
    @Override
    public void onDetach() {
        super.onDetach();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_CREATE, argument);
        arguments.putParcelableArrayList(ARG_SELECTIONS, initSelectionStates(argument));
        this.setArguments(arguments);
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
        //TODO: restore the states set text labels and hints
        selectionTextView1.setText(selectionState1.name());
        selectionTextView1.setHint(getString(selectionState1.labelId()));
        selectionTextView2.setText(selectionState2.name());
        selectionTextView2.setHint(getString(selectionState2.labelId()));
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
    public void navigateNext() {
        Timber.d("Create item clicked!");
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
    public Observable<SelectionStateModel> selectionChanges(int id) {
        if (id == FIRST_SELECTION) {
            return RxTextView.afterTextChangeEvents(selectionTextView1).map(event -> selectionState1);
        } else if (id == SECOND_SELECTION) {
            return RxTextView.afterTextChangeEvents(selectionTextView1).map(event -> selectionState1);
        } else {
            throw new IllegalStateException("No such selection view: " + id);
        }
    }

    @NonNull
    @Override
    public SelectionStateModel getSelectionState(int id) {
        if (id == FIRST_SELECTION) {
            return selectionState1;
        } else if (id == SECOND_SELECTION) {
            return selectionState2;
        } else {
            throw new IllegalStateException("No such selection state: " + id);
        }
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
        return RxView.clicks(create).map(event -> Pair.create(selectionState1.uid(), selectionState2.uid()));
    }

    @Override
    public void setSelection(int id, @NonNull String uid, @NonNull String name) {
        if (id == FIRST_SELECTION) {
            Timber.d("Set text of cardView " + id + " to " + name);
            selectionTextView1.setText(name);
            selectionState1 = SelectionStateModel.createModifiedSelection(uid, name, selectionState1);
            Timber.d("State : " + selectionState1.toString());
        } else {
            Timber.d("Set text of cardView " + id + " to " + name);
            selectionTextView2.setText(name);
            selectionState2 = SelectionStateModel.createModifiedSelection(uid, name, selectionState2);
            Timber.d("State : " + selectionState2.toString());
        }
    }

    @Override
    public void showDialog1() {
        this.showDialog(FIRST_SELECTION);
    }

    @Override
    public void showDialog2() {
        this.showDialog(SECOND_SELECTION);
    }

    private void showDialog(int id) {//, @NonNull String uid, @NonNull String name) {
        Timber.d("Show dialog for " + id);
        SelectionArgument selectionArgument;
        SelectionArgument.Type type = argument.selectorTypes().get(id);

   /*     SelectionArgument arg = SelectionArgument.create("eUZ79clX7y1", "Diagnosis ICD10",
                SelectionArgument.Type.OPTION);
        SelectionDialogFragment dialog = SelectionDialogFragment.create(arg);
        dialog.setTargetFragment(this, REQUEST_CODES[id]);
        dialog.show(getFragmentManager(), "selectionDialogFragment");*/
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
