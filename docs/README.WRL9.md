How To Build WRLINUX 9
======================
This document is relevant to those with WindRiver network access.

Issues:
  * python3 has a problem 
```
ImportError: No module named 'pkg_resources'
```
  * use python 2.7.x (where x > 9)
  * if you use pip, keep in mind that pip doesn't respect the libdir
  if it is lib64.  It will use lib instead.  A patch is required for
  python for this.  https://github.com/pypa/pip/issues/4464

Recommend using python 2 instead.


Using "pip install"
-------------------
There is an issue with python on systems that use lib64.  Python by
default predefines its library path to look in.  Pip will ask python
where its site-packages are stored and install there.  The problem is
that when your python libs were installed into /usr/lib64/python2.7, and
you do a "pip install", pip will use /usr/local/lib/python2.7 not
/usr/local/lib64/python2.7.  The result is that when you try to load
your new modules, python won't find them.

Workaround:
pip install --target=/usr/lib64/python2.7/site-packages package_name

Quick Start
-----------
Tested on build machine iotbuild4
```sh
	git clone -b WRLINUX_9_LTS_CVE /opt/WindRiver/WRL9_Mirror/wrlinux-9
	./wrlinux-9/setup.sh --dl-layers --machines intel-x86-64
	cd layers
	git clone https://github.com/Wind-River/meta-device-cloud.git
	cd ..
	source ./environment-setup-x86_64-wrlinuxsdk-linux
	source ./oe-init-build-env build
	export LANG=en_US.UTF-8
	sed -i "s/\(BB_NO_NETWORK ?= \).*/\1'0'/" conf/local.conf
	bitbake-layers add-layer ../layers/meta-openembedded/meta-python
	bitbake-layers add-layer ../layers/meta-openembedded/meta-networking
	bitbake-layers add-layer ../../repos/meta-openembedded/meta-oe
	bitbake-layers add-layer ../layers/meta-device-cloud
# update the white list (see below)
# update local.conf (see below)
	bitbake wrlinux-image-glibc-std
```

Whitelist
---------
Python3 only:
Add the following to the white list.  Open
layers/wr-base/conf/wrlinux-whitelist.conf and add the following to the
PNWHITELIST_meta-python section:
```
    python-paho-mqtt \
    python-pysocks \
    python-certifi \
    python-requests \
    python-urllib3 \
    python-ndg-httpsclient \
    python-pyopenssl \
    python-pyasn1 \
    python-cryptography \
    python-cffi \
    python-pycparser \
    python-idna \
    python-websocket-client \
    python-ipaddress \
    python3-pysocks \
    python3-certifi \
    python3-requests \
    python3-urllib3 \
    python3-ndg-httpsclient \
    python3-pyopenssl \
    python3-pyasn1 \
    python3-cryptography \
    python3-cffi \
    python3-pycparser \
    python3-idna \
    python-backports-ssl \
```

Note: if you see an error saying 

	ERROR: <pkgname> was skipped: Not supported by Wind River. See documentation on enabling.

that means it needs to be added to the whitelist.

local.conf specific changes
---------------------------
Make sure systemd is set as the init_manager.  The section should look
like:
```
# use systemd as the default init manager
# comment the following lines to use 'sysvinit' as the init manager
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
KERNEL_FEATURES_append = " cfg/systemd.scc"
```

Add Packages To Image
---------------------
Append the following to conf/wrimage_intel-x86-64.inc:
```
IMAGE_INSTALL += "python-device-cloud python-pip"
```

Flashing Image To USB
---------------------
  * Images are stored in: tmp/deploy/images/intel-x86-64
  * Use the *.hddimg to flash
  * Insert USB stick into computer
  * lsblk to show the device list.  The drive letter is required.
  * This will take several minutes to complete
```sh
	IMG_NAME=wrlinux-image-glibc-std-intel-x86-64.hddimg
	PART=e
	sudo dd if=${IMG_NAME} of=/dev/sd${PART} status=progress bs=1M
	sudo sync
	sudo eject /dev/sd${PART}
```
