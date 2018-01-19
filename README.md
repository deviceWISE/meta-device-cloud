Description
===========
Device Cloud (DC) is a cloud/device platform that accelerates
device to cloud and cloud to cloud interaction.  This meta layer integrates
a Python implementation that is designed to be run on the device side and can be
used for device actuation, management, sending telemetry, remote
console etc.

The Python agent for DC is designed for quick deployment on any
platform that supports Python.  The continuous deployment model uses
"pip" to install and update the latest modules.  Any application that
wants to use DC cloud services can import the "device_cloud" module and
begin using the DC APIs.

Requirements:
-------------
meta-device-cloud layer depends on:
    * meta-python layer
    * meta-openembedded layer
    * openembedded layer
    * meta-intel (if an Intel BSP is required)

For Yocto details see README.yocto.md
