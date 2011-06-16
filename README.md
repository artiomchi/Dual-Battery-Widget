Android Dual Battery Widget
---------------------------
It'll show two batteries, for the standard and the dock battery. If the device doesn't have the secondary battery (if it's not the Asus Eee Pad Transformer) it will hide the second battery 

The widget doesn't run a service or anything else.. It will only register a broadcast receiver for the battery status as long as you have a widget on your screen. If you don't have one, it won't be doing anything at all (hence very battery friendly)

You can install it from the android market here: https://market.android.com/details?id=org.flexlabs.widgets.dualbattery

**Note:** On the current release of Honeycomb, it is possible that the widget will not immediately appear in the widget list after installation. To refresh the widget list, you should either restart your device, or restart the launcher app (Settings -> Applications -> Manage applications -> All -> Launcher -> Force stop)

**Update 2011-06-15: v0.5**
New features added:  

* Widget options dialog with text position and text size options
* Ability to hide secondary battery when it's not connected
* Battery charging indicator
* Still working on new proper icons, so for now just made a basic charging "bolt"... Working on some proper, unified design 
