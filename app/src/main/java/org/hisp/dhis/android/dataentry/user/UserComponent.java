package org.hisp.dhis.android.dataentry.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.main.MainComponent;
import org.hisp.dhis.android.dataentry.main.MainModule;
import org.hisp.dhis.android.dataentry.main.home.HomeComponent;
import org.hisp.dhis.android.dataentry.main.home.HomeModule;

import dagger.Subcomponent;

@PerUser
@Subcomponent(modules = {
        UserModule.class
})
public interface UserComponent {

    @NonNull
    MainComponent plus(@NonNull MainModule mainModule);

    @NonNull
    HomeComponent plus(@NonNull HomeModule homeModule);
}
