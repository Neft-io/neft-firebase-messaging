package io.neft.extensions.firebasemessaging_extension;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import io.neft.App;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	private final static App APP = App.getInstance();
	private static final String MESSAGE_RECEIVED_EVENT = "NeftFirebaseMessaging/MessageReceived";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// notify when the app is in foreground
		RemoteMessage.Notification notification = remoteMessage.getNotification();
		if (notification != null) {
			JSONObject data = new JSONObject(remoteMessage.getData());
			APP.getClient().pushEvent(
					MESSAGE_RECEIVED_EVENT,
					notification.getTitle(),
					notification.getBody(),
					data.toString()
			);
		}
	}
}
