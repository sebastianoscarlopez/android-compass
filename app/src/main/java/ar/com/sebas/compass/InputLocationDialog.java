package ar.com.sebas.compass;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sebas on 3/11/2017.
 *
 * Input destination location
 *
 */

public class InputLocationDialog extends DialogFragment {
    private EditText inputLatitude;
    private EditText inputLongitude;
    private CompassActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_input_location, container, false);
        getDialog().setTitle("Input Coordinates");

        Button btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClick();
            }
        });
        inputLatitude = (EditText)rootView.findViewById(R.id.inputLatitude);
        inputLongitude = (EditText)rootView.findViewById(R.id.inputLongitude);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
        activity = (CompassActivity)getActivity();
        return rootView;
    }

    void onSearchClick()
    {
        Log.d("onSearchClick", inputLatitude.getText().toString());
        try {
            double latitude = Double.parseDouble(inputLatitude.getText().toString());
            double longitude = Double.parseDouble(inputLongitude.getText().toString());
            activity.onChangeInputLocation(latitude, longitude);
            dismiss();
        }catch (Exception ex)
        {
            Toast.makeText(getContext(), "Incorret coordinates", Toast.LENGTH_SHORT);
        }
    }
}
