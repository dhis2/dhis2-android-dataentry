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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.date.DatePickerDialogFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class FormFragment extends BaseFragment implements FormView {
    private static final String FORM_VIEW_ARGUMENTS = "formViewArguments";

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

    @Inject
    FormPresenter formPresenter;

    private FormSectionAdapter formSectionAdapter;
    private PublishSubject<ReportStatus> undoObservable;
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
        bind(this, view);

        formSectionAdapter = new FormSectionAdapter(getFragmentManager());
        viewPager.setAdapter(formSectionAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setupActionBar();
        initReportDatePicker();
    }

    private void setupActionBar() {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
            }
        }
    }

    @NonNull
    @Override
    public Observable<ReportStatus> eventStatusChanged() {
        undoObservable = PublishSubject.create();
        return undoObservable.mergeWith(RxView.clicks(fab).map(o -> getReportStatusFromFab()));
    }

    @NonNull
    @Override
    public Observable<String> reportDateChanged() {
        onReportDateChanged = PublishSubject.create();
        return onReportDateChanged;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FormViewArguments arguments = isNull(getArguments()
                .getParcelable(FORM_VIEW_ARGUMENTS), "formViewArguments == null");

        ((DhisApp) getActivity().getApplicationContext())
                .createFormComponent(new FormModule(arguments))
                .inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ((DhisApp) getActivity().getApplicationContext())
                .releaseFormComponent();
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

    @NonNull
    @Override
    public Consumer<List<FormSectionViewModel>> renderSectionViewModels() {
        return sectionViewModels -> {
            formSectionAdapter.swapData(sectionViewModels);
            if (sectionViewModels.size() == 0) {
                Log.d("EMPTY", "Show empty state");
                // TODO: Show empty state
            }

        };
    }

    @NonNull
    @Override
    public Consumer<String> renderReportDate() {
        return date -> reportDate.setText(date);
    }

    @NonNull
    @Override
    public Consumer<String> renderTitle() {
        return title -> toolbar.setTitle(title);
    }

    @NonNull
    @Override
    public Consumer<ReportStatus> renderStatus() {
        return eventStatus -> fab.setActivated(eventStatus == ReportStatus.COMPLETED);
    }

    @NonNull
    @Override
    public Consumer<String> finishEnrollment() {
        return enrollmentUid -> {
            getActivity().finish();
        };
    }

    @Override
    public void renderStatusChangeSnackBar(@NonNull ReportStatus reportStatus) {
        String snackBarMessage = reportStatus == ReportStatus.COMPLETED ?
                getString(R.string.complete) : getString(R.string.active);

        Snackbar.make(coordinatorLayout, snackBarMessage, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), v1 -> {
                    if (undoObservable == null) {
                        return;
                    }
                    if (reportStatus == ReportStatus.COMPLETED) {
                        undoObservable.onNext(ReportStatus.ACTIVE);
                    } else {
                        undoObservable.onNext(ReportStatus.COMPLETED);
                    }
                })
                .show();
    }

    private ReportStatus getReportStatusFromFab() {
        return fab.isActivated() ? ReportStatus.ACTIVE : ReportStatus.COMPLETED;
    }

    private void initReportDatePicker() {
        reportDate.setOnClickListener(v -> {
            DatePickerDialogFragment dialog = DatePickerDialogFragment.create(false);
            dialog.show(getFragmentManager());
            dialog.setFormattedOnDateSetListener(publishReportDateChange());
        });
    }

    @NonNull
    private DatePickerDialogFragment.FormattedOnDateSetListener publishReportDateChange() {
        return date -> {
            if (onReportDateChanged != null) {
                onReportDateChanged.onNext(BaseIdentifiableObject.DATE_FORMAT.format(date));
            }
        };
    }
}