DESCRIPTION = "QTI Graphics drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"

inherit linux-kernel-base deploy
CLEANBROKEN = "1"

PR = "r0"

DEPENDS = "rsync-native bc-native bison-native unifdef-native mmdlkm mmdlkm-headers synx-kernel synx-kernel-header"
DEPENDS:remove:qcs610-odk-64 = "mmdlkm-headers synx-kernel synx-kernel-header"

do_compile[depends] += "virtual/kernel:do_shared_workdir"
do_compile[cleandirs] += "${WORKDIR}/out/${KERNEL_DEFCONFIG}"

FILESPATH   =. "${WORKSPACE}:"
SRC_URI    +=  "file://vendor/qcom/opensource/graphics-kernel/"

KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"

S = "${WORKDIR}/vendor/qcom/opensource/graphics-kernel"

do_configure[noexec] = "1"

do_compile() {
    LE_EXTRA_CFLAGS="-I${STAGING_DIR_HOST}/usr/include"
    cd ${KERNEL_PLATFORM_PATH}
    if [ "${BASEMACHINE}" != "sdmsteppe" ]; then
        KBUILD_EXTRA_SYMBOLS="${STAGING_DIR_HOST}/${nonarch_base_libdir}/modules/${KERNEL_VERSION}/synx-kernel/Module.symvers"
    fi
    LE_EXTRA_CFLAGS="${LE_EXTRA_CFLAGS}" \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")} \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/graphics-kernel\
    INPLACE_COMPILE=y \
    KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
    OUT_DIR=${WORKDIR}/out/${KERNEL_DEFCONFIG} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR}\
    KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
    ./build/build_module.sh
}

do_install() {
    install -d ${D}${includedir}/linux
    sed 's+scripts/unifdef+${LOC_UNIFDEF:-$(dirname $0)/unifdef}+g' ${KERNEL_PLATFORM_PATH}/msm-kernel/scripts/headers_install.sh > ${S}/headers_install.sh
    python3 -u ${S}/gfx_kernel_headers.py --verbose --header_arch arm64 --gen_dir ${D}${includedir} --gfx_include_uapi ${S}/include/uapi/linux/*.h --unifdef $(which unifdef) --headers_install ${S}/headers_install.sh

    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/graphics-kernel/msm_kgsl.ko -D ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/graphics-kernel/Module.symvers -D ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/graphics-kernel/Module.symvers
}

do_deploy() {
    install -d ${DEPLOYDIR}/kernel_modules
    cp -rp ${S}/msm_kgsl.ko ${DEPLOYDIR}/kernel_modules
}

addtask do_deploy after do_install
FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/*"
FILES:${PN} += "${includedir}/*"
