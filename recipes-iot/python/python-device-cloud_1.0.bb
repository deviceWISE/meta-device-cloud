DESCRIPTION = "Python library for Helix Device Cloud"
HOMEPAGE = "https://github.com/Wind-River/device-cloud-python.git"
LICENSE = "Apache-2.0"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "d9b3818ccec5a7571e13dc8d7c555d69cb927cb1"

# for now, prepopulate this in the downloads directory
SRC_URI = "git://github.com/Wind-River/device-cloud-python.git"

S = "${WORKDIR}/git"
RDEPENDS_${PN} += "${PN}-systemd bash sudo"
RDEPENDS_${PN}-systemd += "bash"

ETC_DIR = "/etc/${PN}"
VAR_DIR = "${localstatedir}/lib/${PN}"
BIN_DIR = "${bindir}"
SHARE_DIR = "/usr/share"

# Note: support python2 by default

inherit setuptools systemd

DEPENDS += "${PYTHON_PN}-pytest-runner"

RDEPENDS_${PN} += "\
    bash \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-pytest-runner \
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

#${PYTHON_PN}-argparse
# TODO: fix the websockets vs websocket module name issue!

PACKAGES =+ "${PN}-systemd"
SYSTEMD_SERVICE_${PN}-systemd += "device-manager.service"
SYSTEMD_PACKAGES = "${PN}-systemd"

# device-cloud-python must be able to coexsit with previous HDC versions.  So, install
# into its own namespace.
do_install_append() {

	install -d ${D}/${SHARE_DIR}
	install -d ${D}/${ETC_DIR}
	install -d ${D}/${VAR_DIR}
	install -d ${D}/${BIN_DIR}
	install -d ${D}${systemd_unitdir}/system/
	install -d ${D}/${sysconfdir}/sudoers.d

	echo "Installing: ${B}/share/device-manager.service into ${D}${systemd_unitdir}/system "
	install -m 0644 "${B}/share/device-manager.service" ${D}${systemd_unitdir}/system/

	# uncomment when running as non root user
	#install -m 0400 "${WORKDIR}/device-cloud.sudoers" "${D}/${sysconfdir}/sudoers.d/hdc"

	install -m 644 "${B}/COPYING.txt" ${D}/${SHARE_DIR}
    	cp -r ${B}/demo  ${D}/${SHARE_DIR}
    	cp ${B}/README* ${D}/${SHARE_DIR}
    	cp -r ${B}/share/example-ota-package ${D}/${SHARE_DIR}

	# change the #! line to use python not python3 which is the default
	sed -i 's/env python3/env python/' ${D}/${SHARE_DIR}/demo/*.py
   
	#TODO: update the paths in the device manager for runtime and etc

	# fix python version, this is python 2.x, so check for python3
	for i in device_manager.py generate_config.py validate_script.py validate_app.py; do
	{
		echo "converting ${S}/${i} to ${B}/${i}"
		sed -i "s/env python3/env python/" ${B}/${i}
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

