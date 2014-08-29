nreplds
=======

This project gives you the ability to 1) start an embedded nREPL server on a
Unix Domain Socket (UDS) and 2) connect to a nREPL using a UDS.

Currently nREPLs can only listen on TCP ports. Even if the port selected is
local-only, this is a security risk, since if any process (running as any user)
is compromised, it is possible for an attacker to connect to your nREPL and run
malicious code in the context of your Clojure application. If you connect to a
UDS, you can use standard Unix permissions to protect your nREPL.


Installing junixsocket
======================

Installation is easy, but slightly more complicated than other Clojure libraries
because of the need to use native code.

This library uses [junixsocket](http://code.google.com/p/junixsocket/) to
connect to UDSs. You can
[follow their instructions](http://code.google.com/p/junixsocket/wiki/GettingStarted),
or you can follow my simplified instructions:

```
wget http://junixsocket.googlecode.com/files/junixsocket-1.3-bin.tar.bz2
tar xvf junixsocket-1.3-bin.tar.bz2
cd junixsocket-1.3
mvn install:install-file -Dfile=dist/junixsocket-1.3.jar -DartifactId=junixsocket -Dversion=1.3 -DgroupId=org.newsclub -Dpackaging=jar
sudo mkdir -p /opt/newsclub
sudo cp -r lib-native /opt/newsclub
```

**If someone wants to package this up as a Maven artifact, that'd be nice!**

[This dude's notes](http://brechthouben.be/?p=13) may be helpful to you if you
are on FreeBSD.

Use
===

To run an embedded nREPL server on a UDS, add `[nreplds "0.1"]` to your
dependencies, and start the server using `(nreplds.core/start-server :path
"/path/to/socket.sock")`

To connect to a UDS programatically, and `[nreplds "0.1"]`, load `nreplds.core`,
and use the standard `clojure.tools.nrepl/url-connect` with a URI like
`nreplds:///path/to/sock.sock`. Because of an implementation detail the path must
be absolute.

To connect to `nreplds:///` URIs from shell, add `[lein-nreplds "0.1.1"]` to your
leiningen plugins. Now you can connect to a UDS using `lein nreplds :connect
nreplds:///path/to/sock.sock`. Because of an implementation detail the path must
be absolute. This connection to `nreplds:///` from shell only tested in leiningen 2.4.2.

Forwarding a UDS over SSH
=========================

You'll need to use [socat](http://www.dest-unreach.org/socat/). See [here](http://www.debian-administration.org/users/dkg/weblog/68).

A plea to whoever is in charge of Java
======================================

Please bless a standard library for POSIX functionality. Please make sure the
POSIX sockets you bless support the Java Socket and NIO APIs (junixsocket does
not support NIO). Unix domain socket support is very important.

A plea to whoever is in charge of nREPL
=======================================

I had to copy/paste some nREPL code because the interfaces provided were not sufficiently general.

- Please extend the `nrepl.server/start-server` function to optionally take a `ServerSocket`.
- Please extend the `nrepl/connect` function to optionally take a `Socket`.
- A `url-server-start` function analogous to `url-connect` would be handy for
  extending leiningen to start UDS servers.
