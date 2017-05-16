package org.hisp.dhis.android.dataentry.form;

import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface FormView extends View {

    Observable<EventStatus> eventStatusChanged();

    Observable<String> reportDateChanged();

    Consumer<List<FormSectionViewModel>> renderSectionViewModels();

    Consumer<String> renderReportDate();

    Consumer<String> renderTitle();

    Consumer<EventStatus> renderStatus();

    void renderStatusChangeSnackBar(EventStatus eventStatus);

    FormViewArguments formViewArguments();
}