package chandlerstormo.emergensee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.SearchManager;

public class GoogleFragment extends Fragment {

    public static final String TAG = GoogleFragment.class.getSimpleName();
    public static final String POS_KEY = "position_key";
    public static final String NEW_EMERGENCY_KEY = "new_emergency_key";

    Button mBack;

    int pos;
    String newEmergency;
    String emergency;

    public GoogleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_google, container, false);

        mBack = (Button) v.findViewById(R.id.button_back);

        // get emergency position from bundle args
        pos = getArguments().getInt(POS_KEY);
        newEmergency = getArguments().getString(NEW_EMERGENCY_KEY);

        if (pos >= 0) {
            final EmergenciesSingleton s = EmergenciesSingleton.get(getActivity().getApplication());
            emergency = s.getEmergency(pos);
        }
        else {
            emergency = newEmergency;
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        try {
            Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
            i.putExtra(SearchManager.QUERY, emergency + " first aid");
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

}
