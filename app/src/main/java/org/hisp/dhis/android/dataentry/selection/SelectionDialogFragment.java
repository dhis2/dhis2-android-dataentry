package org.hisp.dhis.android.dataentry.selection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * A recommended way for use:
 *
 * When calling:
 *  SelectionArgument arg = SelectionArgument.create("eUZ79clX7y1", "Diagnosis ICD10", SelectionArgument.Type.OPTION);
 * SelectionDialogFragment dialog = SelectionDialogFragment.create(arg);
 * dialog.setTargetFragment(this, 1);
 * dialog.show(getFragmentManager(), "selectionDialogFragment");
 * (where  1 is the request code)
 *
 * The fragment should implement onActivity like:
 *     @Override
 * public void onActivityResult(int requestCode, int resultCode, Intent data) {
 * super.onActivityResult(requestCode, resultCode, data);
 * if (requestCode == 1 && resultCode == SelectionDialogFragment.RESULT_CODE) {
 * SelectionViewModel model = data.getParcelableExtra(SelectionDialogFragment.SELECTION_RESULT);
 * Timber.d("chosen : " + model);
 * }
 * }
 */
public class SelectionDialogFragment extends AppCompatDialogFragment
        implements SelectionView, View.OnClickListener{

    public static final String SELECTION_ARG = "arg:selection_arg";
    public static final String SELECTION_RESULT = "arg:selection_result";
    public static final int RESULT_CODE = 0;

    @BindView(R.id.selection_dialog_title)
    TextView titleView;

    @BindView(R.id.selection_dialog_searchview)
    SearchView searchView;

    @BindView(R.id.selection_dialog_recyclerView)
    RecyclerView selectionListView;

    private Unbinder unbinder;

    @Inject
    public SelectionPresenter presenter;

    public static SelectionDialogFragment create(SelectionArgument argument) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTION_ARG, argument);

        SelectionDialogFragment dialogFragment = new SelectionDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Consumer<List<SelectionViewModel>> update() {
        return selectionList -> ((SelectionDialogAdapter) selectionListView.getAdapter()).update(selectionList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.selection_dialog_option, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //This lets Android know that the Dialog should be resized when the soft keyboard is shown:
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, view);

        selectionListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectionListView.setAdapter(new SelectionDialogAdapter(this));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SelectionArgument argument = getArguments().getParcelable(SELECTION_ARG);
        if (argument == null) {
            throw new IllegalStateException("SelectionArgument must be supplied!");
        }
        ((DhisApp) context.getApplicationContext()).userComponent().plus(new SelectionModule(argument)).inject(this);
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

    @OnClick(R.id.selection_dialog_cancel)
    public void onCancelClick() {
        this.dismiss();
    }

    @Override
    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public io.reactivex.Observable<SearchViewQueryTextEvent> subscribeToSearchView() {
        return RxSearchView.queryTextChangeEvents(searchView);
    }

    /**
     * Called by the RecyclerView.ViewHolder.TextView onClick.
     * The view is expected to contain the SelectionModelView as a tag.
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view instanceof SelectionView){
            SelectionViewModel model = (SelectionViewModel) view.getTag();
            if (model != null) {
                Intent result = new Intent();
                result.putExtra(SELECTION_RESULT, model);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE, result);
                this.dismiss();
            }
        }
    }
}
