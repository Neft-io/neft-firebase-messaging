const { utils, signal } = Neft
const { callNativeFunction, onNativeEvent } = Neft.native

let deviceToken
utils.defineProperty(exports, 'deviceToken', null, () => deviceToken, null)
signal.create(exports, 'onDeviceTokenChange')
callNativeFunction('extensionFirebaseMessagingGetToken')
onNativeEvent('extensionFirebaseMessagingToken', (newToken) => {
	const oldToken = deviceToken
	deviceToken = newToken
	exports.onDeviceTokenChange.emit(oldToken)
})

signal.create(exports, 'onMessageReceived')
onNativeEvent('extensionFirebaseMessagingMessageReceived', (title, body, data) => {
	exports.onMessageReceived.emit({
		title,
		body,
		data: typeof data === 'string' && data ? JSON.parse(data) : {}
	})
})
