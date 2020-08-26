package itms.com.pe.app.Culqi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by culqi on 2/7/17.
 */

public interface TokenCallback {

    public void onSuccess(JSONObject token) throws JSONException;

    public void onError(Exception error);

}
