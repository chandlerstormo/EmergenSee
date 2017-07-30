package chandlerstormo.emergensee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

public class AddFragment extends Fragment {

    private static final String PREF_FILENAME = "itp341.preferences.app_prefs";
    private static final String PREF_RECENT_EMERGENCIES = "emergensee.preferences.recent_emergencies";

    Button mGo;
    EditText mAdd;
    Set<String> mSet;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add, container, false);

        mGo = (Button) v.findViewById(R.id.add_button);
        mAdd = (EditText) v.findViewById(R.id.add_emergency);
        mSet = new HashSet<>();

        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // push added emergency into shared preferences
                Set<String> set;
                Set<String> defaulter = null;
                SharedPreferences prefs = getActivity().getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
                set = prefs.getStringSet(PREF_RECENT_EMERGENCIES, defaulter);
                if (set != null) {
                    String[] array = set.toArray(new String[set.size()]);
                    for (int i = 0; i < array.length; i++) {
                        mSet.add(array[i]);
                    }
                }
                mSet.add(mAdd.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet(PREF_RECENT_EMERGENCIES, mSet);
                editor.commit();
                Intent i = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                i.putExtra(MapActivity.NEW_EMERGENCY_KEY, mAdd.getText().toString());
                i.putExtra(MapActivity.POS_KEY, -1);
                startActivity(i);
            }
        });

        return v;
    }

}