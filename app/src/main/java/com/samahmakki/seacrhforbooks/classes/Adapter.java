package com.samahmakki.seacrhforbooks.classes;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.samahmakki.seacrhforbooks.R;
import com.samahmakki.seacrhforbooks.fragments.SavedBooksFragment;
import com.samahmakki.seacrhforbooks.fragments.SearchBooksFragment;

public class Adapter extends FragmentPagerAdapter {

    private Context mContext;

    public Adapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new SearchBooksFragment();
        }  else {
            return new SavedBooksFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.search);
        }  else {
            return mContext.getString(R.string.saved);
        }
    }

}
