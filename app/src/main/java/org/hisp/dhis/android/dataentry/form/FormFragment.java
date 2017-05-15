package org.hisp.dhis.android.dataentry.form;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.date.DatePickerDialogFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class FormFragment extends BaseFragment implements FormView {

    // Fragment arguments
    private static final String FORM_VIEW_ARGUMENTS = "formViewArguments";

    private FormViewArguments formViewArguments;

    // Views
    @BindView(R.id.coordinatorlayout_form)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tablayout_data_entry)
    AutoHidingTabLayout tabLayout;

    @BindView(R.id.viewpager_dataentry)
    ViewPager viewPager;

    @BindView(R.id.textview_report_date)
    TextView reportDate;

    @BindView(R.id.fab_complete_event)
    FloatingActionButton fab;

    private Unbinder unbinder;

    @Inject
    FormPresenter formPresenter;

    private FormSectionAdapter formSectionAdapter;
    private PublishSubject<EventStatus> undoObservable;
    private PublishSubject<String> onReportDateChanged;

    public FormFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(@NonNull FormViewArguments formViewArguments) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelable(FORM_VIEW_ARGUMENTS, formViewArguments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        formSectionAdapter = new FormSectionAdapter(getFragmentManager());
        viewPager.setAdapter(formSectionAdapter);
        tabLayout.setupWithViewPager(viewPager);

        initReportDatePicker();
    }

    @Override
    public Observable<EventStatus> eventStatusChanged() {
        undoObservable = PublishSubject.create();
        return undoObservable.mergeWith(RxView.clicks(fab).map(o -> getEventStatusFromFab()));
    }

    private EventStatus getEventStatusFromFab() {
        return fab.isActivated() ? EventStatus.ACTIVE : EventStatus.COMPLETED;
    }

    @Override
    public Observable<String> reportDateChanged() {
        onReportDateChanged = PublishSubject.create();
        return onReportDateChanged;
    }

    private void initReportDatePicker() {
        reportDate.setOnClickListener(v -> {
            DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance(false);
            dialog.show(getFragmentManager());
            dialog.setFormattedOnDateSetListener(publishReportDateChange());
        });
    }

    @NonNull
    private DatePickerDialogFragment.FormattedOnDateSetListener publishReportDateChange() {
        return date -> {
            if (onReportDateChanged != null) {
                onReportDateChanged.onNext(date);
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            formViewArguments = getArguments().getParcelable(FORM_VIEW_ARGUMENTS);
        }
        getUserComponent().plus(new FormModule(formViewArguments)).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        formPresenter.onAttach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        formPresenter.onDetach();
    }

    @Override
    public Consumer<List<FormSectionViewModel>> renderSectionViewModels() {
        return sectionViewModels -> formSectionAdapter.swapData(sectionViewModels);
    }

    @Override
    public Consumer<String> renderReportDate() {
        return reportDate -> this.reportDate.setText(reportDate);
    }

    @Override
    public Consumer<String> renderTitle() {
        return title -> toolbar.setTitle(title);
    }

    @Override
    public Consumer<EventStatus> renderStatus() {
        return eventStatus -> fab.setActivated(eventStatus == EventStatus.COMPLETED);
    }

    @Override
    public void renderStatusChangeSnackBar(EventStatus eventStatus) {
        String snackBarMessage;
        if (eventStatus == EventStatus.COMPLETED) {
            snackBarMessage = getString(R.string.complete);
        } else {
            snackBarMessage = getString(R.string.active);
        }
        Snackbar.make(coordinatorLayout, snackBarMessage, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), v1 -> {
                    if (undoObservable == null) {
                        return;
                    }
                    if (eventStatus == EventStatus.COMPLETED) {
                        undoObservable.onNext(EventStatus.ACTIVE);
                    } else {
                        undoObservable.onNext(EventStatus.COMPLETED);
                    }
                })
                .show();
    }

    @Override
    public FormViewArguments formViewArguments() {
        return formViewArguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}