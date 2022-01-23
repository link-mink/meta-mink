<div align="center"><img src="http://139.162.200.34/mink.png"></div>

<h1 align="center">mINK framework Yocto/OpenEmbedded meta layer</h1>

mink-meta is an [Yocto/OpenEmbedded](https://www.yoctoproject.org/) layer containing recipes necessary for integration of mINK framework into the Yocto/OpenEmbedded framework based GNU/Linux distributions.

Layer is tested and is being developed against referent [Poky](https://github.com/YoeDistro/poky) distribution with the following principles:

- **do-not-touch-the-Vanilla-rule** where the layer has been included into the build from the local location on the host machine. Any modification which must be done on any other layer should not be done on that layer directly (unless for testing purposes, such as switching between init systems - ```systemd``` v ```sysV``` - and those should never be committed system wide). Each necessary modification must be done inside of the ```meta-mink``` layer by creating append recipes if necessary
- **granulation to maintainable units** in order to keep the maintenance and updates as simple and clean as possible. This applies on patches and files
- **package grouping** for implementing groups of packages into the distribution. These packages are grouped per functionality, nature and common features
- **user responsibility** which allows users/developers to add/modify the parts of the system which must be under their control, such as certificates creation and management. We do not want to expose any certificate on the Git repo, but leave these for the developers who are integrating mINK into their solutions.

Core components
  - [**Layer organization**](#layer-organization) - current layer organization
  - [**Layer development recommendation**](#layer-development-recommentadtion) - recommendatons for integrating the layer for development into the Poky reference
  - [**License**](#license)

## Layer organization

Layer is organized with the following top folders:

- ```conf``` which contains the layer configuration with layer dependencies and version compatibility
- ```recipes-core``` which contain append recipes for existing core recipes and package groups
- ```recipes-mink``` where actual mINK related recipes are stored. These include:
  - ```mink-core``` with recipes and additional set of files which are necessary for mINK framework binaries to work
  - ```mink-sysd``` with recipes for ```systemd``` init support (service files). Each service has its own recipe and set of files in order for better granulation and tracking of the past and future changes
  - ```mink-sysv``` with recipes for ```sysV``` init support (init scripts). Each service has its own recipe and set of files in order for better granulation and tracking of the past and future changes

### Layer development recommendations

As mentioned in the introduction paragraph, it is recommended to insert the layer into the Poky referent distribution from another location on the host machine. An exemplary procedure would be:

- Clone the repo locally (example in ```/opt```)

```
$ cd /opt
$ git clone https://github.com/link-mink/meta-mink.git
```
- Clone the Poky repo locall (example in ```/opt```)

```
$ cd /opt
$ git clone https://github.com/YoeDistro/poky.git poky
```
- Create a symlink from the ```poky``` location and link it with ```meta-mink``` location

```
$ ln -s /opt/meta-mink /opt/poky/meta-mink
```
- Source the ```oe-env``` at ```poky``` location
```
$ cd /opt/poky
$ source oe-init-build-env
```
- Add the ```meta-mink``` layer to the build configuration
```
$ vim /opt/poky/build/conf/bblayers.conf
```
and add the following line at the ond of ```BBLAYERS```
```
/opt/poky/meta-mink \
```
- Before starting the build, there are three steps which must be done manually to set the build correctly:
  - Adjust Poky distribution to use either ```systemd``` or ```sysV``` as an init system. By default, ```sysV``` is set and to switch to ```systemd``` modify ```/opt/poky/meta-poky/conf/distro/poky.conf``` with the following (comment out the same in order to switch back to ```sysV```):
```
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
```
  - Generate or provide the certificates which mINK will use in the runtime on the ```/opt/meta-mink/recipes-mink/mink-core/files/etc/mink``` location in the mINK layer. The method of generating certificates is of no concerne for the build system or the layer itself - certificates must be provided on this location in order for the build to pass. If the certificates are not provided build will fail. File ```cert.pem```,  ```dh.pem```,  ```key.pem``` are needed on this location.
  - Adjust the networking for the mINK routingd. This will start the daemon automatically however, if not set, it will not have a critical impact on the build. Once booted, this can be directly modified on the running system. If necessary, it can be modified at the init script (currently only for ```sysV```) at ```/opt/poky/meta-mink/recipes-mink/mink-sysv/files/etc/init.d/routingd``` by modifing ```WS_ADDRESS``` to either match the one of the system IP addresses on some interface or to be set as ```0.0.0.0:8000``` to listen on all the interfaces.
- Build the ```core-image-minimal```
```
$ cd /opt/poky/build
$ bitbake core-image-minimal
```
## License

This software is licensed under the [MIT](https://opensource.org/licenses/MIT) license
