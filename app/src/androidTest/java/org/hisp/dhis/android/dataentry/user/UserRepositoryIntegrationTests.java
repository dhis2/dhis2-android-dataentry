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

package org.hisp.dhis.android.dataentry.user;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.hisp.dhis.android.dataentry.utils.ImmediateScheduler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryIntegrationTests {
    private Date created;
    private Date lastUpdated;
    private ContentValues user;
    private UserRepository userRepository;

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(ImmediateScheduler.create());

    @Before
    public void setUp() throws IOException {
        created = new Date();
        lastUpdated = new Date();

        user = new ContentValues();
        user.put(UserModel.Columns.ID, 55L);
        user.put(UserModel.Columns.UID, "test_user_uid");
        user.put(UserModel.Columns.CODE, "test_code");
        user.put(UserModel.Columns.NAME, "test_name");
        user.put(UserModel.Columns.DISPLAY_NAME, "test_display_name");
        user.put(UserModel.Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(created));
        user.put(UserModel.Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        user.put(UserModel.Columns.BIRTHDAY, "test_birthday");
        user.put(UserModel.Columns.EDUCATION, "test_education");
        user.put(UserModel.Columns.GENDER, "test_gender");
        user.put(UserModel.Columns.JOB_TITLE, "test_job_title");
        user.put(UserModel.Columns.SURNAME, "test_surname");
        user.put(UserModel.Columns.FIRST_NAME, "test_first_name");
        user.put(UserModel.Columns.INTRODUCTION, "test_introduction");
        user.put(UserModel.Columns.EMPLOYER, "test_employer");
        user.put(UserModel.Columns.INTERESTS, "test_interests");
        user.put(UserModel.Columns.LANGUAGES, "test_languages");
        user.put(UserModel.Columns.EMAIL, "test_email");
        user.put(UserModel.Columns.PHONE_NUMBER, "test_phone_number");
        user.put(UserModel.Columns.NATIONALITY, "test_nationality");

        userRepository = new UserRepositoryImpl(databaseRule.briteDatabase());
    }

    @Test
    public void meShouldReturnUserAndObserveChanges() {
        databaseRule.database().insert(UserModel.TABLE, null, user);

        TestObserver<UserModel> testObserver = userRepository.me().test();
        assertThat(testObserver.values().size()).isEqualTo(1);

        UserModel userModel = testObserver.values().get(0);
        assertThat(userModel.id()).isEqualTo(55L);
        assertThat(userModel.uid()).isEqualTo("test_user_uid");
        assertThat(userModel.code()).isEqualTo("test_code");
        assertThat(userModel.name()).isEqualTo("test_name");
        assertThat(userModel.displayName()).isEqualTo("test_display_name");
        assertThat(userModel.created()).isEqualTo(created);
        assertThat(userModel.lastUpdated()).isEqualTo(lastUpdated);
        assertThat(userModel.birthday()).isEqualTo("test_birthday");
        assertThat(userModel.education()).isEqualTo("test_education");
        assertThat(userModel.gender()).isEqualTo("test_gender");
        assertThat(userModel.jobTitle()).isEqualTo("test_job_title");
        assertThat(userModel.surname()).isEqualTo("test_surname");
        assertThat(userModel.firstName()).isEqualTo("test_first_name");
        assertThat(userModel.introduction()).isEqualTo("test_introduction");
        assertThat(userModel.employer()).isEqualTo("test_employer");
        assertThat(userModel.interests()).isEqualTo("test_interests");
        assertThat(userModel.languages()).isEqualTo("test_languages");
        assertThat(userModel.email()).isEqualTo("test_email");
        assertThat(userModel.phoneNumber()).isEqualTo("test_phone_number");
        assertThat(userModel.nationality()).isEqualTo("test_nationality");

        user.put(UserModel.Columns.FIRST_NAME, "test_another_first_name");

        int updated = databaseRule.briteDatabase().update(UserModel.TABLE, user,
                UserModel.Columns.ID + " = ?", String.valueOf(55L));

        assertThat(updated).isEqualTo(1);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();

        UserModel updatedUserModel = testObserver.values().get(1);

        // we except only one value to change
        assertThat(updatedUserModel.firstName()).isEqualTo("test_another_first_name");

        // rest should be same, including local id
        assertThat(updatedUserModel.id()).isEqualTo(55L);
        assertThat(updatedUserModel.uid()).isEqualTo("test_user_uid");
        assertThat(updatedUserModel.code()).isEqualTo("test_code");
        assertThat(updatedUserModel.name()).isEqualTo("test_name");
        assertThat(updatedUserModel.displayName()).isEqualTo("test_display_name");
        assertThat(updatedUserModel.created()).isEqualTo(created);
        assertThat(updatedUserModel.lastUpdated()).isEqualTo(lastUpdated);
        assertThat(updatedUserModel.birthday()).isEqualTo("test_birthday");
        assertThat(updatedUserModel.education()).isEqualTo("test_education");
        assertThat(updatedUserModel.gender()).isEqualTo("test_gender");
        assertThat(updatedUserModel.jobTitle()).isEqualTo("test_job_title");
        assertThat(updatedUserModel.surname()).isEqualTo("test_surname");
        assertThat(updatedUserModel.introduction()).isEqualTo("test_introduction");
        assertThat(updatedUserModel.employer()).isEqualTo("test_employer");
        assertThat(updatedUserModel.interests()).isEqualTo("test_interests");
        assertThat(updatedUserModel.languages()).isEqualTo("test_languages");
        assertThat(updatedUserModel.email()).isEqualTo("test_email");
        assertThat(updatedUserModel.phoneNumber()).isEqualTo("test_phone_number");
        assertThat(updatedUserModel.nationality()).isEqualTo("test_nationality");

        testObserver.assertNoErrors();
        testObserver.dispose();
    }
}
