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
import android.widget.ImageButton;
import android.widget.TextView;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rx.Observable;
import timber.log.Timber;

public class SelectionDialogFragment extends AppCompatDialogFragment
        implements SelectionView, View.OnClickListener{

    public static final String SELECTION_ARG = "arg:selection_arg";
    public static final String SELECTION_RESULT = "arg:selection_result";
    public static final int RESULT_CODE = 0;

    @BindView(R.id.selection_dialog_title)
    TextView titleView;

    @BindView(R.id.selection_dialog_cancel)
    ImageButton cancelButton;

    @BindView(R.id.selection_dialog_searchview)
    SearchView searchView;

    @BindView(R.id.selection_dialog_recyclerView)
    RecyclerView selectionListView;

    private Unbinder unbinder;
    private Disposable searchDisposable;

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
        return ((SelectionDialogAdapter) this.selectionListView.getAdapter())::update;
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
        searchDisposable.dispose();
        presenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchDisposable = RxSearchView.queryTextChangeEvents(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( query -> {
                    presenter.onDetach();
                    presenter.onAttach(this, query.queryText().toString());
//                    presenter.onAttach(this);
//                    Timber.d("text changed !" + query); //TODO:
                });

    }

    @OnClick(R.id.selection_dialog_cancel)
    public void onCancelClick() {
        this.dismiss();
    }

    @Override
    public void setTitle(String title) {
        titleView.setText(title);
    }

    /**
     * Called by the RecyclerView.ViewHolder.TextView onClick.
     * The view is expected to contain the SelectionModelView as a tag.
     * @param view
     */
    @Override
    public void onClick(View view) {
        SelectionViewModel model  = (SelectionViewModel) view.getTag();
        if(model != null) {
            Timber.d("Tag: " + view.getTag());
            Intent result = new Intent();
            result.putExtra(SELECTION_RESULT, model);
            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE, result);
            this.dismiss();
        }
    }
}
