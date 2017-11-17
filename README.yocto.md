Building for Yocto
==================

  * Tested on "rocko" and "morty"

Requirements
------------
  * meta-device-cloud layer depends on
    * meta-python layer
    * meta-openembedded layer
    * openembedded layer
    * meta-intel (if an Intel BSP is required)

Building Quick Start
--------------------
```sh
mkdir repos
#clone the repos
git clone git://git.yoctoproject.org/poky repos/poky
git clone https://github.com/openembedded/meta-openembedded.git repos/meta-openembedded
git clone git://git.yoctoproject.org/meta-intel repos/meta-intel
git clone git@github.com:Wind-River/meta-device-cloud.git repos/meta-device-cloud
git clone https://github.com/openembedded/openembedded-core.git repos/openembedded

# checkout the correct branches
cd poky && git checkout morty
cd ../repos/meta-intel && git checkout morty && cd ../..
cd ../repos/openembedded && git checkout morty && cd ../..
cd ../repos/meta-openembedded && git checkout morty && cd ../..

# setup the build config
mkdir poky-build
cd poky-build
source ../poky/oe-init-build-env intel-corei7-64
bitbake-layers add-layer ../../repos/meta-intel
bitbake-layers add-layer ../../repos/meta-device-cloud
bitbake-layers add-layer ../../repos/openembedded/meta
bitbake-layers add-layer ../../repos/meta-openembedded/meta-oe
bitbake-layers add-layer ../../repos/meta-openembedded/meta-python
bitbake-layers add-layer ../../repos/meta-openembedded/meta-networking

```

Note: fix a bug in openembedded where a txt file is not found:
touch ../../repos/openembedded/meta/files/deploydir_readme.txt

local.conf addtions
-------------------
Add the following to your local.conf:

```
CORE_IMAGE_EXTRA_INSTALL += "python3-modules python3-device-cloud python3-pytest-native"

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
