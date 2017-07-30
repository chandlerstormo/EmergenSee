package chandlerstormo.emergensee;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EmergenciesSingleton {

    private Context mAppContext;
    private ArrayList<String> mEmergencies;
    private static EmergenciesSingleton sEmergencies;

    private EmergenciesSingleton (Context c) {
        mAppContext = c;
        mEmergencies = new ArrayList<>();
        populateEmergencies();
    }

    private void populateEmergencies() {
        try {
            JSONArray array = new JSONObject(readJSON()).getJSONArray("emergencies");
            for (int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.has("name")) {
                    mEmergencies.add(object.getString("name"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readJSON() {
        String json = null;
        try {
            InputStream is = mAppContext.getAssets().open("document.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public static EmergenciesSingleton get (Context c) {
        if (sEmergencies == null) {
            sEmergencies = new EmergenciesSingleton(c.getApplicationContext());
        }
        return sEmergencies;
    }

    public ArrayList<String> getEmergencies() {
        return mEmergencies;
    }

    public String getEmergency(int index) {
        return mEmergencies.get(index);
    }

    public void addEmergency(String emergency) {
        mEmergencies.add(emergency);
    }
}
