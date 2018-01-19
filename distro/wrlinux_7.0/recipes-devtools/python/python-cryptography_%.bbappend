#
# Copyright (C) 2017 Wind River Systems, Inc.
# License: MIT
#
# this fixes a QA error due to an incorrect naming of the module.  SRCNAME !=
# cryptography.
FILES_${PN}-dbg += " \
    ${libdir}/${PYTHON_PN}2.7/site-packages/cryptography/hazmat/bindings/.debug \
"

