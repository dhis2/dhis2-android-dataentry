package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.daterow.DateViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextDoubleViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextIntegerViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.optionsrow.OptionsViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;
import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

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

    @NonNull
    private final String hintFilterOptions;

    @NonNull
    private final String hintChooseDate;

    public FieldViewModelFactoryImpl(@NonNull String hintEnterText, @NonNull String hintEnterLongText,
            @NonNull String hintEnterNumber, @NonNull String hintEnterInteger,
            @NonNull String hintEnterIntegerPositive, @NonNull String hintEnterIntegerNegative,
            @NonNull String hintEnterIntegerZeroOrPositive, @NonNull String filterOptions,
            @NonNull String hintChooseDate) {
        this.hintEnterText = hintEnterText;
        this.hintEnterLongText = hintEnterLongText;
        this.hintEnterNumber = hintEnterNumber;
        this.hintEnterInteger = hintEnterInteger;
        this.hintEnterIntegerPositive = hintEnterIntegerPositive;
        this.hintEnterIntegerNegative = hintEnterIntegerNegative;
        this.hintEnterIntegerZeroOrPositive = hintEnterIntegerZeroOrPositive;
        this.hintFilterOptions = filterOptions;
        this.hintChooseDate = hintChooseDate;
    }

    @NonNull
    @Override
    @SuppressWarnings({
            "PMD.CyclomaticComplexity",
            "PMD.StdCyclomaticComplexity"
    })
    public FieldViewModel create(@NonNull String id, @NonNull String label, @NonNull ValueType type,
            @NonNull Boolean mandatory, @Nullable String optionSet, @Nullable String value) {
        isNull(type, "type must be supplied");

        if (!isEmpty(optionSet)) {
            return createOption(id, label, mandatory, optionSet, value);
        }

        switch (type) {
            case BOOLEAN:
                return RadioButtonViewModel.fromRawValue(id, label, mandatory, value);
            case TRUE_ONLY:
                return CheckBoxViewModel.fromRawValue(id, label, mandatory, value);
            case TEXT:
                return createText(id, label, mandatory, value);
            case LONG_TEXT:
                return createLongText(id, label, mandatory, value);
            case NUMBER:
                return createNumber(id, label, mandatory, value);
            case INTEGER:
                return createInteger(id, label, mandatory, value);
            case INTEGER_POSITIVE:
                return createIntegerPositive(id, label, mandatory, value);
            case INTEGER_NEGATIVE:
                return createIntegerNegative(id, label, mandatory, value);
            case INTEGER_ZERO_OR_POSITIVE:
                return createIntegerZeroOrPositive(id, label, mandatory, value);
            case DATE:
                return createDate(id, label, mandatory, value);
            case DATETIME:
                return createDateTime(id, label, mandatory, value);
            default:
                return TextViewModel.create(id, label, type.toString());
        }
    }

    @NonNull
    private DateViewModel createDate(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return DateViewModel.forDate(uid, label, hintChooseDate, mandatory, value);
    }

    @NonNull
    private DateViewModel createDateTime(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return DateViewModel.forDateTime(uid, label, hintChooseDate, mandatory, value);
    }

    @NonNull
    private OptionsViewModel createOption(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @NonNull String optionSet, @Nullable String value) {
        return OptionsViewModel.create(id, label, hintFilterOptions, mandatory, optionSet, value);
    }

    @NonNull
    private EditTextViewModel createText(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextViewModel.create(id, label, mandatory, value, hintEnterText, 1);
    }

    @NonNull
    private EditTextViewModel createLongText(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextViewModel.create(id, label, mandatory, value, hintEnterLongText, 3);
    }

    @NonNull
    private EditTextDoubleViewModel createNumber(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextDoubleViewModel.fromRawValue(id, label, mandatory, value, hintEnterNumber);
    }

    @NonNull
    private EditTextIntegerViewModel createInteger(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value, hintEnterInteger,
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @NonNull
    private EditTextIntegerViewModel createIntegerPositive(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value,
                hintEnterIntegerPositive, InputType.TYPE_CLASS_NUMBER);
    }

    @NonNull
    private EditTextIntegerViewModel createIntegerNegative(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value, hintEnterIntegerNegative,
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @NonNull
    private EditTextIntegerViewModel createIntegerZeroOrPositive(@NonNull String id, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value) {
        return EditTextIntegerViewModel.fromRawValue(id, label, mandatory, value,
                hintEnterIntegerZeroOrPositive, InputType.TYPE_CLASS_NUMBER);
    }
}
