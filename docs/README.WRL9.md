How to use it in WRLinux 9
======================
The device cloud only supports python2 in WRLinux 9 because python3 has a bug:

```
ImportError: No module named 'pkg_resources'
```
More info: https://github.com/pypa/pip/issues/4464

Build it
-----------
$ /path/to/setup.sh --machines=intel-x86-64 --dl-layers
    --templates=feature/python-device-cloud --layers=meta-device-cloud
$ source ./environment-setup-x86_64-wrlinuxsdk-linux
$ source ./oe-init-build-env build
$ bitbake wrlinux-image-glibc-std

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
                             -c api.devicecloud.windriver.com \
                             -p 8883 \
                             -t <token of bm_hdc_device_manager_app>

        More info: $ generate_config.py --help

    * Start device manager
      $ device_manager.py

More info: https://portal.devicecloud.windriver.com
