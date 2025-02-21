package com.sahansachintha.meds.helper;

import android.content.Context;
import android.util.TypedValue;

public class GeneralHelper {

    private static GeneralHelper instance;

    private GeneralHelper() {
    }

    public static synchronized GeneralHelper getInstance() {
        if (instance == null) {
            instance = new GeneralHelper();
        }
        return instance;
    }

    public int getThemeColor(Context context, int attribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attribute, typedValue, true);
        return typedValue.data;
    }
}
