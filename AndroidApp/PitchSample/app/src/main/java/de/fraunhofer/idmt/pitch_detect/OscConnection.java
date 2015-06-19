package de.fraunhofer.idmt.pitch_detect;

/**
 * Created by balazsbela on 6/18/15.
 */

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.illposed.osc.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class OscConnection {

    public OscConnection(MainActivity parent)
    {
        this.parent = parent;
    }


    private MainActivity parent;
    private OSCPortOut sender;

    public void sendMessage(float currentFreq) {

        if (sender == null) {

            try {

                SharedPreferences preferences = parent.getSharedPreferences("settings", MainActivity.MODE_PRIVATE);

                String host = preferences.getString("host", "");
                long port = preferences.getLong("port", 7000);
                sender = new OSCPortOut(InetAddress.getByName(host), (int)port);
            }
            catch(Exception e)
            {
                Log.e("Main", "Couldn't connect to osc server:" + e.getMessage());
            }

        }

        List<Object> args = new ArrayList<Object>();
        args.add(currentFreq);

        OSCMessage msg = new OSCMessage("/pitch", args);
        try {
            sender.send(msg);
        } catch (Exception e) {
            Log.e("Main", "Couldn't connect to osc server!");
        }

    }
}
