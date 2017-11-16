DESCRIPTION = "Python library for Helix Device Cloud"
HOMEPAGE = "https://github.com/Wind-River/device-cloud-python.git"
LICENSE = "Apache-2.0"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "${AUTOREV}"

# for now, prepopulate this in the downloads directory
SRC_URI = "git://github.com/Wind-River/device-cloud-python.git"

S = "${WORKDIR}/git"
RDEPENDS_${PN} += "${PN}-systemd bash sudo inetutils-telnetd"
RDEPENDS_${PN}-systemd += "bash"

ETC_DIR = "/etc/${PN}"
VAR_DIR = "${localstatedir}/lib/${PN}"
BIN_DIR = "${bindir}"
SHARE_DIR = "/usr/share/${PN}"

RDEPENDS_${PN} += "\
    bash \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-paho-mqtt \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-websocket-client \
    ${PYTHON_PN}-requests \
"
# requests has a problem 
# ERROR: Multiple .bb files are due to be built which each provide python-distribute:
#   layers/oe-core/meta/recipes-devtools/python/python-setuptools_18.2.bb
#   layers/oe-core/meta/recipes-devtools/python/python-distribute_0.6.32.bb


PACKAGES =+ "${PN}-systemd"
SYSTEMD_SERVICE_${PN}-systemd += "device-manager.service"
SYSTEMD_PACKAGES = "${PN}-systemd"

# device-cloud-python must be able to coexsit with previous HDC versions.  So, install
# into its own namespace.
do_install_append() {
	DETECT_IDP="${@bb.utils.contains('LINUX_KERNEL_TYPE', 'idp', 'idp', '', d)}"

	install -d ${D}/${SHARE_DIR}
	install -d ${D}/${ETC_DIR}
	install -d ${D}/${VAR_DIR}
	install -d ${D}/${BIN_DIR}
	install -d ${D}${systemd_unitdir}/system/
	install -d ${D}/${sysconfdir}/sudoers.d

	install -m 0400 "${B}/share/sudoers.d/device-cloud" "${D}/${sysconfdir}/sudoers.d/device-cloud"
	cat ${B}/share/device-manager.service.in | sed -e "s|%user%|${DC_USER}|" > "${B}/share/device-manager.service"
	install -m 0644 "${B}/share/device-manager.service" ${D}${systemd_unitdir}/system/

	# update the working directory
	sed -i "s|^#WorkingDirectory.*|WorkingDirectory=${VAR_DIR}|"  ${D}${systemd_unitdir}/system/device-manager.service

	# post process the vars for paths
	cat ${B}/share/cloud-test.py.in | sed -e "s|%bindir%|${BIN_DIR}|" \
		-e "s|%vardir%|${VAR_DIR}|" -e "s|%etcdir%|${ETC_DIR}|" > "${B}/share/cloud-test.py"
	install -m 0755 "${B}/share/cloud-test.py" ${D}${BIN_DIR}

	# iot service watchdog for OTA and rollback on IDP only
	if [ "$DETECT_IDP" = "idp" ]; then
		echo "IDP detected, installing watchdog files"

		# post process the vars for paths
		cat ${B}/share/iot-watchdog-test.sh.in | sed -e "s|%bindir%|${BIN_DIR}|" \
			-e "s|%vardir%|${VAR_DIR}|" -e "s|%etcdir%|${ETC_DIR}|" > "${B}/share/iot-watchdog-test.sh"
		cat ${B}/share/iot-watchdog.conf.in | sed -e "s|%bindir%|${BIN_DIR}|" \
			-e "s|%vardir%|${VAR_DIR}|" -e "s|%etcdir%|${ETC_DIR}|" > "${B}/share/iot-watchdog.conf"

		install -m 0755 "${B}/share/iot-watchdog-test.sh" ${D}${BIN_DIR}
		install -m 0755 "${B}/share/iot-watchdog.conf" ${D}${ETC_DIR}
		install -m 0755 "${B}/share/snapshot_util.py" ${D}${BIN_DIR}
	fi


	install -m 644 "${B}/COPYING.txt" ${D}/${SHARE_DIR}
    	cp -r ${B}/demo  ${D}/${SHARE_DIR}
    	cp ${B}/README* ${D}/${SHARE_DIR}
    	cp -r ${B}/share/example-ota-packages ${D}/${SHARE_DIR}

	# change the #! line to use python not python3 which is the default
	sed -i "s/env python\?/env ${PYTHON_PN}/" ${D}/${SHARE_DIR}/demo/*.py
   
	# fix python version, this is python 2.x, so check for python3
	for i in device_manager.py generate_config.py validate_script.py validate_app.py; do
	{
		echo "converting ${S}/${i} to ${B}/${i}"
		sed -i "s/env python\?/env ${PYTHON_PN}/" ${B}/${i}
	} done

	# TODO: update the demo app config/runtime dirs
	# update the runtime dir in iot.cfg
	# update the config dir
	sed -i "s|:\"runtime\"|:\"${VAR_DIR}\"|" ${B}/iot.cfg
	sed -i "s|default_cfg_dir = \".\"|default_cfg_dir = \"${ETC_DIR}\"|" ${B}/device_manager.py
	sed -i "s|systemd_controlled = False|systemd_controlled = True|" ${B}/device_manager.py

	# install scripts
	install -m 755 ${B}/device_manager.py ${D}/${BIN_DIR}
	install -m 755 ${B}/generate_config.py ${D}/${BIN_DIR}
	install -m 755 ${B}/validate_app.py ${D}/${SHARE_DIR}
	install -m 755 ${B}/validate_script.py ${D}/${SHARE_DIR}

	# install the /etc/ files
	install -m 755 ${B}/iot.cfg ${D}/${ETC_DIR}/iot.cfg

}
FILES_${PN} += "${SHARE_DIR} ${VAR_DIR} ${ETC_DIR} ${BIN_DIR}"
FILES_${PN} += "/etc/sudoers.d/device-cloud"
FILES_${PN}-systemd += "${systemd_unitdir}"


pkg_postinst_${PN}() {
#!/bin/sh -e
if [ x"$D" = "x" ]; then
    chown -R ${DC_USER}:${DC_GROUP} ${VAR_DIR}
    chown -R ${DC_USER}:${DC_GROUP} ${ETC_DIR}
else
    exit 1
fi
}
