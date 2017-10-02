Building for Yocto
==================

  * Tested on "rocko" and "morty"

Requirements
------------
  * meta-hdc layer depends on
    * meta-python layer
    * meta-openembedded layer

local.conf addtions
-------------------
Add the following to your local.conf:

```
CORE_IMAGE_EXTRA_INSTALL += "python-modules hdc-python"

# use systemd as the default init manager
# comment the following lines to use 'sysvinit' as the init manager
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
KERNEL_FEATURES_append = " cfg/systemd.scc"
```
