# Copyright (C) 2021 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "link-mINK mink core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

PV = "1.1.5"

# Generate cerficiates by yourself on files/etc/mink location. They will be then installed on
# the /etc/mink location on the system. If no certs are provided, error will trigger during the
# build and it will fail.
#
# We don't want to keep any certs on git repo so this is the user's/engineer's responsibility.

SRC_URI = " \
            git://github.com/link-mink/mink-core.git;protocol=https;branch=main;tag=v${PV} \
            file://etc/mink/cert.pem \
            file://etc/mink/dh.pem \
            file://etc/mink/key.pem \
            file://etc/mink/mink.db \
            "

DEPENDS += " \
            pkgconfig-native gperf-native libtool automake autoconf \
            ncurses lksctp-tools bash openssl \
            boost libcap procps zlib \
            "

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit autotools

TARGET_CXXLD:remove = " \
                        -Wformat-security"

TARGET_CPPFLAGS:append = " \
                        -I${S}/src/include \
                        -I${S}/src/services/config \
                        -I${S}/src/services/sysagent \
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

FILES_SOLIBSDEV = ""

FILES:${PN} = " \
                ${bindir} \
                ${libdir} \
                ${libdir}/mink \
                ${sysconfdir} \
                ${sysconfdir}/mink/cert.pem \
                ${sysconfdir}/mink/dh.pem \
                ${sysconfdir}/mink/key.pem \
                ${sysconfdir}/mink/mink.db \
                "

INSANE_SKIP:${PN} = "dev-so"

do_compile:prepend() {
    # prepare these for the build
    cp ${S}/version.sh ${B}
    cp ${S}/authors.sh ${B}
    cp ${S}/changelog.sh ${B}

    # prepare libantlr3c-3.4 on a correct location for the buildsystem
    mkdir -p ${B}/lib/libantlr3c-3.4
    cp -r ${S}/lib/libantlr3c-3.4/include ${B}/lib/libantlr3c-3.4

}

do_install:append() {
    mkdir -p ${D}${sysconfdir}/mink
    cp -R --no-dereference --preserve=mode -v ${WORKDIR}/etc/mink/* ${D}${sysconfdir}/mink/
}
