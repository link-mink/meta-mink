# Copyright (C) 2021 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "link-mINK mink core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

SRC_URI = "git://github.com/link-mink/mink-core.git;protocol=https;branch=feature-json-rpc"
SRC_URI[sha256sum] = "bce3118ab1fea9c53bb579100e6bca3cb9a8b6b1f145bfab0c3c28397d6cabe0"
SRCREV = "8dd2086d3b32e653e42814c2b49fe196740d4fce"

DEPENDS += " \
            pkgconfig-native gperf-native libtool automake autoconf \
            ncurses lksctp-tools bash\
            boost libcap"

S = "${WORKDIR}/git"

inherit autotools

TARGET_CXXLD:remove = " \
                        -Wformat-security"

TARGET_CPPFLAGS:append = " \
                        -I${S}/src/include \
                        -I${S}/src/services/config \
                        -I${S}/src/services/routing \
                        -Wno-format-security"

RDPENDS = "bash"

# EXTRA_OECONF:append = "--enable-configd=no"

do_compile:prepend() {
    cp ${S}/version.sh ${WORKDIR}/build
    cp ${S}/authors.sh ${WORKDIR}/build
    cp ${S}/changelog.sh ${WORKDIR}/build

    mkdir -p ${WORKDIR}/build/lib/libantlr3c-3.4
    cp -r ${S}/lib/libantlr3c-3.4/include ${WORKDIR}/build/lib/libantlr3c-3.4
}

do_install() {
    # TODO: add install section
}
