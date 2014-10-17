# How to build a corobot

This document should include all the step-by-step instructions
on how to take the components and construct, install software
and do any initial testing and calibration for a corobot.  It
will probably get really long.  It might need to be broken up
into several documents.

## Hardware assembly

This may well need its own document.

## Install Ubuntu

###Acer C720

Begin by booting the laptop into developer mode by starting the laptop up until you reach the login screen.
At this screen press esc+refresh+power. An error message will appear saying that the 
OS contains an error. Press ctrl-d to move past it. Turn off os verification.

Turn off the laptop and remove the back (THIS WILL VOID THE WARRENTY).
Unscrew the write-protect screw as seen [here](http://www.chromium.org/_/rsrc/1381990807648/chromium-os/developer-information-for-chrome-os-devices/acer-c720-chromebook/c720-chromebook-annotated-innards.png). The write-protect screw is screw number 7.

Deactivate chromeos by using [this script](https://johnlewis.ie/custom-chromebook-firmware/rom-download/).

Put the write-protect screw back into the laptop.

Using the Ubuntu 12.04 install on the usb stick, install ubuntu as normal.

Get a usb mouse and plug it in, the touch pad shouldn't be working. In order to fix this use these commands
`cd /tmp`

`sudo mkdir kernel`

`cd kernel`

`sudo wget http://kernel.ubuntu.com/~kernel-ppa/mainline/v3.17-rc1-utopic/linux-headers-3.17.0-031700rc1-generic_3.17.0-031700rc1.201409021903_amd64.deb`

`sudo wget http://kernel.ubuntu.com/~kernel-ppa/mainline/v3.17-rc1-utopic/linux-headers-3.17.0-031700rc1_3.17.0-031700rc1.201409021903_all.deb`

`sudo wget http://kernel.ubuntu.com/~kernel-ppa/mainline/v3.17-rc1-utopic/linux-image-3.17.0-031700rc1-generic_3.17.0-031700rc1.201409021903_i386.deb`

`sudo dpkg -i *.deb`

`sudo reboot `

###Acer Aspire V5

On most recent hardware (Acer Aspire V5): F2 to get into BIOS.
Under boot menu, change from UEFI to Legacy BIOS to bypass Win8
Safe Boot.  Also move the USB CD (or whatever Ubuntu is on) up
in the list of boot devices.

We are now using Ubuntu 12.10 since that seems to support the Kinect better than 12.04 with our laptop hardware.

Probably need only one admin account, no user accounts.

## Install the Corobot software

See [INSTALL.md](https://github.com/corobotics/corobots/blob/master/INSTALL.md) in the main repo.

## Set up the cameras

1. Plug in the cameras, find their serial numbers by plugging in
   each camera separately and running:

    `udevadm info -a -p $(udevadm info -q path -n /dev/video1)`

   (Make sure you are not looking at the built-in webcam!  You may need /dev/video0 or /dev/video2 to get our cameras.)  This will give you data about the whole USB chain, make sure you find the serial numbers for the cameras themselves.
      
2. Use the serial numbers to write device rules
   (more details at <http://www.reactivated.net/writing_udev_rules.html>).
   Create the file `/etc/udev/rules.d/10-video.rules` with two lines:

    `SUBSYSTEM=="usb", ATTRS{serial}=="left-camera-serial-number", SYMLINK+="videoleft"`  
    `SUBSYSTEM=="usb", ATTRS{serial}=="right-camera-serial-number", SYMLINK+="videoright"`  

## set up the robot connection

1. Plug in the robot and find the idProduct and idVendor by running:
    `udevadm info -a -p $(udevadm info -q path -n /dev/ttyUSB0)`
   Make sure the information is for the serial to usb converter

2. Create or modify the file `/etc/udev/rules.d/52-corobot.rules` with
    `SUBSYSTEMS=="usb", ATTRS{idProduct}=="idProduct", ATTRS{idVendor}=="idVendor", MODE="666", GROUP="corobot"`
   
