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
