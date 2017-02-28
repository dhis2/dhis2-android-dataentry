package org.hisp.dhis.android.dataentry.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.home.HomeComponent;
import org.hisp.dhis.android.dataentry.home.HomeModule;

import dagger.Subcomponent;

@PerUser
@Subcomponent(modules = {
        UserModule.class
})
public interface UserComponent {

    @NonNull
    HomeComponent plus(@NonNull HomeModule homeModule);
}
