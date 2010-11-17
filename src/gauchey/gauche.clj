(ns gauchey.gauche)

(defn sys-pipe []
  (let [in  (java.io.PipedInputStream.)
	out (java.io.PipedOutputStream. in)]
    [in out]))
