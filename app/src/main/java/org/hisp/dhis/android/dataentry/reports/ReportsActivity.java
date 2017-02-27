package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.hisp.dhis.android.dataentry.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class ReportsActivity extends AppCompatActivity implements ReportsView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
    }

    @NonNull
    @Override
    public Consumer<List<ReportViewModel>> renderReportViewModels() {
        return null;
    }
}
