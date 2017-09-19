DESCRIPTION = "Python library for Helix Device Cloud"
HOMEPAGE = "https://github.com/Wind-River/hdc-python.git"
LICENSE = "Apache-2.0"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "a06b99f1b64a0e510600405657ccd55cbfeada84"

# for now, prepopulate this in the downloads directory
SRC_URI = "git://github.com/Wind-River/hdc-python.git"

S = "${WORKDIR}/git"

RDEPENDS_${PN}_class-native = ""
DEPENDS_append_class-native = " python-native "

RDEPENDS_${PN} += "${PN}-systemd bash sudo"
RDEPENDS_${PN}-systemd += "bash"

ETC_DIR = "/etc/${PN}"
VAR_DIR = "${localstatedir}/lib/${PN}"
BIN_DIR = "${bindir}"
SHARE_DIR = "/usr/share"

# Note: support python3 by default
inherit setuptools3 systemd

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "\
    bash \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-paho-mqtt \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-paho-mqtt \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-websockets \
"
# TODO: fix the websockets vs websocket module name issue!

PACKAGES =+ "${PN}-systemd"
SYSTEMD_SERVICE_${PN}-systemd += "device-manager.service"
SYSTEMD_PACKAGES = "${PN}-systemd"

# hdc-python must be able to coexsit with previous HDC versions.  So, install
# into its own namespace.
do_install_append() {

	install -d ${D}/${SHARE_DIR}
	install -d ${D}/${ETC_DIR}
	install -d ${D}/${VAR_DIR}
	install -d ${D}/${BIN_DIR}
	install -d ${D}${systemd_unitdir}/system/
	#install -d ${D}/${sysconfdir}/sudoers.d

	echo "Installing: ${B}/share/device-manager.service into ${D}${systemd_unitdir}/system "
	install -m 0644 "${B}/share/device-manager.service" ${D}${systemd_unitdir}/system/

	#install -m 0400 "${WORKDIR}/hdc.sudoers" "${D}/${sysconfdir}/sudoers.d/hdc"

	install -m 644 "${B}/COPYING.txt" ${D}/${SHARE_DIR}
    	cp -r ${B}/demo  ${D}/${SHARE_DIR}
    	cp -r ${B}/docker  ${D}/${SHARE_DIR}
    	cp ${B}/README* ${D}/${SHARE_DIR}
    	cp -r ${B}/share/example-ota-package ${D}/${SHARE_DIR}
    
	#TODO: update the paths in the device manager for runtime and etc

	# fix python version
	for i in device_manager.py generate_config.py validate_script.py validate_app.py; do
	{
		echo "converting ${S}/${i} to ${B}/${i}"
		echo sed -i "s/env python/env python3/" ${B}/${i}
		sed -i "s/env python/env python3/" ${B}/${i}
	} done

	# TODO: update the demo app config/runtime dirs
	# update the runtime dir in iot.cfg
	# update the config dir
	sed -i "s|:\"runtime\"|:\"${VAR_DIR}\"|" ${B}/iot.cfg
	sed -i "s|default_cfg_dir = \".\"|default_cfg_dir = \"${ETC_DIR}\"|" ${B}/device_manager.py

	# install scripts
	install -m 755 ${B}/device_manager.py ${D}/${BIN_DIR}
	install -m 755 ${B}/generate_config.py ${D}/${BIN_DIR}
	install -m 755 ${B}/validate_app.py ${D}/${SHARE_DIR}
	install -m 755 ${B}/validate_script.py ${D}/${SHARE_DIR}

	# install the /etc/ files
	install -m 755 ${B}/iot.cfg ${D}/${ETC_DIR}/iot.cfg

}
FILES_${PN} += "${SHARE_DIR} ${VAR_DIR} ${ETC_DIR} ${BIN_DIR}"
FILES_${PN}-systemd += "${systemd_unitdir}"

