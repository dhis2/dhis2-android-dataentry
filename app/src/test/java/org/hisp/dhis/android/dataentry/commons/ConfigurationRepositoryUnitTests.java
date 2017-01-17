package org.hisp.dhis.android.dataentry.commons;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigurationRepositoryUnitTests {

    @Mock
    private ConfigurationManager configurationManager;

    private ConfigurationRepository configurationRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configurationRepository = new ConfigurationRepositoryImpl(configurationManager);
    }

    @Test
    public void configuration_shouldReturnEmptyConfiguration() {
        when(configurationManager.configuration()).thenReturn(null);

        TestObserver<ConfigurationModel> testSubscriber = configurationRepository.configuration().test();
        testSubscriber.assertResult(ConfigurationModel.builder().serverUrl("").build());
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void configuration_shouldReturnMatchingConfiguration() {
        ConfigurationModel configurationModel = ConfigurationModel.builder()
                .serverUrl("test_server_url")
                .build();

        when(configurationManager.configuration()).thenReturn(configurationModel);

        TestObserver<ConfigurationModel> testSubscriber = configurationRepository.configuration().test();
        testSubscriber.assertResult(configurationModel);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }
}
