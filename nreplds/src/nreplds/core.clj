
(ns nreplds.core
  (:require [clojure.tools.nrepl :as nrepl]
            [clojure.tools.nrepl.server :as nrepl-server]
            [clojure.tools.nrepl.transport :as t]
            [clojure.java.io :as io])
  (:import (org.newsclub.net.unix AFUNIXSocket AFUNIXServerSocket AFUNIXSocketAddress)
           (clojure.tools.nrepl.server Server)))

(defn connect
  "Connects to a socket-based REPL at the given host (defaults to localhost) and port,
returning the Transport (by default clojure.tools.nrepl.transport/bencode)
for that connection.

Transports are most easily used with `client`, `client-session`, and
`message`, depending on the semantics desired."
  [& {:keys [path transport-fn] :or {transport-fn t/bencode}}]
  (transport-fn (AFUNIXSocket/connectTo (AFUNIXSocketAddress. (io/file path)))))

(defmethod nrepl/url-connect "nreplds"
  [uri]
  (connect :path (.getPath (#'nrepl/to-uri uri))))

;; See https://github.com/clojure/tools.nrepl/blob/master/src/main/clojure/clojure/tools/nrepl/server.clj#L114
(defn start-server
  "Starts a Unix domain socket-based nREPL server. Configuration options include:

* :path — the file system path to the domain socket
* :handler — the nREPL message handler to use for each incoming connection;
defaults to the result of `(default-handler)`
* :transport-fn — a function that, given a java.net.Socket corresponding
to an incoming connection, will return an value satisfying the
clojure.tools.nrepl.Transport protocol for that Socket.

Returns a (map) handle to the server that is started, which may be stopped
either via `stop-server`, (.close server), or automatically via `with-open`.
The port that the server is open on is available in the :port slot of the
server map (useful if the :port option is 0 or was left unspecified."
  [& {:keys [path transport-fn handler greeting-fn]}]
  (let [ss (AFUNIXServerSocket/bindOn (AFUNIXSocketAddress. (io/file path)))
        server (assoc
                 (Server. ss
                          0
                          (atom #{})
                          (or transport-fn t/bencode)
                          greeting-fn
                          (or handler (nrepl-server/default-handler)))
                 ;; TODO here for backward compat with 0.2.x; drop eventually
                 :ss ss)]
    (future (#'nrepl-server/accept-connection server))
    server))
