package com.jookovjook.base.DataProvider;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DataProviderFragment extends Fragment {
    private AbstractDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mDataProvider = new DataProvider(getActivity());
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }
}
