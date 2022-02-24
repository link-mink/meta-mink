# Copyright (C) 2022 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "mINK-core routingd set of init scripts for systemd init style"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

inherit systemd

SYSTEMD_SERVICE:${PN} = "routingd.service"

FILES:${PN}:append = " ${systemd_system_unitdir}/routingd.service ${systemd_system_unitdir}/routingd.service.d/routingd.preconf"

SRC_URI:append = " \
                file://lib/systemd/system/routingd.service \
                file://lib/systemd/system/routingd.service.d \
                "

S = "${WORKDIR}"

do_install:append() {
    install -d -m 755 ${D}${systemd_system_unitdir}
    cp -R --no-dereference --preserve=mode -v ${S}/lib/systemd/system/routingd.service ${D}${systemd_system_unitdir}/

    install -d -m 755 ${D}${systemd_system_unitdir}/routingd.service.d
    cp -R --no-dereference --preserve=mode -v ${S}/lib/systemd/system/routingd.service.d/routingd.preconf ${D}${systemd_system_unitdir}/routingd.service.d/
}
