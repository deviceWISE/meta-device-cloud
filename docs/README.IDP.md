Description
===========
WRLinux IDP has an old version of python that is insecure and is not
suitable for device-cloud.  Python must be up versioned to 2.7.9.  The
configure script for WRL IDP checks out layers/oe-core from the IDP
release.  This must be changed and while making that change, add the
device cloud dependencies.

Note: at the time of this writing, the meta-device-cloud and the
oe-core patched repositories are not public.  The following procedure
will work only for those with WindRiver network access.

RCPL Supported
--------------
This document has been updated to support RCPL0027 + only.  Previous
RCPLs did not have the correct version of python.

Configure without device-cloud
------------------------------
This is an example configuration.  Add any specific layers required.

```sh
# e.g.:
/opt/WindRiver/IDP_3_xt_LB21_7.0_RCPL0027_Windshare/wrlinux-7/wrlinux/configure --enable-rootfs=idp --enable-addons=wr-idp --enable-kernel=idp  --without-layer=wr-srm,wr-mcafee --enable-board=intel-baytrail-64 --with-template=feature/python279 --with-rcpl-version=0027
```

Update the layers
-----------------
From your workspace:
```sh
cd layers

# clone
git clone https://github.com/Wind-River/meta-device-cloud.git

# clone the morty branch
git clone https://github.com/openembedded/meta-openembedded.git -b morty

```

Fix some problems in bitbake files
----------------------------------
  * the version of python-dbus in meta-openembedded/meta-python
  conflicts with the version in oe-core.  Remove the meta-openembedded
  one from the build, and use the oe-core version:

```sh
# Remove python-dbus_1.2.4.bb from the build
rm ./meta-openembedded/meta-python/recipes-devtools/python/python-dbus_1.2.4.bb
```

Update the bblayers.conf
------------------------
Add meta-device-cloud and meta-openembedded, e.g.

```sh
cd ..
vim bitbake_build/conf/bblayers.conf
```

add to the BBLAYERS list, recommend adding the following before 
*${WRL_TOP_BUILD_DIR}/layers/local* line:


```
	${WRL_TOP_BUILD_DIR}/layers/meta-openembedded/meta-python \
	${WRL_TOP_BUILD_DIR}/layers/meta-device-cloud \
```

Build the image
---------------
```sh
make python-device-cloud.addpkg; make python-pip.addpkg; make xinetd.addpkg; make inetutils.addpkg
make fs
```

After boot up
-------------
Once the image is flashed and booted, the credentials must be put in
place.  Systemd is used to manage the device-manager process by
default.  The device-manager is the only service that has a systemd
service file.  In order to connect, from the device console, run:
```sh
generate_config.py -f /etc/python-device-cloud/iot-connect.cfg -c <CLOUD URL> -p 8883 -t <APP TOKEN>
```
This command will create the connection credentials file.  Now, restat
the device manager:
```sh
systemctl restart device-manager
```

For debugging, tail the log:
```sh
journalctl -f -u device-manager
```
