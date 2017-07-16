package com.rodafleets.rodadriver.utils;

import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {

    public String serializeObject(Object myObject) {
        String serializedObject = "";
        // serialize the object
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(myObject);
            so.flush();
            serializedObject = bo.toString();
        } catch (Exception e) {

        }

        return serializedObject;
    }

    public static Object deSerializeObject(String serializedString) {
        Object obj = null;
        // deserialize the object
        try {
            byte b[] = serializedString.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = (Object) si.readObject();
        } catch (Exception e) {

        }

        return obj;
    }

    public static void enableWindowActivity(Window window, Boolean enable) {
        if(enable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else{
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
