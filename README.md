# Godot Android Notifications for 3.6
[![Godot](https://img.shields.io/badge/Godot%20Engine-3.6-blue?style=for-the-badge&logo=godotengine&logoSize=auto)](https://godotengine.org/)
[![GitHub License](https://img.shields.io/github/license/damnedpie/godot-android-notifications?style=for-the-badge)](#)
[![GitHub Repo stars](https://img.shields.io/github/stars/damnedpie/godot-android-notifications?style=for-the-badge&logo=github&logoSize=auto&color=%23FFD700)](#)

Godot Android Notifications plugin. Built on Godot 3.6 AAR.

## Setup

### Project integration

Grab the``GodotNotification`` plugin binary (.aar) and config (.gdap) from the releases page and put both into ``res://android/plugins``. For easy start, you can also use my ``godot_android_notifications.gd`` script.

In order to use your game's icons and an icon color of your choice, see steps below.

### Adding flat notification icons

If you don't do this step, your notifications will feature just a coloured square or circle instead of your icons. If you want to add your icons, you need to make a square white color icon on a transparent background and get your mipmaps from it. [There's a handy tool for it.](https://romannurik.github.io/AndroidAssetStudio/icons-notification.html)

All the icon mipmaps must be named as "notification_icon.png" and placed into their respective folders:

```
android/build/res/mipmap/notification_icon.png            Size 192x192
android/build/res/mipmap-hdpi-v4/notification_icon.png    Size 72x72
android/build/res/mipmap-mdpi-v4/notification_icon.png    Size 48x48
android/build/res/mipmap-xhdpi-v4/notification_icon.png   Size 96x96
android/build/res/mipmap-xxhdpi-v4/notification_icon.png  Size 144x144
android/build/res/mipmap-xxxhdpi-v4/notification_icon.png Size 192x192
```
### Specifying the tint color for your icons

Create a file named "colors.xml" in your project at ``android/build/res/values``. Create a new color named "notification_color" and specify the color code like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
 <color name="notification_color">#e45d0a</color>
</resources>
```
### Methods

Plugin only has two methods to it:

#### scheduleNotification(message:String, header:String, intervalSeconds:int, tag:int)

Creates a notification to be shown after "intervalSeconds" with a given tag. If you want to create few parallel notifications, make sure that you use different tags. Tag can be any integer number.

#### cancelNotification(tag:int)

Cancels a notification with given tag. It's safe to cancel an unexistent notification (nothing will happen).
