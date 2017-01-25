/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.dataentry.drawerform.form;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.drawerform.form.common.FormEntity;
import org.hisp.dhis.android.dataentry.drawerform.form.common.RowView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static android.text.TextUtils.isEmpty;

public final class EditTextRowView implements RowView {

    public EditTextRowView() {
        // explicit empty constructor
    }

    @Override
    public ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new EditTextRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityEditText entity = (FormEntityEditText) formEntity;
        ((EditTextRowViewHolder) viewHolder).update(entity);
    }

    protected static class EditTextRowViewHolder extends RecyclerView.ViewHolder {

        private FormEntityEditText formEntity;

        /* in order to improve performance, we pre-fetch
        all hints from resources */
        final SparseArray<String> hintCache;

        @BindView(R.id.textview_row_label)
        public TextView textViewLabel;

        @BindView(R.id.edittext_row_textinputlayout)
        public TextInputLayout textInputLayout;

        @BindView(R.id.edittext_row_edittext)
        public EditText editText;

        /* we use OnFocusChangeListener in order to hide
        hint from user when row is not focused */
        public final OnFocusChangeListener onFocusChangeListener;

        public EditTextRowViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            Context context = itemView.getContext();

            // caching hint strings
            hintCache = new SparseArray<>();
            hintCache.append(R.string.enter_text, context.getString(R.string.enter_text));
            hintCache.append(R.string.enter_long_text, context.getString(R.string.enter_long_text));
            hintCache.append(R.string.enter_number, context.getString(R.string.enter_number));
            hintCache.append(R.string.enter_integer, context.getString(R.string.enter_integer));
            hintCache.append(R.string.enter_positive_integer, context.getString(R.string.enter_positive_integer));
            hintCache.append(R.string.enter_positive_integer_or_zero, context.getString(R.string.enter_positive_integer_or_zero));
            hintCache.append(R.string.enter_negative_integer, context.getString(R.string.enter_negative_integer));

            onFocusChangeListener = new OnFocusChangeListener(textInputLayout, editText);

            editText.setOnFocusChangeListener(onFocusChangeListener);
        }

        @OnTextChanged(callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED, value = {
                R.id.edittext_row_edittext
        })
        public void onTextChanged(Editable editable) {
            if (formEntity != null) {
                formEntity.setValue(editable.toString(), true);
            }
        }

        public void update(FormEntityEditText entity) {
            // update callbacks with current entities
            formEntity = entity;
            textViewLabel.setText(entity.getLabel());
            editText.setText(entity.getValue());
            editText.setEnabled(!entity.isLocked());

            // configure edittext according to entity
            configureView(entity);
        }

        private void configureView(FormEntityEditText formEntityEditText) {

            String hint = isEmpty(formEntityEditText.getHint()) ?
                    hintCache.get(formEntityEditText.getHintResourceId()) : formEntityEditText.getHint();

            String textInputLayoutHint = isEmpty(editText.getText()) ? hint : null;

            onFocusChangeListener.setHint(hint);
            textInputLayout.setHint(textInputLayoutHint);

            editText.setInputType(formEntityEditText.getAndroidInputType());
            editText.setMaxLines(formEntityEditText.getMaxLines());
        }
    }

    private static class OnFocusChangeListener implements View.OnFocusChangeListener {
        private final TextInputLayout textInputLayout;
        private final EditText editText;
        private CharSequence hint;

        public OnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
            this.textInputLayout = inputLayout;
            this.editText = editText;
        }

        public void setHint(CharSequence hint) {
            this.hint = hint;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                textInputLayout.setHint(hint);
            } else {
                if (!isEmpty(editText.getText().toString())) {
                    textInputLayout.setHint(null);
                }
            }
        }
    }
}
