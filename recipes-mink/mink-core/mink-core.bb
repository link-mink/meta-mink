# Copyright (C) 2021 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "link-mINK mink core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

PV = "1.1.5"
SRC_URI = "git://github.com/link-mink/mink-core.git;protocol=https;branch=main;tag=v${PV}"

DEPENDS += " \
            pkgconfig-native gperf-native libtool automake autoconf \
            ncurses lksctp-tools bash openssl \
            boost libcap procps zlib \
            "

S = "${WORKDIR}/git"

inherit autotools

TARGET_CXXLD:remove = " \
                        -Wformat-security"

TARGET_CPPFLAGS:append = " \
                        -I${S}/src/include \
                        -I${S}/src/services/config \
                        -I${S}/src/services/routing \
                        -I${S}/src/proto \
                        -Wno-format-security \
                        -std=c++11 \
                        "

RDPENDS = "bash"

PACKAGECONFIG_CONFARGS:append = " \
                        --enable-mdebug=no \
                        --enable-grpc=no \
                        --enable-codegen=no \
                        --enable-openwrt=no \
                        --enable-configd=no \
                        --enable-syslog=no \
                        --enable-clips=no \
                        --enable-tlsv12=no \
                        --enable-plain-ws=no \
                        --enable-ws-single-session=no \
                        --enable-jrpc=yes \
                        --enable-sysagent=yes \
                        --enable-openssl=yes \
                        --enable-gdttrac=yes \
                        --with-gdt-csize=1024 \
                        --with-boost="${D}${libdir} ${D}${includedir}" \
                        "

SOLIBS = "*.so.*"
SOLIBSDEV = "*.so"
FILES_SOLIBSDEV = ""

FILES:${PN}-lib-mink = "${libdir}/mink/${SOLIBS} ${libdir}/${SOLIBSDEV}"
PACKAGES:append = " ${PN}-lib-mink"

do_compile:prepend() {
    # prepare these for the build
    cp ${S}/version.sh ${WORKDIR}/build
    cp ${S}/authors.sh ${WORKDIR}/build
    cp ${S}/changelog.sh ${WORKDIR}/build

    # prepare libantlr3c-3.4 on a correct location for the buildsystem
    mkdir -p ${WORKDIR}/build/lib/libantlr3c-3.4
    cp -r ${S}/lib/libantlr3c-3.4/include ${WORKDIR}/build/lib/libantlr3c-3.4
}

do_install:append() {
    # remove uncessary symlinks to satisfy QA
    find ${D}${libdir} -type l -delete -name "$SOLIBSDEV"
    find ${D}${libdir}/mink -type l -delete -name "$SOLIBSDEV"
}
