package org.hisp.dhis.android.dataentry.form.section.viewmodels;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.utils.Preconditions;
import org.hisp.dhis.android.dataentry.form.section.EditTextHintCache;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.coordinate.CoordinateViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.date.DateViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.optionset.OptionSetViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.radiobutton.RadioButtonViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_TEXT;

final public class FormItemViewModelFactoryImpl implements FormItemViewModelFactory {

    private final static String EMPTY_STRING = "";
    private final static String TRUE = "TRUE";

    private final EditTextHintCache editTextHintCache;

    public FormItemViewModelFactoryImpl(@NonNull EditTextHintCache editTextHintCache) {
        this.editTextHintCache = editTextHintCache;
    }

    @Override
    public FormItemViewModel fromCursor(@NonNull Cursor cursor) {
        return create(cursor.getString(0), cursor.getString(1), integerToBoolean(cursor.getInt(2)), cursor.getString(3),
                ValueType.valueOf(cursor.getString(4)), cursor.getString(5));
    }

    @Override
    public FormItemViewModel create(@NonNull String uid, @NonNull String label, @NonNull Boolean mandatory,
            @Nullable String value, @NonNull ValueType valueType,
            @Nullable String optionSet) {

        Preconditions.isNull(valueType, "Unsupported ValueType: 'NULL' for: " + label + " - " + uid);

        if (isOptionSet(optionSet)) {
            return OptionSetViewModel.create(uid, label, mandatory, optionSet,
                    editTextHintCache.hint(R.string.enter_option_set));
        } else if (isEditTextType(valueType)) {
            return createEditTextViewModel(uid, label, mandatory, value, valueType);
        } else {
            switch (valueType) {
                case BOOLEAN:
                    return createRadioButtonViewModel(uid, label, mandatory, value);
                case TRUE_ONLY:
                    return CheckBoxViewModel.create(uid, label, mandatory,
                            value != null && TRUE.equals(value.toUpperCase(Locale.ENGLISH)));
                case DATE:
                    return DateViewModel.create(uid, label, mandatory, value == null ? EMPTY_STRING : value);
                case COORDINATE:
                    return createCoordinateViewModel(uid, label, mandatory, value);
                default:
                    throw new IllegalArgumentException("Unsupported ValueType: '" + valueType.name() +
                            "' for: " + label + " - " + uid);
            /* TODO: implement later
            case LETTER:
                break;
            case EMAIL:
                break;
            case DATETIME:
                break;
            case TIME:
                break;
            case UNIT_INTERVAL:
                break;
            case PERCENTAGE:
                break;
            case TRACKER_ASSOCIATE:
                break;
            case USERNAME:
                break;
            case FILE_RESOURCE:
                break;
            case ORGANISATION_UNIT:
                break;
            case AGE:
                break;
            case URL:
                break; */
            }
        }

    }

    @NonNull
    private FormItemViewModel createRadioButtonViewModel(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        Boolean boolValue;
        if (value == null || value.equals(EMPTY_STRING)) {
            boolValue = null;
        } else {
            boolValue = TRUE.equals(value.toUpperCase(Locale.ENGLISH));
        }
        return RadioButtonViewModel.create(uid, label, mandatory, boolValue);
    }

    @NonNull
    private FormItemViewModel createCoordinateViewModel(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        Double latitude;
        Double longitude;

        if (value != null && value.split(",").length == 2) {
            String[] latLong = value.split(",");
            // exception is thrown if lat or long is not a number
            latitude = Double.parseDouble(latLong[0]);
            longitude = Double.parseDouble(latLong[1]);
        } else {
            latitude = longitude = 0.0;
        }

        return CoordinateViewModel.create(uid, label, mandatory, latitude.toString(), longitude.toString());
    }

    private static boolean isOptionSet(String optionSet) {
        return optionSet != null;
    }

    private static boolean isEditTextType(ValueType valueType) {
        return (valueType.isText() || valueType.isInteger() || valueType.isNumeric()) && !valueType.isCoordinate();
    }

    @NonNull
    private EditTextViewModel createEditTextViewModel(String uid, String label, Boolean mandatory, String value,
            ValueType valueType) {
        // default values
        EditTextViewModel formViewModel = null;
        Integer inputType = TYPE_CLASS_TEXT;
        Integer maxLines = 1;
        String hint;
        List<InputFilter> inputFilters = new ArrayList<>(); // todo: use inputfilters when needed

        switch (valueType) {
            case TEXT:
                hint = editTextHintCache.hint(R.string.enter_text);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case LONG_TEXT:
                maxLines = 3;
                hint = editTextHintCache.hint(R.string.enter_long_text);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case NUMBER:
                inputType = InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED;
                hint = editTextHintCache.hint(R.string.enter_number);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case INTEGER:
                inputType = InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED;
                hint = editTextHintCache.hint(R.string.enter_integer);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case INTEGER_POSITIVE:
                inputType = InputType.TYPE_CLASS_NUMBER;
                hint = editTextHintCache.hint(R.string.enter_positive_integer);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case INTEGER_NEGATIVE:
                inputType = InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED;
                hint = editTextHintCache.hint(R.string.enter_negative_integer);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            case INTEGER_ZERO_OR_POSITIVE:
                inputType = InputType.TYPE_CLASS_NUMBER;
                hint = editTextHintCache.hint(R.string.enter_positive_integer_or_zero);
                formViewModel = EditTextViewModel.create(uid, label, mandatory, value, inputType, maxLines, hint,
                        inputFilters);
                break;
            default:
                break;
        }

        Preconditions.isNull(formViewModel, "Unsupported ValueType: '" + valueType.name() + "'");

        return formViewModel;
    }

    private static Boolean integerToBoolean(int integer) {
        return integer == 1;
    }
}
