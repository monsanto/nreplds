(defproject nreplds "0.1"
  :description "Unix domain socket support for nREPL"
  :url "https://github.com/monsanto/nreplds"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [com.kohlschutter.junixsocket/junixsocket-native-common "2.0.4"]])
