package org.hisp.dhis.android.dataentry.main.home;

import java.util.List;

import io.reactivex.Observable;

public interface HomeRepository {

    Observable<List<HomeViewModel>> homeEntities();
}
