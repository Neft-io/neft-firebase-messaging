package io.neft.extensions.firebasemessaging_extension;

import com.google.firebase.iid.FirebaseInstanceId;
import io.neft.App;
import io.neft.utils.Consumer;

public final class FirebaseMessagingExtension {
    private static final App APP = App.getInstance();
    private static final String GET_TOKEN_FUNC = "NeftFirebaseMessaging/GetToken";
    private static final String TOKEN_EVENT = "NeftFirebaseMessaging/Token";

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
    }
}
