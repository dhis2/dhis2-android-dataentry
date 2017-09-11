package org.hisp.dhis.android.dataentry.main;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;
import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

class MainPresenterImpl implements MainPresenter {
    private final SchedulerProvider schedulerProvider;
    private final UserRepository userRepository;
    private final CompositeDisposable compositeDisposable;
    private final D2 d2;

    MainPresenterImpl(@NonNull D2 d2,
                      @NonNull SchedulerProvider schedulerProvider,
                      @NonNull UserRepository userRepository) {
        this.d2 = d2;
        this.schedulerProvider = schedulerProvider;
        this.userRepository = userRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        isNull(view, "MainView must not be null");

        if (view instanceof MainView) {
            MainView mainView = (MainView) view;

            ConnectableFlowable<UserModel> userObservable = userRepository.me()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .publish();

            compositeDisposable.add(userObservable
                    .map(this::username)
                    .subscribe(mainView.renderUsername(), Timber::e));

            compositeDisposable.add(userObservable
                    .map(this::userInitials)
                    .subscribe(mainView.renderUserInitials(), Timber::e));

            compositeDisposable.add(userObservable.connect());
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    private String username(@NonNull UserModel user) {
        String username = "";
        if (!isEmpty(user.firstName())) {
            username += user.firstName();
        }

        if (!isEmpty(user.surname())) {
            if (!username.isEmpty()) {
                username += " ";
            }

            username += user.surname();
        }

        return username;
    }

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    private String userInitials(@NonNull UserModel user) {
        String initials = "";
        if (!isEmpty(user.firstName())) {
            initials += String.valueOf(user.firstName().charAt(0));
        }

        if (!isEmpty(user.surname())) {
            initials += String.valueOf(user.surname().charAt(0));
        }
        return initials.toUpperCase(Locale.US);
    }

    @Override
    public void logOut() {
        try {
            d2.logOut().call();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}