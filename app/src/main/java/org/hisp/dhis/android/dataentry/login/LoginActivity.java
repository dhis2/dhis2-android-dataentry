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

package org.hisp.dhis.android.dataentry.login;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.home.HomeActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;

@SuppressWarnings({
        "PMD.ExcessiveImports"
}) // This activity needs a lot of android.* imports
public class LoginActivity extends AppCompatActivity implements LoginView {
    private static final String STATE_IS_LOADING = "state:isLoading";

    @BindView(R.id.progress_bar_circular)
    CircularProgressBar progressBar;

    @BindView(R.id.layout_login_views)
    ViewGroup loginViewsContainer;

    @BindView(R.id.edittext_server_url)
    EditText serverUrl;

    @BindView(R.id.edittext_username)
    EditText username;

    @BindView(R.id.edittext_password)
    EditText password;

    @BindView(R.id.button_log_in)
    Button loginButton;

    @Inject
    LoginPresenter loginPresenter;

    // LayoutTransition (for JellyBean+ devices only)
    LayoutTransition layoutTransition;

    // Animations for pre-JellyBean devices
    Animation layoutTransitionSlideIn;
    Animation layoutTransitionSlideOut;

    // Action which should be executed after animation is finished
    OnPostAnimationRunnable onPostAnimationAction;

    @NonNull
    public static Intent create(@NonNull Activity activity) {
        return new Intent(activity, LoginActivity.class);
    }

    private static boolean isGreaterThanOrJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Configuring progress bar (setting width of 6dp)
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(R.dimen.progressbar_stroke_width);
        progressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());

        OnPostAnimationListener onPostAnimationListener = new OnPostAnimationListener();

        /* adding transition animations to root layout */
        if (isGreaterThanOrJellyBean()) {
            setLayoutTransitionOnJellyBeanAndGreater(onPostAnimationListener);
        } else {
            layoutTransitionSlideIn = AnimationUtils.loadAnimation(this, R.anim.in_up);
            layoutTransitionSlideOut = AnimationUtils.loadAnimation(this, R.anim.out_down);

            layoutTransitionSlideIn.setAnimationListener(onPostAnimationListener);
            layoutTransitionSlideOut.setAnimationListener(onPostAnimationListener);
        }

        hideProgress();
        onTextChanged();

        LoginComponent loginComponent = ((Components) getApplicationContext()).loginComponent();
        if (loginComponent == null) {
            // in case if we don't have cached presenter
            loginComponent = ((Components) getApplicationContext()).createLoginComponent();
        }

        loginComponent.inject(this);
    }

    @OnTextChanged(callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED, value = {
            R.id.edittext_server_url, R.id.edittext_username, R.id.edittext_password
    })
    public void onTextChanged() {
        loginButton.setEnabled(!isEmpty(serverUrl.getText()) &&
                !isEmpty(username.getText()) && !isEmpty(password.getText()));
    }

    @OnClick(value = {
            R.id.button_log_in
    })
    public void onButtonClicked(View view) {
        if (view.getId() == R.id.button_log_in) {
            loginPresenter.validateCredentials(serverUrl.getText().toString(),
                    username.getText().toString(), password.getText().toString());
        }
    }

    /*
    * @RequiresApi annotation needed to pass lint checks run outside of Android Studio
    * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setLayoutTransitionOnJellyBeanAndGreater(OnPostAnimationListener animationListener) {
        layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.addTransitionListener(animationListener);

        RelativeLayout loginLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
        loginLayoutContent.setLayoutTransition(layoutTransition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginPresenter.onAttach(this);
    }

    @Override
    protected void onPause() {
        loginPresenter.onDetach();

        if (onPostAnimationAction != null) {
            onPostAnimationAction.run();
            onPostAnimationAction = null;
        }

        super.onPause();
    }

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        if (onPostAnimationAction == null) {
            outState.putBoolean(STATE_IS_LOADING, progressBar.isShown());
        } else {
            outState.putBoolean(STATE_IS_LOADING, onPostAnimationAction.isProgressBarWillBeShown());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_IS_LOADING, false)) {
            showProgress();
        } else {
            hideProgress();
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(this, true);
        } else {
            onStartLoading();
        }
    }

    @Override
    public void hideProgress() {
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(this, false);
            return;
        }

        onFinishLoading();
    }

    @Override
    public void showInvalidServerUrlError() {
        serverUrl.setError(getResources().getString(R.string.error_wrong_server_url));
    }

    @Override
    public void showInvalidCredentialsError() {
        username.setError(getString(R.string.error_wrong_credentials));
        password.setError(getString(R.string.error_wrong_credentials));
    }

    @Override
    public void showUnexpectedError() {
        Toast.makeText(this, getResources().getString(
                R.string.error_unexpected_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showServerError() {
        Toast.makeText(this, getResources().getString(
                R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        Intent intent = HomeActivity.create(this);
        ActivityCompat.startActivity(this, intent, null);
        overridePendingTransition(
                R.anim.activity_open_enter,
                R.anim.activity_open_exit);
        finish();

        // clean-up the component instance, since we don't need it anymore.
        ((Components) getApplicationContext()).releaseLoginComponent();
    }

    /**
     * Should be called in order to show progressbar.
     */
    private void onStartLoading() {
        if (layoutTransitionSlideOut != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideOut);
        }

        loginViewsContainer.setVisibility(View.GONE);
    }

    private void onFinishLoading() {
        if (layoutTransitionSlideIn != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideIn);
        }

        loginViewsContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private boolean isAnimationInProgress() {
        boolean layoutTransitionAnimationsInProgress =
                layoutTransition != null && layoutTransition.isRunning();
        boolean layoutTransitionAnimationSlideUpInProgress = layoutTransitionSlideIn != null &&
                layoutTransitionSlideIn.hasStarted() && !layoutTransitionSlideIn.hasEnded();
        boolean layoutTransitionAnimationSlideOutInProgress = layoutTransitionSlideOut != null &&
                layoutTransitionSlideOut.hasStarted() && !layoutTransitionSlideOut.hasEnded();

        return layoutTransitionAnimationsInProgress ||
                layoutTransitionAnimationSlideUpInProgress ||
                layoutTransitionAnimationSlideOutInProgress;
    }

    /* since this runnable is intended to be executed on UI (not main) thread, we should
    be careful and not keep any implicit references to activities */
    private static class OnPostAnimationRunnable implements Runnable {
        private final LoginActivity loginActivity;
        private final boolean showProgress;

        OnPostAnimationRunnable(LoginActivity loginActivity, boolean showProgress) {
            this.loginActivity = loginActivity;
            this.showProgress = showProgress;
        }

        @Override
        public void run() {
            if (loginActivity != null) {
                if (showProgress) {
                    loginActivity.showProgress();
                } else {
                    loginActivity.hideProgress();
                }
            }
        }

        boolean isProgressBarWillBeShown() {
            return showProgress;
        }
    }

    private class OnPostAnimationListener implements TransitionListener, AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onPostAnimation();
        }

        @Override
        public void startTransition(LayoutTransition transition,
                ViewGroup container, View view, int type) {
            // stub implementation
        }

        @Override
        public void endTransition(LayoutTransition transition,
                ViewGroup container, View view, int type) {
            if (LayoutTransition.CHANGE_APPEARING == type
                    || LayoutTransition.CHANGE_DISAPPEARING == type) {
                onPostAnimation();
            }
        }

        private void onPostAnimation() {
            if (onPostAnimationAction != null) {
                onPostAnimationAction.run();
                onPostAnimationAction = null;
            }
        }
    }
}