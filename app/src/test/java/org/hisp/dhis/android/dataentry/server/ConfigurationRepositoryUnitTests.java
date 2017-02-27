package org.hisp.dhis.android.dataentry.server;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;
import okhttp3.HttpUrl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigurationRepositoryUnitTests {

    @Mock
    private ConfigurationManager configurationManager;

    @Mock
    private ConfigurationModel configurationModel;

    private ConfigurationRepository configurationRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configurationRepository = new ConfigurationRepositoryImpl(configurationManager);
    }

    @Test
    public void configureShouldInvokeConfigurationManager() {
        HttpUrl baseUrl = HttpUrl.parse("https://play.dhis2.org/demo/");
        HttpUrl baseApiUrl = HttpUrl.parse("https://play.dhis2.org/demo/api/");

        when(configurationModel.serverUrl()).thenReturn(baseApiUrl);
        when(configurationManager.configure(baseUrl)).thenReturn(configurationModel);

        TestObserver<ConfigurationModel> testObserver = configurationRepository
                .configure(baseUrl).test();

        testObserver.assertValue(configurationModel);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.dispose();

        verify(configurationManager).configure(baseUrl);
    }

    @Test
    public void configureShouldEmitErrorIfUrlIsMalformed() {
        // url without trailing slash, which won't work as base url
        HttpUrl baseUrl = HttpUrl.parse("https://play.dhis2.org/demo");

        when(configurationManager.configure(baseUrl)).thenThrow(IllegalArgumentException.class);

        TestObserver<ConfigurationModel> testObserver = configurationRepository
                .configure(baseUrl).test();

        testObserver.assertNoValues();
        testObserver.assertError(IllegalArgumentException.class);
        testObserver.dispose();

        verify(configurationManager).configure(baseUrl);
    }

    @Test
    public void getShouldReturnConfigurationModel() {
        when(configurationManager.get()).thenReturn(configurationModel);

        TestObserver<ConfigurationModel> testObserver = configurationRepository.get().test();

        testObserver.assertValue(configurationModel);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.dispose();

        verify(configurationManager).get();
    }

    @Test
    public void getShouldNotEmitAnyItemsIfConfigurationIsNull() {
        when(configurationManager.get()).thenReturn(null);

        TestObserver<ConfigurationModel> testObserver = configurationRepository.get().test();

        testObserver.assertNoValues();
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.dispose();

        verify(configurationManager).get();
    }

    @Test
    public void removeShouldEmitCountOfRemovedConfigurations() {
        when(configurationManager.remove()).thenReturn(1);

        TestObserver<Integer> testObserver = configurationRepository.remove().test();

        testObserver.assertValue(1);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.dispose();

        verify(configurationManager).remove();
    }
}
