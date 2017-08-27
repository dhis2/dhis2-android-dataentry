package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.utils.Result;
import org.hisp.dhis.rules.models.RuleEffect;

import io.reactivex.Flowable;

interface RuleEngineRepository {

    @NonNull
    Flowable<Result<RuleEffect>> calculate();
}
