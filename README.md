# Yakumo
Yakumo is a TCP server and client for transferring files. It uses its own simple protocol.

It supports bi-directional file transfer, as well as server security configuration and a convenient terminal interface. Yakumo is mostly convenient for transferring files over LAN. While you can host a public server to the world, you should be careful with the data you transfer and store, as well as the password you set up, as the connection is currently not encrypted.

# Download & how to use

## Supported platforms
Yakumo is designed to be platform-agnostic, and so it is cross-platform and very portable. It is expected to work all desktop/server operating systems with Scala/Java support.

Although cross-platform support is very good, Yakumo is only officially tested for these systems:
* Linux-based
* Windows
* FreeBSD
* Android (with Termux)

## Download
Grab the program from the [releases](https://github.com/spacebanana420/yakumo/releases)

## Requirements
* [Scala](https://scala-lang.org) (recommended version 3.0 or above)
or
* Java 11 or higher

## How to use
Launch the jar program with ```scala yakumo.jar```. You will be prompted to choose if you want to open a server or connect to one using the built-in client.

### Server
The server requires a port, and defaults to ```42069```.

If it doesn't exist, a config.txt file will be created where the program exists. This configuration file is necessary and contains the following options:

* ```usepass```: Whether to use password security or not. Default is ```yes```
* ```password```: The password for your server connections. Default is ```test123```, and you should change it to something secure of course.
* ```directory```: The storage directory for the server. Default is ".", which corresponds to where yakumo.jar is.
* ```maxperfile```: The maximum allowed file size per file, in gigabytes. The server will ignore files that are bigger than this.
* ```maxtotal```: The maximum storage limit of the server, in gigabytes. The server will ignore all file uploads if it's full
* ```pathsperline```: This setting is unused for now

### Client

The client requires an IP and port, and defaults to ```localhost``` and ```42069```.

When you open the client, a file browser opens, where you can select the file you want to upload. After choosing a file, you can connect to the server.

Type the password so the server accepts your connection. Your file will be uploaded to the server if the password is correct, the server's directory isn't full and your file doesn't exceed the server's size limit.

# Building from source
You require [Scala 3.0](https://scala-lang.org) or higher to build Yakumo

There are 3 ways to build Yakumo, depending on your system and its setup.

### Scalac
Open a terminal in the root of the project and type `scalac src/*.scala src/*/*.scala -d yakumo.jar`

### Scala-CLI (recommended)
Open a terminal in the root of the project and type `scala-cli --power package src --assembly --preamble=false -f -o build/yakumo.jar`

### Using Bash
If you have Bash on your system, run build.sh: ```bash build.sh```

### NixOS
If you are using NixOS, you can install my developer environment by running ```nix-shell``` on the root directory of the project.

To build Yakumo, paste the following command: ```nix-shell && $buildyakumo```.

The nix environment installs Scala 3 and Git and lets you compile Yakumo conveniently.

#### List of commands:
* ```$yakumo```: Launches yakumo.jar in the current directory
* ```$getyakumo``` Downloads the Github repository of Yakumo
* ```$buildyakumo``` Builds yakumo in the current directory

