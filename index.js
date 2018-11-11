const { util, signal, callNativeFunction, onNativeEvent } = require('@neft/core')

let deviceToken
util.defineProperty(exports, 'deviceToken', null, () => deviceToken, null)
signal.create(exports, 'onDeviceTokenChange')
callNativeFunction('NeftFirebaseMessaging/GetToken')
onNativeEvent('NeftFirebaseMessaging/Token', (newToken) => {
	const oldToken = deviceToken
	deviceToken = newToken
	exports.onDeviceTokenChange.emit(oldToken)
})

signal.create(exports, 'onMessageReceived')
onNativeEvent('NeftFirebaseMessaging/MessageReceived', (title, body, data) => {
	exports.onMessageReceived.emit({
		title,
		body,
		data: typeof data === 'string' && data ? JSON.parse(data) : {}
	})
})
