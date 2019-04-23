const { util, SignalDispatcher, callNativeFunction, onNativeEvent } = require('@neft/core')

let deviceToken
util.defineProperty(exports, 'deviceToken', null, () => deviceToken, null)
exports.onDeviceTokenChange = new SignalDispatcher()
callNativeFunction('NeftFirebaseMessaging/GetToken')
onNativeEvent('NeftFirebaseMessaging/Token', (newToken) => {
	const oldToken = deviceToken
	deviceToken = newToken
	exports.onDeviceTokenChange.emit(oldToken)
})

exports.onMessageReceived = new SignalDispatcher()
onNativeEvent('NeftFirebaseMessaging/MessageReceived', (data) => {
  const payload = typeof data === 'string' && data ? JSON.parse(data) : {}
  console.log('NEW FIREBASE MESSAGE', payload)
	exports.onMessageReceived.emit(payload)
})

exports.register = () => {
  callNativeFunction('NeftFirebaseMessaging/Register')
}
