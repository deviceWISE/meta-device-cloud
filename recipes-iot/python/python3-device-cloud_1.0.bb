#
# Copyright (C) 2017 Wind River Systems, Inc.
# License: MIT
#
# Python 3
inherit setuptools3 update-rd.d systemd

# pull in the user id details
require device-cloud-user.inc

# all details are in device-cloud-common.inc
require device-cloud-common.inc
