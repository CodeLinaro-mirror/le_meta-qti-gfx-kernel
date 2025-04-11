DESCRIPTION = "QTI Graphics drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"

inherit deploy module
CLEANBROKEN = "1"
PR = "r0"


RPROVIDES:${PN} += "kernel-module-msm-kgsl-${KERNEL_VERSION}"
FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://vendor/qcom/opensource/graphics-kernel"
SRC_URI += "file://${THISDIR}/kgsl.rules"


S = "${WORKDIR}/vendor/qcom/opensource/graphics-kernel"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"
EXTRA_OEMAKE += "M=${S}"
EXTRA_OEMAKE += "USE_DEDICATED_KERNEL_LE_TARGET=1"
DEFAULT_PREFERENCE = "-1"

MAKE_TARGETS = "modules"
KERNEL_MODULES = "msm_kgsl"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

KERNEL_CC = "${STAGING_BINDIR_NATIVE}/clang/bin/clang -target ${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}"

do_install() {
  install -d ${D}${includedir}/linux
  install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/
  install -d ${D}${sysconfdir}/udev/rules.d
  install -m 0755 ${S}/msm_kgsl.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/msm_kgsl.ko
  install -m 0755 ${S}/Module.symvers -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/Module.symvers
  install -m 0644 ${THISDIR}/msm_kgsl.conf -D ${D}${sysconfdir}/modules-load.d/msm_kgsl.conf
  install -m 0644 ${THISDIR}/kgsl.rules -D ${D}${sysconfdir}/udev/rules.d/kgsl.rules
}

do_deploy() {
    install -d ${DEPLOYDIR}/kernel_modules
    cp -rp ${S}/msm_kgsl.ko ${DEPLOYDIR}/kernel_modules
}

addtask do_deploy after do_install
FILES:${PN} += "${base_libdir}/modules/${KERNEL_VERSION}/*"
FILES:${PN} += "${base_libdir}/modules/*"
FILES:${PN} += "${sysconfdir}/udev/rules.d/kgsl.rules"
FILES:${PN} += "${sysconfdir}/modules-load.d/msm_kgsl.conf"