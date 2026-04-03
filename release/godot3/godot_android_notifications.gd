extends Node

var _notifications : JNISingleton = null

signal alarm_permission_status_changed(granted)
signal notification_permission_status_changed(granted)

func output(message) -> void:
	print("%s: %s" % [name, message])

func _ready() -> void:
	if Engine.has_singleton("GodotNotification"):
		output("singleton found")
		_notifications = Engine.get_singleton("GodotNotification")
		connectSignals()
	else:
		output("singleton not found")

func connectSignals() -> void:
	_notifications.connect("alarm_permission_status_changed", self, "_onAlarmPermissionStatusChanged")
	_notifications.connect("notification_permission_status_changed", self, "_onNotificationPermissionStatusChanged")

# Returns true if app has permissions for notifications
func isNotificationPermissionGranted() -> bool:
	return _notifications.isNotificationPermissionGranted()

# Asks user to give the app POST_NOTIFICATION permission
# Only does this if it's not granted and Android API level requires so
func requestPushNotificationsPermission() -> void:
	_notifications.requestPushNotificationsPermission()

# Returns true if SCHEDULE_EXACT_ALARM permission was granted or it's not necessary
func isExactAlarmPermissionGranted() -> bool:
	return _notifications.isExactAlarmPermissionGranted()

# Asks user to give the app SCHEDULE_EXACT_ALARM permission
# Only does this if it's not granted and Android API level requires so
func requestExactAlarmPermission() -> void:
	_notifications.requestExactAlarmPermission()

# Creates a notification to be shown after "intervalSeconds" with a given tag.
# If you want to create few parallel notifications, make sure that you use different tags.
# A tag is any integer number.
func scheduleNotification(message:String, header:String, intervalSeconds:int, tag:int) -> void:
	_notifications.scheduleNotification(message, header, intervalSeconds, tag)

# Same as scheduleNotification(), but this type of notifications fires EXACTLY at provided time
# Requires you to have the SCHEDULE_EXACT_ALARM permission, otherwise throws a security error
func scheduleNotificationExact(message:String, header:String, intervalSeconds:int, tag:int) -> void:
	_notifications.scheduleNotificationExact(message, header, intervalSeconds, tag)

# Cancels a notification with given tag.
# It's safe to cancel an unexistent notification (nothing will happen).
func cancelNotification(tag:int) -> void:
	_notifications.cancelNotification(tag)

func _onAlarmPermissionStatusChanged(granted:bool) -> void:
	emit_signal("alarm_permission_status_changed", granted)

func _onNotificationPermissionStatusChanged(granted:bool) -> void:
	emit_signal("notification_permission_status_changed", granted)
