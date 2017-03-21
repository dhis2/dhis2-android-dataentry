package org.hisp.dhis.android.dataentry.main.home;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import io.reactivex.observers.TestObserver;
import rx.internal.schedulers.TrampolineScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class HomeRepositoryIntegrationTests {

    private ContentValues trackedEntity;
    private ContentValues program;
    private ContentValues programWithRegistration;
    private HomeRepository homeRepository;

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(TrampolineScheduler.INSTANCE);

    @Before
    public void setUp() throws IOException {
        trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.ID, 333L);
        trackedEntity.put(TrackedEntityModel.Columns.UID, "test_tracked_entity_uid");
        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_NAME, "test_tracked_entity_display_name");

        program = new ContentValues();
        program.put(ProgramModel.Columns.ID, 177L);
        program.put(ProgramModel.Columns.UID, "test_program_uid");
        program.put(ProgramModel.Columns.DISPLAY_NAME, "test_program_display_name");
        program.put(ProgramModel.Columns.PROGRAM_TYPE, ProgramType.WITHOUT_REGISTRATION.name());

        programWithRegistration = new ContentValues();
        programWithRegistration.put(ProgramModel.Columns.ID, 144L);
        programWithRegistration.put(ProgramModel.Columns.UID, "test_program_uid");
        programWithRegistration.put(ProgramModel.Columns.DISPLAY_NAME, "test_program_display_name");
        programWithRegistration.put(ProgramModel.Columns.PROGRAM_TYPE, ProgramType.WITH_REGISTRATION.name());

        homeRepository = new HomeRepositoryImpl(databaseRule.briteDatabase());
    }

    @Test
    public void homeViewModelsShouldReturnCorrectTypes() {

        databaseRule.database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        databaseRule.database().insert(ProgramModel.TABLE, null, program);


        TestObserver<List<HomeViewModel>> testObserver = homeRepository.homeViewModels().test();

        testObserver.assertValueCount(1); // One list is returned

        assertThat(testObserver.values().get(0).size()).isEqualTo(2); // List contains two HomeViewModels

        // TrackedEntity type should be returned first
        assertThat(testObserver.values().get(0).get(0).type()).isEqualTo(HomeViewModel.Type.TRACKED_ENTITY);

        // Program type should be returned last
        assertThat(testObserver.values().get(0).get(1).type()).isEqualTo(HomeViewModel.Type.PROGRAM);

        testObserver.assertNoErrors();

        testObserver.assertNotComplete();

    }

    @Test
    public void homeViewModelsShouldObserveChangesInTrackedEntityTable() {

        databaseRule.database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        databaseRule.database().insert(ProgramModel.TABLE, null, program);

        TestObserver<List<HomeViewModel>> testObserver = homeRepository.homeViewModels().test();

        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_NAME, "test_another_tracked_entity_display_name");

        int updated = databaseRule.briteDatabase().update(TrackedEntityModel.TABLE, trackedEntity,
                TrackedEntityModel.Columns.ID + " = ?", String.valueOf(333L));

        assertThat(updated).isEqualTo(1);
        testObserver.assertValueCount(2);

        assertThat(testObserver.values().get(0).get(0).title()).isEqualTo("test_tracked_entity_display_name");
        assertThat(testObserver.values().get(1).get(0).title()).isEqualTo("test_another_tracked_entity_display_name");

        testObserver.assertNoErrors();

        testObserver.assertNotComplete();

    }

    @Test
    public void homeViewModelsShouldObserveChangesInProgramTable() {

        databaseRule.database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        databaseRule.database().insert(ProgramModel.TABLE, null, program);

        TestObserver<List<HomeViewModel>> testObserver = homeRepository.homeViewModels().test();

        program.put(ProgramModel.Columns.DISPLAY_NAME, "test_another_program_display_name");

        int updated = databaseRule.briteDatabase().update(ProgramModel.TABLE, program,
                ProgramModel.Columns.ID + " = ?", String.valueOf(177L));

        assertThat(updated).isEqualTo(1);
        testObserver.assertValueCount(2);

        assertThat(testObserver.values().get(0).get(1).title()).isEqualTo("test_program_display_name");
        assertThat(testObserver.values().get(1).get(1).title()).isEqualTo("test_another_program_display_name");

        testObserver.assertNoErrors();

        testObserver.assertNotComplete();

    }

    @Test
    public void homeViewModelsShouldNotReturnProgramsWithRegistration() {

        databaseRule.database().insert(ProgramModel.TABLE, null, programWithRegistration);

        TestObserver<List<HomeViewModel>> testObserver = homeRepository.homeViewModels().test();

        testObserver.assertValueCount(1);
        assertThat(testObserver.values().get(0).size()).isEqualTo(0); // List should be empty

        testObserver.assertNoErrors();

        testObserver.assertNotComplete();

    }
}