(ns gauchey.util.digest)

(defprotocol DigestAlgorithmMeta
  (make-algorithm [this])
  (digest [this])
  (digest-string [this str]))

(defprotocol DigestAlgorithm
  (digest-update! [this])
  (digest-final! [this]))

(defn default-digest-string [algorithm-meta str]
  (with-in-str str (make-algorithm algorithm-meta)))

(defn digest-hexify [str]
  (with-out-str
    (doseq [byte (.getBytes str)]
      (printf "%02x" byte))))
