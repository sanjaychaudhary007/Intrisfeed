package com.archi.intrisfeed.util;

import org.json.JSONException;

/**
 * Created by archi_info on 11/14/2016.
 */

public interface Listner {
    void onSuccess(Object object);
    void onFail(Object object) throws JSONException;
}
