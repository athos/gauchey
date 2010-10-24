(ns gauchey.rfc.sha
  (:use gauchey.util.digest
	[clojure.contrib.string :only (lower-case)])
  (:import java.security.MessageDigest))

(def default-algorithm-implementation
  {:digest-update! (fn [this data] (.update (:delegate this) data))
   :digest-final!  (fn [this] (.digest (:delegate this)))})

(defn- default-digest [this]
  (let [data (.getBytes (slurp *in*))
	algorithm (make-algorithm this)]
    (digest-update! algorithm data)
    (digest-final! algorithm)))

(def default-algorithm-meta-implementation
  {:make-algorithm nil
   :digest default-digest
   :digest-string default-digest-string})

(defn- symbol-append [& syms]
  (symbol (apply str syms)))

(defmacro defalgorithms
  [& algorithms]
  `(do ~@(for [[name algorithm-name] (apply hash-map algorithms)
	       :let [algorithm-meta-name (symbol-append name 'Meta)
		     lower-name (symbol (lower-case (str name)))
		     object-name (symbol-append '< lower-name '>)]]
	   `(do (defrecord ~name [~'delegate])
		(extend ~name
		  DigestAlgorithm
		  default-algorithm-implementation)
		(defrecord ~algorithm-meta-name [])
		(extend ~algorithm-meta-name
		  DigestAlgorithmMeta
		  (merge
		    default-algorithm-meta-implementation
		    {:make-algorithm
		     (fn [this#]
		       (new ~name (MessageDigest/getInstance ~algorithm-name)))}))
		(def ~object-name (new ~algorithm-meta-name))
		(defn ~(symbol-append lower-name '-digest) []
		  (digest ~object-name))
		(defn ~(symbol-append lower-name '-digest-string) [s#]
		  (digest-string ~object-name s#))))))

(defalgorithms
  SHA1   "SHA"
  SHA224 "SHA224"
  SHA256 "SHA256"
  SHA384 "SHA384"
  SHA512 "SHA512")
