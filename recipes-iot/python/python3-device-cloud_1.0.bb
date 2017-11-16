# Python 3
inherit setuptools3 systemd

# pull in the user id details
require device-cloud-user.inc

# all details are in device-cloud-common.inc
require device-cloud
