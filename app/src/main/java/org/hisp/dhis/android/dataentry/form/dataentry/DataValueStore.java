package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import java.util.Date;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

final class DataValueStore implements DataEntryStore {
    private static final String SELECT_EVENT = "SELECT * FROM " + EventModel.TABLE +
            " WHERE " + EventModel.Columns.UID + " = ?";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final Flowable<UserCredentialsModel> userCredentials;

    @NonNull
    private final String eventUid;

    DataValueStore(@NonNull BriteDatabase briteDatabase,
            @NonNull UserRepository userRepository,
            @NonNull CurrentDateProvider currentDateProvider,
            @NonNull String eventUid) {
        this.briteDatabase = briteDatabase;
        this.currentDateProvider = currentDateProvider;
        this.eventUid = eventUid;

        // we want to re-use results of the user credentials query
        this.userCredentials = userRepository.credentials()
                .cacheWithInitialCapacity(1);
    }

    @NonNull
    @Override
    public Flowable<Long> save(@NonNull String uid, @Nullable String value) {
        return userCredentials
                .switchMap((userCredentials) -> {
                    long updated = update(uid, value);
                    if (updated > 0) {
                        return Flowable.just(updated);
                    }

                    return Flowable.just(insert(uid, value, userCredentials.username()));
                })
                .switchMap(id -> updateEvent(id));
    }

    private long update(@NonNull String uid, @Nullable String value) {
        ContentValues dataValue = new ContentValues();

        // renderSearchResults time stamp
        dataValue.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDateProvider.currentDate()));
        if (value == null) {
            dataValue.putNull(TrackedEntityDataValueModel.Columns.VALUE);
        } else {
            dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, value);
        }

        // ToDo: write test cases for different events
        return (long) briteDatabase.update(TrackedEntityDataValueModel.TABLE, dataValue,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ? AND " +
                        TrackedEntityDataValueModel.Columns.EVENT + " = ?", uid, eventUid);
    }

    private long insert(@NonNull String uid, @Nullable String value, @NonNull String storedBy) {
        Date created = currentDateProvider.currentDate();
        TrackedEntityDataValueModel dataValueModel =
                TrackedEntityDataValueModel.builder()
                        .created(created)
                        .lastUpdated(created)
                        .dataElement(uid)
                        .event(eventUid)
                        .value(value)
                        .storedBy(storedBy)
                        .build();
        return briteDatabase.insert(TrackedEntityDataValueModel.TABLE,
                dataValueModel.toContentValues());
    }

    private Flowable<Long> updateEvent(long status) {
        return toV2Flowable(briteDatabase.createQuery(EventModel.TABLE, SELECT_EVENT, eventUid)
                .mapToOne(cursor -> EventModel.create(cursor)).take(1))
                .switchMap(eventModel -> {
                    if (State.SYNCED.equals(eventModel.state()) || State.TO_DELETE.equals(eventModel.state()) ||
                            State.ERROR.equals(eventModel.state())) {

                        ContentValues values = eventModel.toContentValues();
                        values.put(EventModel.Columns.STATE, State.TO_UPDATE.toString());

                        if (briteDatabase.update(EventModel.TABLE, values,
                                EventModel.Columns.UID + " = ?", eventUid) <= 0) {

                            throw new IllegalStateException(String.format(Locale.US, "Event=[%s] " +
                                    "has not been successfully updated", eventUid));
                        }
                    }

                    return Flowable.just(status);
                });
    }
}
