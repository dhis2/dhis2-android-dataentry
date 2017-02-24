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

package org.hisp.dhis.android.dataentry.main.home;

import android.content.ContentValues;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.commons.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

import static org.hisp.dhis.android.dataentry.main.home.HomeEntity.Columns.ENTITY_TYPE;

public class HomeRepositoryImpl extends Repository implements HomeRepository {

    private final static String SELECT_HOME_ENTITIES = String.format(
            "SELECT * FROM " +
                    "(SELECT %s,%s,'%s' AS %s FROM %s " +
                    "UNION SELECT %s,%s,'%s' AS %s FROM %s WHERE %s.%s = '%s') " +
                    "ORDER BY %s DESC",
            TrackedEntityModel.Columns.UID, TrackedEntityModel.Columns.DISPLAY_NAME,
            HomeEntity.HomeEntityType.TRACKED_ENTITY.name(), ENTITY_TYPE, TrackedEntityModel.TABLE,
            ProgramModel.Columns.UID, ProgramModel.Columns.DISPLAY_NAME, HomeEntity.HomeEntityType.PROGRAM.name(),
            ENTITY_TYPE, ProgramModel.TABLE, ProgramModel.TABLE, ProgramModel.Columns.PROGRAM_TYPE,
            ProgramType.WITHOUT_REGISTRATION.name(), ENTITY_TYPE);

    private static final String[] TABLE_NAMES = new String[]{TrackedEntityModel.TABLE, ProgramModel.TABLE};
    private static final Set<String> TABLE_SET = new HashSet<>(Arrays.asList(TABLE_NAMES));

    HomeRepositoryImpl(BriteDatabase briteDatabase) {
        super(briteDatabase);
        insertDummyData(briteDatabase);
    }

    private void insertDummyData(BriteDatabase briteDatabase) {

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.ID, 333L);
        trackedEntity.put(TrackedEntityModel.Columns.UID, "test_tracked_entity_uid");
        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_NAME, "test_tracked_entity_display_name");

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.ID, 177L);
        program.put(ProgramModel.Columns.UID, "test_program_uid");
        program.put(ProgramModel.Columns.DISPLAY_NAME, "test_program_display_name");
        program.put(ProgramModel.Columns.PROGRAM_TYPE, ProgramType.WITHOUT_REGISTRATION.name());

        briteDatabase.insert(TrackedEntityModel.TABLE, trackedEntity);
        briteDatabase.insert(ProgramModel.TABLE, program);
    }

    @Override
    public Observable<List<HomeEntity>> homeEntities() {
        return queryMultipleTables(TABLE_SET, SELECT_HOME_ENTITIES)
                .map(cursor -> {
                    List<HomeEntity> trackedEntityTypes = new ArrayList<>();
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        trackedEntityTypes.add(
                                HomeEntity.fromCursor(cursor));
                        cursor.moveToNext();
                    }

                    cursor.close();
                    return trackedEntityTypes;
                });
    }
}
