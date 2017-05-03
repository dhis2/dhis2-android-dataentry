package org.hisp.dhis.android.dataentry.service;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        D2.class, Response.class
})
public class SyncPresenterTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SyncView syncView;

    @Mock
    private Call<Response> metaDataCall;

    @Captor
    private ArgumentCaptor<SyncResult> syncResultCaptor;

    // @PowerMock
    private D2 d2;

    // instance of sync presenter under tests
    private SyncPresenter syncPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        d2 = PowerMockito.mock(D2.class);
        syncPresenter = new SyncPresenterImpl(d2, new MockSchedulersProvider());

        when(d2.syncMetaData()).thenReturn(metaDataCall);
    }

    @Test
    public void d2ShouldNotBeCalledOnAttach() {
        syncPresenter.onAttach(syncView);

        verify(d2, never()).syncMetaData();
    }

    @Test
    public void successShouldBeReturnedOnResponse() throws Exception {
        Response response = PowerMockito.mock(Response.class);

        when(metaDataCall.call()).thenReturn(response);
        syncPresenter.onAttach(syncView);

        // method under tests
        syncPresenter.sync();

        verify(d2).syncMetaData();
        verify(syncView.render(), times(2)).accept(syncResultCaptor.capture());
        assertThat(syncResultCaptor.getAllValues().get(0)).isEqualTo(SyncResult.progress());
        assertThat(syncResultCaptor.getAllValues().get(1)).isEqualTo(SyncResult.success());
    }

    @Test
    public void failureShouldBeReturnedOnException() throws Exception {
        when(metaDataCall.call()).thenThrow(new IOException("oops"));
        syncPresenter.onAttach(syncView);

        // method under tests
        syncPresenter.sync();

        verify(d2).syncMetaData();
        verify(syncView.render(), times(2)).accept(syncResultCaptor.capture());
        assertThat(syncResultCaptor.getAllValues().get(0)).isEqualTo(SyncResult.progress());
        assertThat(syncResultCaptor.getAllValues().get(1)).isEqualTo(SyncResult.failure("oops"));
    }

    @Test
    public void onDetachShouldNotKeepReferenceToView() throws Exception {
        // necessary to execute call
        Response response = PowerMockito.mock(Response.class);
        when(metaDataCall.call()).thenReturn(response);

        syncPresenter.onAttach(syncView);
        syncPresenter.onDetach();

        // trigger sync, view should not be invoked in any way
        syncPresenter.sync();

        verify(syncView, never()).render();
    }
}
