package chandlerstormo.emergensee;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChooseEmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emergency);

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = new ChooseEmergencyFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container_1, f);
        ft.commit();
    }
}
