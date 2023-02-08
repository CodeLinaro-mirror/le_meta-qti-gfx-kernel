SUMMARY = "QTI GFX package groups"

PACKAGE_ARCH="${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = " \
            packagegroup-qti-gfx \
           "

RDEPENDS:packagegroup-qti-gfx += " graphicsdlkm "
