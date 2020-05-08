package com.example.fevertracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class qrScannerAdmin extends Fragment {
    admin admin;
    ZBarScannerView mScannerView;

    public void setAdmin(com.example.fevertracker.admin admin) {
        this.admin = admin;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());    // Programmatically initialize the scanner view
        admin.qrCam(mScannerView);
        return mScannerView;
    }
}
