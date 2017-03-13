package org.hisp.dhis.android.dataentry.main.home;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

class HomePresenterImpl implements HomePresenter {

    private final SchedulerProvider schedulerProvider;
    private final HomeRepository homeRepository;
    private final CompositeDisposable compositeDisposable;

    HomePresenterImpl(SchedulerProvider schedulerProvider, HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        isNull(view, "HomeView must not be null");

        if (view instanceof HomeView) {
            HomeView homeView = (HomeView) view;

            compositeDisposable.add(homeRepository.homeEntities()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            homeView.swapData(),
                            throwable -> homeView.renderError(throwable.getMessage())));
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
