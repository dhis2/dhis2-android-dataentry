package org.hisp.dhis.android.dataentry.form.dataentry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.utils.Preconditions;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class DataEntryFragment extends BaseFragment implements DataEntryView {
    private static final String ARGUMENTS = "args";

    @BindView(R.id.recyclerview_data_entry)
    RecyclerView recyclerView;

    DataEntryAdapter dataEntryAdapter;

    @NonNull
    public static DataEntryFragment create(@NonNull DataEntryArguments arguments) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENTS, arguments);

        DataEntryFragment dataEntryFragment = new DataEntryFragment();
        dataEntryFragment.setArguments(bundle);

        return dataEntryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);
        setUpRecyclerView();

        DataEntryArguments args = Preconditions.isNull(getArguments()
                .getParcelable(ARGUMENTS), "dataEntryArguments == null");

        BriteDatabase briteDatabase = ((DhisApp) getActivity().getApplication())
                .appComponent().briteDatabase();
        (new ProgramStageRepositoryImpl(briteDatabase, args.event())).fields()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @NonNull
    @Override
    public Flowable<RowAction> rowActions() {
        return dataEntryAdapter.asFlowable();
    }

    private void setUpRecyclerView() {
        // dataEntryAdapter = new DataEntryAdapter(LayoutInflater.from(getActivity()));
        // recyclerView.setAdapter(dataEntryAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
}
