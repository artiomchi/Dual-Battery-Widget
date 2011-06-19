Android Dual Battery Widget
---------------------------
It'll show two batteries, for the standard and the dock battery. If the device doesn't have the secondary battery 
(if it's not the Asus Eee Pad Transformer) it will hide the second battery 

The widget doesn't run a service or anything else.. It will only register a broadcast receiver for the battery status 
as long as you have a widget on your screen. If you don't have one, it won't be doing anything at all (hence very battery friendly)

You can install it from the android market here: https://market.android.com/details?id=org.flexlabs.widgets.dualbattery

**Note:** On the current release of Honeycomb, it is possible that the widget will not immediately 
appear in the widget list after installation. To refresh the widget list, you should either restart your device, 
or restart the launcher app (Settings -> Applications -> Manage applications -> All -> Launcher -> Force stop)

**Note:** *If you use a custom rom*, there is a high chance that this widget will not be able to identify
the dock status, or even register that your device supports it at all.. This is a restriction of most custom
kernels at this moment, not of this application.  
This is because Asus extended their Android kernel build to send extra information about the dock and the
dock battery along with battery status notifications. Unless this change has been merged in the custom kernel,
it will not be sending these notifications

**Update 2011-06-19: v0.6.5**  
[+] New 10-step icons for the batteries  

**Update 2011-06-19: v0.6**  
[+] Showing battery info / widget properties / other info on widget click  
[+] Option to select which battery to display  
[-] Removing the battery dock options on devices without the dock  
[+] Stack trace catcher (for the error reports)  
[!] not recognizing dock on first boot  
[+] Feedback form  
[+] About dialog  

**Update 2011-06-06: v0.5.2**  
[!] Fixed the bug when the widget wasn't added to the user's homescreen  
[+] Showing the battery info when the widget is tapped  

**Update 2011-06-19: v0.6**
[+] Showing battery info / widget properties / other info on widget click
[+] Option to select which battery to display
[-] Removing the battery dock options on devices without the dock
[+] Stack trace catcher (for the error reports)
[!] not recognizing dock on first boot
[+] Feedback form
[+] About dialog

**Update 2011-06-06: v0.5.2**
[!] Fixed the bug when the widget wasn't added to the user's homescreen
[+] Showing the battery info when the widget is tapped

**Update 2011-06-15: v0.5**
New features added:  

* Widget options dialog with text position and text size options  
* Ability to hide secondary battery when it's not connected  
* Battery charging indicator  
* Still working on new proper icons, so for now just made a basic charging "bolt"... Working on some proper, unified design 
