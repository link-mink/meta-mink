# Copyright (C) 2022 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "mINK-core sysagnetd set of init scripts for systemd init style"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
HOMEPAGE = "https://github.com/link-mink/mink-core"

inherit systemd

SYSTEMD_SERVICE:${PN} = "sysagentd.service"

FILES:${PN}:append = " ${systemd_system_unitdir}/sysagentd.service ${systemd_system_unitdir}/sysagentd.service.d/sysagentd.preconf"

SRC_URI:append = " \
                file://lib/systemd/system/sysagentd.service \
                file://lib/systemd/system/sysagentd.service.d \
                "

S = "${WORKDIR}"

do_install:append() {
    install -d -m 755 ${D}${systemd_system_unitdir}
    cp -R --no-dereference --preserve=mode -v ${S}/lib/systemd/system/sysagentd.service ${D}${systemd_system_unitdir}/

    install -d -m 755 ${D}${systemd_system_unitdir}/sysagentd.service.d
    cp -R --no-dereference --preserve=mode -v ${S}/lib/systemd/system/sysagentd.service.d/sysagentd.preconf ${D}${systemd_system_unitdir}/sysagentd.service.d/
}
