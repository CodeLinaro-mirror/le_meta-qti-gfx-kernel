DESCRIPTION = "QTI Graphics devicetree"
LICENSE          = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

inherit linux-kernel-base deploy

PR = "r0"

FILESPATH   =. "${WORKSPACE}:"
SRC_URI     =  "file://vendor/qcom/opensource/graphics-devicetree/"

S = "${WORKDIR}/vendor/qcom/opensource/graphics-devicetree"

do_compile[depends] = "virtual/kernel:do_shared_workdir"
do_compile[cleandirs] += "${WORKDIR}/out/${KERNEL_DEFCONFIG}"

do_configure[noexec] = "1"

do_compile() {
    cd ${KERNEL_PLATFORM_PATH}
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")} \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/graphics-devicetree \
    INPLACE_COMPILE=y \
    KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
    OUT_DIR=${WORKDIR}/out/${KERNEL_DEFCONFIG} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    ./build/build_module.sh
}

do_deploy() {
    install -d ${DEPLOYDIR}/tech_dtbs/
    install -m 0644 \
    ${WORKDIR}/vendor/qcom/opensource/graphics-devicetree/gpu/*.dtbo \
    ${DEPLOYDIR}/tech_dtbs/
}

addtask do_deploy after do_install

ALLOW_EMPTY:${PN} = "1"
