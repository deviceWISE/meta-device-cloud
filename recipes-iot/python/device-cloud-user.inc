inherit useradd

# add user and group for DC
USERADD_PACKAGES = "${PN}"
DC_USER = "dc"
DC_GROUP = "dc"


# On IDP with TPE support, only dirs owned by root can execute binaries.  Some
# binaries generate files in their bin dir, e.g. ccg.  So, use the TPE
# trusted GID as the GID for wra.  GID 1005 doesn't exist on the system anyway,
# but it is the default trusted GID.
TRUSTED_GID          = "1005"
DC_USER_PARAM        = "-r -s ${sbindir}/nologin -g ${TRUSTED_GID} ${DC_USER}"
DC_GROUP_PARAM       = "-f -r -o -g ${TRUSTED_GID} ${DC_GROUP}"
USERADD_PARAM_${PN}  = "${DC_USER_PARAM}"
GROUPADD_PARAM_${PN} = "${DC_GROUP_PARAM}"
DC_OWNER_AND_GROUP   = "-o ${DC_USER} -g ${DC_GROUP}"
