Building for Yocto
==================

  * Tested on the "morty" branch

Requirements
------------
  * meta-device-cloud layer depends on
    * meta-python layer
    * meta-openembedded layer
    * openembedded layer
    * meta-intel (if an Intel BSP is required)

Build Setup Quick Start
-----------------------
```sh
mkdir repos

git clone git://git.yoctoproject.org/poky repos/poky -b morty
git clone https://github.com/openembedded/meta-openembedded.git repos/meta-openembedded -b morty
git clone git://git.yoctoproject.org/meta-intel repos/meta-intel -b morty
git clone git@github.com:Wind-River/meta-device-cloud.git repos/meta-device-cloud
git clone https://github.com/openembedded/openembedded-core.git repos/openembedded -b morty

mkdir poky-build
cd poky-build
source ../repos/poky/oe-init-build-env intel-corei7-64
export LANG=en_US.UTF-8
bitbake-layers add-layer ../../repos/meta-intel
bitbake-layers add-layer ../../repos/openembedded/meta
bitbake-layers add-layer ../../repos/meta-openembedded/meta-oe
bitbake-layers add-layer ../../repos/meta-openembedded/meta-python
bitbake-layers add-layer ../../repos/meta-openembedded/meta-networking
bitbake-layers add-layer ../../repos/meta-device-cloud

```

Note: fix a bug in openembedded where a txt file is not found:
touch ../../repos/openembedded/meta/files/deploydir_readme.txt

local.conf addtions
-------------------
The default MACHINE type for yocto is qemux86.  Change the MACHINE
type to the desired build architecture, e.g.  intel-corei7-64:

```
MACHINE ??= "intel-corei7-64"
```

Add the following to your local.conf.  Note: add whatever specific
packages required to the build with the CORE_IMAGE_EXTRA_INSTALL
variable.  Here the device-cloud packages are added:

```
CORE_IMAGE_EXTRA_INSTALL += " python-device-cloud python-pip "

# use systemd as the default init manager
# comment the following lines to use 'sysvinit' as the init manager
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
KERNEL_FEATURES_append = " cfg/systemd.scc"
```

Build Image
-----------
```sh
bitbake core-image-full-cmdline
```

Flashing Image To USB
---------------------
  * Images are stored in: tmp/deploy/images/intel-x86-64
  * Use the *.hddimg to flash
  * Insert USB stick into computer
  * lsblk to show the device list.  The drive letter is required, e.g.
  sdc, set PART=c.
  * This will take several minutes to complete
```sh
	IMG_NAME=core-image-full-cmdline-intel-corei7-64.hddimg
	PART=(your device letter)
	sudo dd if=${IMG_NAME} of=/dev/sd${PART} status=progress bs=1M
	sudo sync
	sudo eject /dev/sd${PART}
```

Notes
-----
The ethernet interface is not up by default.  The ethernet device is
not "eth0".
```sh
ifconfig -a
 (look for the ethernet device)
ifup (ethernet device name)
