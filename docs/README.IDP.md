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


Configure without device-cloud
------------------------------
This is an example configuration.  Add any specific layers required.

```sh
/opt/WindRiver/IDP21_RCPL0022_XT3.1_Windshare/wrlinux-7/wrlinux/configure --enable-rootfs=idp --enable-addons=wr-idp --enable-kernel=idp  --without-layer=wr-srm,wr-mcafee --enable-board=intel-baytrail-64 --with-rcpl-version=0022
```

Update the layers
-----------------

```sh
cd layers
rm -fr oe-core
# clone
git clone http://stash.wrs.com/scm/hpr/oe-core-idp.git oe-core
git clone https://github.com/Wind-River/meta-device-cloud.git
git clone /opt/WindRiver/WRL9_Mirror/WRLinux-9-LTS-CVE/meta-openembedded

```

Fix some problems in bitbake files
----------------------------------
  * crda does not compile wh newer python due to sslv2 obsolete
  ciphers.  Remove it from the idp package group.
```sh
sed -i 's/crda//' wr-idp/wr-idp-base/recipes-base/packagegroups/packagegroup-idp-net.bb
```
  * the version of python-dbus in meta-openembedded/meta-python
  conflicts with the version in oe-core.  Remove the
  meta-openembedded one
```sh
mv ./meta-openembedded/meta-python/recipes-devtools/python/python-dbus_1.2.4.bb ./meta-openembedded/meta-python/recipes-devtools/python/python-dbus_1.2.4_bb
```

Update the bblayers.conf
------------------------
Add meta-device-cloud and meta-openembedded, e.g.
	$ vim bitbake_build/conf/bblayers.conf
add to the BBLAYERS list:
	${WRL_TOP_BUILD_DIR}/layers/meta-openembedded/meta-python \
	${WRL_TOP_BUILD_DIR}/layers/meta-device-cloud \

Build the image
---------------
```sh
make python-device-cloud.addpkg
make python-pip.addpkg
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
