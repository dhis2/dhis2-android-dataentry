package org.hisp.dhis.android.dataentry.form;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.hisp.dhis.android.dataentry.commons.ui.DummyFragment;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryViewArguments;

import java.util.ArrayList;
import java.util.List;

class FormSectionAdapter extends FragmentStatePagerAdapter {

    private final List<DataEntryViewArguments> dataEntryViewArgumentsList;

    FormSectionAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.dataEntryViewArgumentsList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return DummyFragment.newInstance(dataEntryViewArgumentsList.get(position).label());
        //return DataEntryFragment.newInstance(dataEntryViewArgumentsList.get(position));
    }

    @Override
    public int getCount() {
        return dataEntryViewArgumentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataEntryViewArgumentsList.get(position).label();
    }

    void swapData(List<DataEntryViewArguments> dataEntryViewArguments) {
        this.dataEntryViewArgumentsList.clear();
        this.dataEntryViewArgumentsList.addAll(dataEntryViewArguments);
        notifyDataSetChanged();
    }
}