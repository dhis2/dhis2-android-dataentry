package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextDoubleViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextIntegerViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public final class FieldViewModelFactoryImpl implements FieldViewModelFactory {

    @NonNull
    private final String hintEnterText;

    @NonNull
    private final String hintEnterLongText;

    @NonNull
    private final String hintEnterNumber;

    @NonNull
    private final String hintEnterInteger;

    @NonNull
    private final String hintEnterIntegerPositive;

    @NonNull
    private final String hintEnterIntegerNegative;

    @NonNull
    private final String hintEnterIntegerZeroOrPositive;

    public FieldViewModelFactoryImpl(@NonNull String hintEnterText, @NonNull String hintEnterLongText,
            @NonNull String hintEnterNumber, @NonNull String hintEnterInteger,
            @NonNull String hintEnterIntegerPositive, @NonNull String hintEnterIntegerNegative,
            @NonNull String hintEnterIntegerZeroOrPositive) {
        this.hintEnterText = hintEnterText;
        this.hintEnterLongText = hintEnterLongText;
        this.hintEnterNumber = hintEnterNumber;
        this.hintEnterInteger = hintEnterInteger;
        this.hintEnterIntegerPositive = hintEnterIntegerPositive;
        this.hintEnterIntegerNegative = hintEnterIntegerNegative;
        this.hintEnterIntegerZeroOrPositive = hintEnterIntegerZeroOrPositive;
    }

    @NonNull
    @Override
    public FieldViewModel create(@NonNull String id, @NonNull String label, @NonNull ValueType type,
            @NonNull Boolean mandatory, @Nullable String optionSet, @Nullable String value) {
        isNull(type, "type must be supplied");

        switch (type) {
            case BOOLEAN:
                return RadioButtonViewModel.fromRawValue(id, label, mandatory, value);
            case TRUE_ONLY:
                return CheckBoxViewModel.fromRawValue(id, label, mandatory, value);
            case TEXT:
                return EditTextViewModel.create(id, label, mandatory, value, hintEnterText, 1);
            case LONG_TEXT:
                return EditTextViewModel.create(id, label, mandatory, value, hintEnterLongText, 3);
            case NUMBER:
                return EditTextDoubleViewModel.fromRawValue(id, label, mandatory, value, hintEnterNumber);
            case INTEGER:
                return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value, hintEnterInteger,
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            case INTEGER_POSITIVE:
                return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value,
                        hintEnterIntegerPositive, InputType.TYPE_CLASS_NUMBER);
            case INTEGER_NEGATIVE:
                return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value, hintEnterIntegerNegative,
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            case INTEGER_ZERO_OR_POSITIVE:
                return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value,
                        hintEnterIntegerZeroOrPositive, InputType.TYPE_CLASS_NUMBER);
            default:
                return TextViewModel.create(id, label, type.toString());
        }
    }
}
