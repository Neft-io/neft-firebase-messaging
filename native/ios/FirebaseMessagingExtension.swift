import UIKit
import Firebase
import UserNotifications

extension Extension.FirebaseMessaging {
    static let app = App.getApp()

    static func registerForNotifications(_ application: UIApplication) {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate

        Messaging.messaging().delegate = appDelegate
        FirebaseApp.configure()

        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = appDelegate

            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }

        application.registerForRemoteNotifications()
    }

    fileprivate static func pushToken() {
        let token = Messaging.messaging().fcmToken
        app.client.pushEvent("NeftFirebaseMessaging/Token", args: [token])
    }

    fileprivate static func onMessage(_ userInfo: [AnyHashable: Any]) {
        var title: String?
        var body: String?

        if let aps = userInfo["aps"] as? [AnyHashable: Any] {
            if let alert = aps["alert"] as? [AnyHashable: Any] {
                title = alert["title"] as? String
                body = alert["body"] as? String
            }
        }

        var data = [String: String?]()
        for (key, value) in userInfo {
            let keyStr = key as? String
            guard keyStr != nil else { continue }
            if !keyStr!.starts(with: "google.") && !keyStr!.starts(with: "gcm.") && keyStr != "aps" {
                data[keyStr!] = value as? String
            }
        }
        let dataJsonRow = try? JSONSerialization.data(withJSONObject: data)
        let dataJson = dataJsonRow.map { it in String(data: it, encoding: .ascii) } ?? nil

        app.client.pushEvent("NeftFirebaseMessaging/MessageReceived", args: [title, body, dataJson])
    }

    static func register() {
        app.client.addCustomFunction("NeftFirebaseMessaging/GetToken") { _ in
            pushToken()
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        Extension.FirebaseMessaging.onMessage(userInfo)
    }

    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        Extension.FirebaseMessaging.onMessage(userInfo)
        completionHandler(UIBackgroundFetchResult.newData)
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        Extension.FirebaseMessaging.pushToken()
    }
}
