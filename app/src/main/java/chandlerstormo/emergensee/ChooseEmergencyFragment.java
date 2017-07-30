package chandlerstormo.emergensee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Set;

public class ChooseEmergencyFragment extends Fragment {

    public static final String TAG = ChooseEmergencyFragment.class.getSimpleName();
    private static final String PREF_FILENAME = "itp341.preferences.app_prefs";
    private static final String PREF_RECENT_EMERGENCIES = "emergensee.preferences.recent_emergencies";

    ArrayList<String> mEmergencies;
    ArrayAdapter<String> mAdapter;
    ListView emergencyList;
    Button mGo;
    Button mAddEmergency;

    int pos;

    public ChooseEmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choose_emergency, container, false);

        emergencyList = (ListView) v.findViewById(R.id.emergency_list);
        mGo = (Button) v.findViewById(R.id.button_go);
        mAddEmergency = (Button) v.findViewById(R.id.add_emergency_button);

        mGo.setEnabled(false);

        final EmergenciesSingleton s = EmergenciesSingleton.get(getActivity().getApplicationContext());
        mEmergencies = s.getEmergencies();

        // get previously added emergencies from shared preferences
        Set<String> set;
        Set<String> defaulter = null;
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_FILENAME,  Context.MODE_PRIVATE);

        set = prefs.getStringSet(PREF_RECENT_EMERGENCIES, defaulter);
        if (set != null) {
            String[] array = set.toArray(new String[set.size()]);
            for (int i = 0; i < array.length; i++) {
                s.addEmergency(array[i]);
            }
        }

        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, mEmergencies);
        emergencyList.setAdapter(mAdapter);
        this.emergencyList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        emergencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                mGo.setEnabled(true);
            }
        });

        mAddEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddActivity.class);
                startActivity(i);
            }
        });

        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplication(), MapActivity.class);
                i.putExtra(MapActivity.POS_KEY, pos);
                startActivity(i);
            }
        });

        return v;
    }
}


