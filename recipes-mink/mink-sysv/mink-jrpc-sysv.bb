# Copyright (C) 2022 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "link-mINK mink core set of init scripts for sysV init style"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

# try to match with mink-core version
PV = "1.1.5"
SRC_URI = " \
            file://etc/init.d/jrpcd \
            "

DEPENDS += " \
            mink-core \
            "

S = "${WORKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "jrpcd"
INITSCRIPT_PARAMS = "start 44 2 3 4 5 ."

do_install () {
    install -d -m 755 ${D}${sysconfdir}/init.d
    cp -R --no-dereference --preserve=mode -v ${S}/etc/init.d/${INITSCRIPT_NAME} ${D}${sysconfdir}/init.d/
}

