#
# Copyright (C) 2017 Wind River Systems, Inc.
# License: MIT
#
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://0001-SWIG-_evp.i-remove-_des_-symbols-which-newer-python-.patch"
