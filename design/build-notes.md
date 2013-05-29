# How to build a corobot

This document should include all the step-by-step instructions
on how to take the components and construct, install software
and do any initial testing and calibration for a corobot.  It
will probably get really long.  It might need to be broken up
into several documents.

## Hardware assembly

This may well need its own document.

## Install Ubuntu

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

   