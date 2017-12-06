#
# Copyright (C) 2017 Wind River Systems, Inc.
#
# Python 2
inherit setuptools systemd

# pull in the user id details
require device-cloud-user.inc

# all details are in device-cloud-common.inc
require device-cloud-common.inc

