package com.score.homez.utils;

import android.content.Context;

import com.score.homez.exceptions.NoUserException;
import com.score.homez.pojos.Switch;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by eranga on 2/2/16.
 */
public class SenzUtils {
    public static Senz createPutSenz(Switch aSwitch, Context context) {
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put(aSwitch.getName(), aSwitch.getStatus() == 0 ? "on" : "off");
        senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(context);

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;

            return new Senz(id, signature, senzType, null, receiver, senzAttributes);
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Senz createGetSenz(ArrayList<Switch> switchList, Context context) {
        return null;
    }
}
