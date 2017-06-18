package org.hisp.dhis.android.dataentry.selection;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.DividerDecoration;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStoreModule;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * A recommended way for use:
 * <p>
 * When calling:
 * SelectionArgument arg = SelectionArgument.create("eUZ79clX7y1", "Diagnosis ICD10", SelectionArgument.Type.OPTION);
 * SelectionDialogFragment dialog = SelectionDialogFragment.create(arg);
 * dialog.setTargetFragment(this, 1);
 * dialog.show(getFragmentManager(), "selectionDialogFragment");
 * (where  1 is the request code)
 * <p>
 * The fragment should implement onActivity like:
 *
 * @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
 * super.onActivityResult(requestCode, resultCode, data);
 * if (requestCode == 1 && resultCode == SelectionDialogFragment.RESULT_CODE) {
 * SelectionViewModel model = data.getParcelableExtra(SelectionDialogFragment.SELECTION_RESULT);
 * Timber.d("chosen : " + model);
 * }
 * }
 */
public class SelectionDialogFragment extends AppCompatDialogFragment implements SelectionView {
    private static final String SELECTION_ARG = "arg:selectionArg";
    private static final String OPTION_SELECTION_ARG = "arg:optionSelectionArgs";

    @BindView(R.id.textview_selection_dialog_title)
    TextView titleView;

    @BindView(R.id.searchview_selection_dialog)
    SearchView searchView;

    @BindView(R.id.recyclerview_selection_dialog)
    RecyclerView selectionListView;

    @Inject
    SelectionPresenter presenter;

    @Inject
    SelectionNavigator selectionNavigator;

    private SelectionDialogAdapter selectionAdapter;
    private Unbinder unbinder;

    @NonNull
    public static SelectionDialogFragment create(@NonNull SelectionArgument argument) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTION_ARG, argument);

        SelectionDialogFragment dialogFragment = new SelectionDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    public static SelectionDialogFragment create(@NonNull OptionSelectionArgument argument) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTION_SELECTION_ARG, argument);

        SelectionDialogFragment dialogFragment = new SelectionDialogFragment();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SelectionArgument selectionArgument = getArguments()
                .getParcelable(SELECTION_ARG);
        OptionSelectionArgument optionSelectionArgument = getArguments()
                .getParcelable(OPTION_SELECTION_ARG);

        if (optionSelectionArgument == null) {
            ((DhisApp) context.getApplicationContext()).userComponent()
                    .plus(new SelectionModule(selectionArgument, this))
                    .inject(this);
        } else {
            ((DhisApp) context.getApplicationContext()).userComponent()
                    .plus(new OptionSelectionModule(optionSelectionArgument, this),
                            new DataEntryStoreModule(optionSelectionArgument.dataEntryArgs()))
                    .inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        Window window = getDialog().getWindow();
        if (window != null) {
            // this lets Android know that the Dialog should be
            // resized when the soft keyboard is shown:
            window.setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        selectionAdapter = new SelectionDialogAdapter(LayoutInflater.from(getActivity()));
        selectionListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectionListView.setAdapter(selectionAdapter);
        selectionListView.addItemDecoration(new DividerDecoration(ContextCompat.getDrawable(
                selectionListView.getContext(), R.drawable.divider)));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NonNull
    @Override
    public Observable<SearchViewQueryTextEvent> searchView() {
        return RxSearchView.queryTextChangeEvents(searchView);
    }

    @NonNull
    @Override
    public Observable<SelectionViewModel> searchResultClicks() {
        return selectionAdapter.asObservable();
    }

    @NonNull
    @Override
    public Consumer<List<SelectionViewModel>> renderSearchResults() {
        return selectionAdapter::update;
    }

    @NonNull
    @Override
    public Consumer<String> renderTitle() {
        return title -> titleView.setText(title);
    }

    @NonNull
    @Override
    public Consumer<SelectionViewModel> navigateTo() {
        return selectionNavigator::navigateTo;
    }

    @OnClick(R.id.button_selection_dialog_cancel)
    public void onCancelClick() {
        dismiss();
    }
}
