# Copyright (C) 2022 Davor Popovic <davor.popeye@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Set of packages for mink-core"
LICENSE = "MIT"
PR = "r14"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

MINK_INIT_SYSV = " \
    mink-routing-sysv \
    mink-sysagent-sysv \
    mink-jrpc-sysv \
"

MINK_INIT_SYSTEMD = " \
    mink-routing-sysd \
    mink-sysagent-sysd \
    mink-jrpc-sysd \
"

MINK_CORE = " \
    mink-core \
"

RDEPENDS:${PN} = " \
    ${MINK_CORE} \
    ${@bb.utils.contains("DISTRO_FEATURES", "sysvinit", "${MINK_INIT_SYSV}", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "${MINK_INIT_SYSTEMD}", "", d)} \
"

