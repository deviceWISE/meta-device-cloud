#
# Copyright(c) 2013 Wind River Systems, Inc.
#
PR =  "r1"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://lvmcheck \
	file://init \
"


do_install_append () {
	DETECT_IDP="${@bb.utils.contains('LINUX_KERNEL_TYPE', 'idp', 'idp', '', d)}"
	if [ "$DETECT_IDP" = "idp" ]; then
		install -d -m 0755 ${D}/etc/init
		install -m 0755 lvmcheck ${D}/etc/init
	else
		echo "Non IDP platforms not supported"
	fi
}

RDEPENDS_${PN} += "tar bzip2 dosfstools e2fsprogs "
