Description
===========
Wind River Linux IDP must be configured to use Python 2.7.9 or later.

RCPL Supported
--------------
This document supports RCPL0027 or later only. Earlier
RCPLs do not have the correct version of Python.

Configure Without Device Cloud
------------------------------
This is an example configuration.  You must add any layers specific to your requirements.

```sh
# e.g.:
/opt/WindRiver/IDP_3_xt_LB21_7.0_RCPL0027_Windshare/wrlinux-7/wrlinux/configure --enable-rootfs=idp --enable-addons=wr-idp --enable-kernel=idp  --without-layer=wr-srm,wr-mcafee --enable-board=intel-baytrail-64 --with-template=feature/python279 --with-rcpl-version=0027
```

Update the Layers
-----------------
From your workspace:
```sh
cd layers

# clone
git clone https://github.com/Wind-River/meta-device-cloud.git

# clone the morty branch
git clone https://github.com/openembedded/meta-openembedded.git -b morty

```

Fix Some Problems in the BitBake Files
----------------------------------
  * The version of python-dbus in meta-openembedded/meta-python
  conflicts with the version in oe-core. Remove the meta-openembedded
  version from the build and use the oe-core version. For example:

```sh
# Remove python-dbus_1.2.4.bb from the build
rm ./meta-openembedded/meta-python/recipes-devtools/python/python-dbus_1.2.4.bb
```

Update bblayers.conf
------------------------
Add meta-device-cloud and meta-openembedded, for example:

```sh
cd ..
vim bitbake_build/conf/bblayers.conf
```

Add to the BBLAYERS list, adding the following before the  
*${WRL_TOP_BUILD_DIR}/layers/local* line is recommended:


```
	${WRL_TOP_BUILD_DIR}/layers/meta-openembedded/meta-python \
	${WRL_TOP_BUILD_DIR}/layers/meta-device-cloud \
```

Build the Image
---------------
```sh
make python-device-cloud.addpkg; make python-pip.addpkg; make xinetd.addpkg; make inetutils.addpkg
make fs
```

After Boot Up
-------------
Once the image is flashed and booted, the credentials must be put in
place. systemd is used to manage the device-manager process by
default. The device-manager is the only service that has a systemd
service file. To connect, from the device console, run:
```sh
generate_config.py -f /etc/python-device-cloud/iot-connect.cfg -c <CLOUD URL> -p 8883 -t <APP TOKEN>
```
This command creates the connection credentials file. Restart
the device manager:
```sh
systemctl restart device-manager
```

For debugging, tail the log:
```sh
journalctl -f -u device-manager
```
