package chandlerstormo.emergensee;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GoogleActivity extends AppCompatActivity {

    public static final String POS_KEY = "position_key";
    public static final String NEW_EMERGENCY_KEY = "new_emergency_key";

    int pos;
    String newEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        Intent i = getIntent();
        pos = i.getIntExtra(POS_KEY, 0);
        newEmergency = i.getStringExtra(NEW_EMERGENCY_KEY);

        Bundle bundle = new Bundle();
        bundle.putInt(POS_KEY, pos);
        bundle.putString(NEW_EMERGENCY_KEY, newEmergency);

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = new GoogleFragment();
        f.setArguments(bundle);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container_3, f);
        ft.addToBackStack("google_fragment");
        ft.commit();

    }
}
