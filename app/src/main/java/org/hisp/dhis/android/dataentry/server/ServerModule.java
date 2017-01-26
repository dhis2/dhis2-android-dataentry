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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.data.api.Authenticator;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@Module
@PerServer
public class ServerModule {
    private final HttpUrl baseUrl;

    public ServerModule(@NonNull HttpUrl baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @PerServer
    D2 sdk(DbOpenHelper openHelper, OkHttpClient client) {
        return new D2.Builder()
                .baseUrl(baseUrl)
                .dbOpenHelper(openHelper)
                .okHttpClient(client)
                .build();
    }

    @Provides
    @PerServer
    Authenticator authenticator(DbOpenHelper dbOpenHelper) {
        return BasicAuthenticatorFactory.create(dbOpenHelper);
    }

    @Provides
    @PerServer
    OkHttpClient okHttpClient(Authenticator authenticator) {
        return new OkHttpClient.Builder()
                .addInterceptor(authenticator)
                .build();
    }

    @Provides
    @PerServer
    ConfigurationRepository configurationRepository(D2 d2) {
        return new ConfigurationRepositoryImpl(d2);
    }
}
