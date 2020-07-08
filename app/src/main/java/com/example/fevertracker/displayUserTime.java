package com.example.fevertracker;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialogFragment;

public class displayUserTime extends AppCompatDialogFragment {
    TextView Time;
    String timeString;

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.display_user_time, null);

        Time = view.findViewById(R.id.timeForUser);
        if (!timeString.isEmpty()) {
            Time.setText(timeString);
        }

        Dialog d = new Dialog(getActivity());
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(view);

        return d;
    }
}
