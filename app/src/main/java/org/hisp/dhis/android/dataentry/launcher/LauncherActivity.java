/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dataentry.launcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.hisp.dhis.android.dataentry.AppComponent;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.server.ServerComponent;

import javax.inject.Inject;

public class LauncherActivity extends AppCompatActivity implements LauncherView {

    @Inject
    LauncherPresenter launcherPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        AppComponent appComponent = ((DhisApp) getApplicationContext()).appComponent();
        ServerComponent serverComponent = ((DhisApp) getApplicationContext()).serverComponent();

        // creating instance of LauncherComponent and
        // injecting dependencies into activity
        appComponent.plus(new LauncherModule(serverComponent))
                .inject(this);

        // Injection should happen here, but without constant interaction
        // with application (you should not keep everything in your application class)
        launcherPresenter.isUserLoggedIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcherPresenter.onAttach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        launcherPresenter.onDetach();
    }

    @Override
    public void navigateToLoginView() {
        Toast.makeText(this, "navigateToLoginView()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHomeView() {
        Toast.makeText(this, "navigateToHomeView()", Toast.LENGTH_SHORT).show();
    }
}
