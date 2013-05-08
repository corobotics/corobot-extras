# How to build a corobot

This document should include all the step-by-step instructions
on how to take the components and construct, install software
and do any initial testing and calibration for a corobot.  It
will probably get really long.  It might need to be broken up
into several documents.

## Hardware assembly

This may well need its own document.

## Install Ubuntu

At present, ROS is not supported under Ubuntu 13, so we will stick with 12.04 LTS.

Probably need only one admin account, no user accounts.

## Install needed packages

This would include ROS and all its dependencies.
We're sticking with ROS Fuerte, be sure to install the correct version.

Perform the Desktop-Full-Install
http://www.ros.org/wiki/fuerte/Installation/Ubuntu

(Might be able to get away with Desktop-Install, untested though.)

Probably also ZBar (our version of it, <https://github.com/corobotics/ZBar> )

## Install the corobot software

`git clone git://github.com/corobotics/corobots.git`

Then run all the compilation, then installation (???)

## Set up the cameras - not tested yet

1. Plug in the cameras, find their serial numbers
  (plug in each camera separately and run the following)

    `udevadm info -q all -n /dev/video0`

2. Use the serial numbers to write device rules
   (more details at <http://www.reactivated.net/writing_udev_rules.html>).
   Create the file `/etc/udev/rules.d/10-video.rules` with two lines:

    `SUBSYSTEM=="usb", ATTRS{serial}=="left-camera-serial-number", SYMLINK+="video0"`  
    `SUBSYSTEM=="usb", ATTRS{serial}=="right-camera-serial-number", SYMLINK+="video1"`

