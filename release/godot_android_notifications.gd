extends Node

var _notifications : JNISingleton = null

func output(message) -> void:
	print("%s: %s" % [name, message])

func _ready() -> void:
	if Engine.has_singleton("GodotNotification"):
		output("singleton found")
		_notifications = Engine.get_singleton("GodotNotification")
	else:
		output("singleton not found")

# Creates a notification to be shown after "intervalSeconds" with a given tag.
# If you want to create few parallel notifications, make sure that you use different tags.
# A tag is any integer number.
func scheduleNotification(message:String, header:String, intervalSeconds:int, tag:int) -> void:
	if !_notifications:
		return
	_notifications.scheduleNotification(message, header, intervalSeconds, tag)

# Cancels a notification with given tag.
# It's safe to cancel an unexistent notification (nothing will happen).
func cancelNotification(tag:int) -> void:
	if !_notifications:
		return
	_notifications.cancelNotification(tag)
