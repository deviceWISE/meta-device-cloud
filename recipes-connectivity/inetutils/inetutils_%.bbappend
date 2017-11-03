FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# update the telnet config to bind to localhost only
SRC_URI += "file://telnet-localhost-only.xinetd.inetutils"

FILES_${PN}-telnetd += "${sysconfdir}/xinetd.d/telnet-localhost-only"

do_install_append(){

	install -m 0755 -d ${D}${sysconfdir}/xinetd.d
	cp ${WORKDIR}/telnet-localhost-only.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/telnet
}
