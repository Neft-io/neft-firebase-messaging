package io.neft.extensions.firebasemessaging_extension;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import io.neft.App;
import io.neft.utils.Consumer;

public final class FirebaseMessagingExtension {
    private static final App APP = App.getInstance();
    private static final String GET_TOKEN_FUNC = "NeftFirebaseMessaging/GetToken";
    private static final String TOKEN_EVENT = "NeftFirebaseMessaging/Token";
    private static final String REGISTER = "NeftFirebaseMessaging/Register";
    private static final String MESSAGE_RECEIVED_EVENT = "NeftFirebaseMessaging/MessageReceived";

    static void pushToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        APP.getClient().pushEvent(TOKEN_EVENT, token);
    }

    public static void register() {
        APP.getClient().addCustomFunction(GET_TOKEN_FUNC, new Consumer<Object[]>() {
            @Override
            public void accept(Object[] var) {
                pushToken();
            }
        });

        APP.getClient().addCustomFunction(REGISTER, new Consumer<Object[]>() {
            @Override
            public void accept(Object[] var) {
                // nothing to do on Android
            }
        });

        lookForMessageInIntent();
    }

    private static void lookForMessageInIntent() {
        Intent intent = APP.getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.get("google.message_id") != null) {
            JSONObject data = new JSONObject();
            for (String key : bundle.keySet()) {
                if (key.startsWith("google.")) continue;
                if (key.equals("collapse_key")) continue;
                if (key.equals("from")) continue;
                Object value = bundle.get(key);
                if (value == null) continue;
                try {
                    data.put(key, value.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            onMessageReceived(data);
        }
    }

    static void onMessageReceived(JSONObject data) {
        System.out.println(data.toString());
        APP.getClient().pushEvent(MESSAGE_RECEIVED_EVENT, data.toString());
    }
}
