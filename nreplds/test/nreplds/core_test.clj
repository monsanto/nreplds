(ns nreplds.core-test
  (:require [clojure.test :refer :all]
            [nreplds.core :refer :all]
            [clojure.tools.nrepl :as nrepl])
  (:import java.net.URI
           (java.nio.file Files attribute.FileAttribute)))

(def socket-file
  (-> "nreplds-core-test"
      (Files/createTempDirectory (make-array FileAttribute 0))
      (.resolve "socket")
      .toFile))

(def socket-uri
  (URI. "nreplds" (str socket-file) nil))

(def ^:dynamic *server*)

(use-fixtures :once
  #(with-open [server (start-server :path socket-file)]
     (binding [*server* server]
       (%))))

(defn- repl-eval [client code]
  (nrepl/response-values (nrepl/message client {:op "eval" :code code})))

(defn- validate-transport [transport]
  (with-open [t transport]
    (is (= [5] (repl-eval (nrepl/client transport 1000)
                          (nrepl/code (+ 2 3)))))))

(deftest test-connect
  (validate-transport (connect :path socket-file)))

(deftest test-url-connect
  (validate-transport (nrepl/url-connect socket-uri)))
