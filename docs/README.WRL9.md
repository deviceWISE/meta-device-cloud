How to use it in WRLinux 9
==========================
The device cloud only supports python2 in WRLinux 9 because python3 has a bug:

```
ImportError: No module named 'pkg_resources'
```
More info: https://github.com/pypa/pip/issues/4464

Using "pip install"
-------------------
There is an issue with python on systems that use lib64.  Python by
default predefines its library path to look in.  Pip will ask python
where its site-packages are stored and install there.  The problem is
that when your python libs were installed into /usr/lib64/python2.7, and
you do a "pip install", pip will use /usr/local/lib/python2.7 not
/usr/local/lib64/python2.7.  The result is that when you try to load
your new modules, python won't find them.

Workaround:
pip install --target=/usr/lib64/python2.7/site-packages package_name

Build it
-----------
$ /path/to/setup.sh --machines=intel-x86-64 --dl-layers
    --templates=feature/python-device-cloud --layers=meta-device-cloud
$ source ./environment-setup-x86_64-wrlinuxsdk-linux
$ source ./oe-init-build-env build
$ bitbake wrlinux-image-glibc-std

Flashing Image To USB
---------------------
  * Images are stored in: tmp/deploy/images/intel-x86-64
  * Use the *.hddimg to flash
  * Insert USB stick into computer
  * lsblk to show the device list.  The drive letter is required.
  * This will take several minutes to complete
```sh
	IMG_NAME=wrlinux-image-glibc-std-intel-x86-64.hddimg
	PART=e
	sudo dd if=${IMG_NAME} of=/dev/sd${PART} status=progress bs=1M
	sudo sync
	sudo eject /dev/sd${PART}
```

Start it
---------------------
The following commands should be run on target:
    * Generate configuration:
      $ generate_config.py -f <config_file> -c <host> -p <port> -t <token>
        config_file: it is /etc/python-device-cloud/iot-connect.cfg by default
        host: login to https://portal.devicecloud.windriver.com, click
              "Developer" tab, see "Endpoints" section:
                HTTP  http://api.devicecloud.windriver.com/api
              it should be: api.devicecloud.windriver.com
              Note, *no* "http://" or "/api".
        port: 8883 by default
        token: Click "Applications" on side bar, see the
               "bm_hdc_device_manager_app" section, you need create it if there
               is no "bm_hdc_device_manager_app", then use the token list there.

        For example:
        $ generate_config.py -f /etc/python-device-cloud/iot-connect.cfg \
                             -c <host> \
                             -p 8883 \
                             -t <token of bm_hdc_device_manager_app>

        More info: $ generate_config.py --help

    * Start device manager
      $ systemctl start device-manager

More info: https://portal.devicecloud.windriver.com
