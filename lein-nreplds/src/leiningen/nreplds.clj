(ns leiningen.nreplds
  (require [robert.hooke]
           [leiningen.repl]))

(defn skip-ensure-port-number
  "leiningen version 2.4.2 (at least) use ensure-port to force port number usage. this hook skip the execution."
  [f s]
  s)

(defn ^:no-project-needed nreplds
  [project & args]
  (robert.hooke/with-scope
    (robert.hooke/add-hook #'leiningen.repl/ensure-port #'leiningen.nreplds/skip-ensure-port-number)
    (apply leiningen.repl/repl project args)
    ))
  
