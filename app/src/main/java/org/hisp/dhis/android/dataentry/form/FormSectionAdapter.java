package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.hisp.dhis.android.dataentry.commons.ui.DummyFragment;

import java.util.ArrayList;
import java.util.List;

class FormSectionAdapter extends FragmentStatePagerAdapter {

    @NonNull
    private final List<FormSectionViewModel> formSectionViewModelList;

    FormSectionAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.formSectionViewModelList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return DummyFragment.newInstance(formSectionViewModelList.get(position).label());
        //return DataEntryFragment.newInstance(DataEntryViewArguments.fromFormSectionViewModel());
    }

    @Override
    public int getCount() {
        return formSectionViewModelList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return formSectionViewModelList.get(position).label();
    }

    void swapData(List<FormSectionViewModel> formSectionViewArguments) {
        this.formSectionViewModelList.clear();
        this.formSectionViewModelList.addAll(formSectionViewArguments);
        notifyDataSetChanged();
    }
}