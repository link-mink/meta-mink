# Copyright (C) 2021 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "link-mINK mink core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

# SRCREV = "v1.1.7"
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

# Generate cerficiates by yourself on files/etc/mink location. They will be then installed on
# the /etc/mink location on the system. If no certs are provided, error will trigger during the
# build and it will fail.
#
# We don't want to keep any certs on git repo so this is the user's/engineer's responsibility.

SRC_URI = " \
    git://github.com/link-mink/mink-core.git;protocol=https;branch=dev; \
    file://etc/mink/cert.pem \
    file://etc/mink/dh.pem \
    file://etc/mink/key.pem \
    file://etc/mink/mink.db \
"

DEPENDS += " \
    pkgconfig-native \
    gperf-native \
    libtool-native \
    automake-native \
    autoconf-native \
    ncurses lksctp-tools bash \
    boost libcap procps zlib \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit autotools

TARGET_CXXLD:remove = " \
    -Wformat-security \
"

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

PACKAGECONFIG_CONFARGS = " \
    --with-gdt-csize=1024 \
    --with-boost="${D}${libdir} ${D}${includedir}" \
"

# minimal configuration set for basic mNIK instance
PACKAGECONFIG = " \
    sysagentd \
    jrpcd \
    configd \
    openssl \
    lua \
"

PACKAGECONFIG[sysagentd] = " \
    --enable-sysagent=yes, \
    --enable-sysagent=no \
"
PACKAGECONFIG[jrpcd] = " \
    --enable-jrpc=yes, \
    --enable-jrpc=no \
"
PACKAGECONFIG[grpcd] = " \
    --enable-grpc=yes, \
    --enable-grpc=no, \
    protobuf-native grpc-native protobuf grpc \
"
PACKAGECONFIG[configd] = " \
    --enable-configd=yes, \
    --enable-configd=no \
"
PACKAGECONFIG[gdttrac] = " \
    --enable-gdttrac=yes, \
    --enable-gdttrac=no \
"
PACKAGECONFIG[syslog] = " \
    --enable-syslog=yes, \
    --enable-syslog=no \
"
PACKAGECONFIG[clips] = " \
    --enable-clips=yes, \
    --enable-clips=no \
"
PACKAGECONFIG[tlsv12] = " \
    --enable-tlsv12=yes, \
    --enable-tlsv12=no \
"
PACKAGECONFIG[openssl] = " \
    --enable-openssl=yes, \
    --enable-openssl=no, \
    openssl \
"
PACKAGECONFIG[plain-ws] = " \
    --enable-plain-ws=yes, \
    --enable-plain-ws=no \
"
PACKAGECONFIG[ws-single-session] = " \
    --enable-ws-single-session=yes, \
    --enable-ws-single-session=no \
"

PACKAGECONFIG[lua] = " \
    --enable-lua=yes, \
    --enable-lua=no, \
    lua luajit \
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
    /usr/share/mink/mink.lua \
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

    # Handle protoc and grpc only if grpcd is selected.
    # Scan current packageconfig values for grpcd and if exists,
    # invoke protoc.
    cur_packageconfig="${@d.getVar('PACKAGECONFIG', True).split()}"
    if  [[ " $cur_packageconfig " =~ grpcd ]]; then
        ${STAGING_DIR_NATIVE}/usr/bin/protoc \
            --grpc_out=${S}/src/proto \
            --plugin=protoc-gen-grpc=${STAGING_DIR_NATIVE}/usr/bin/grpc_cpp_plugin \
            -I${S}/src/proto \
            ${S}/src/proto/gdt.proto

        ${STAGING_DIR_NATIVE}/usr/bin/protoc \
            --cpp_out=${S}/src/proto \
            -I${S}/src/proto \
            ${S}/src/proto/gdt.proto
    fi
}

do_install:append() {
    mkdir -p ${D}${sysconfdir}/mink
    cp -R --no-dereference --preserve=mode -v ${WORKDIR}/etc/mink/* ${D}${sysconfdir}/mink/
}
