package com.moment.mobileplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.moment.mobileplayer.base.BasePager;

public class MyFragment extends Fragment {
    private BasePager basePager;

    public MyFragment(BasePager basePager) {
        this.basePager = basePager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (basePager != null) {
            return basePager.rootView;
        }
        return null;
    }
}
