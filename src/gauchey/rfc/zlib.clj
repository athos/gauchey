(ns gauchey.rfc.zlib
  (:use [clojure.contrib.def :only (defnk)])
  (:import [java.util.zip Deflater Inflater
	    DeflaterOutputStream InflaterInputStream
	    GZIPOutputStream GZIPInputStream]))

(gen-interface
  :name gauchey.rfc.zlib.XflatingStream
  :methods [[getTotalIn [] int]
	    [getTotalOut [] int]
	    [getAdler32 [] int]
	    [getDataType [] String]])

(gen-class
  :name gauchey.rfc.zlib.DeflatingOutputStream
  :extends java.util.zip.DeflaterOutputStream
  :implements [gauchey.rfc.zlib.XflatingStream]
  :init init-deflating
  :state state
  :constructors {[java.io.OutputStream int int int "[B" boolean]
		 [java.io.OutputStream java.util.zip.Deflater int]})

(defn -init-deflating [out clevel size strategy dict owner?]
  (let [deflater (Deflater. clevel)]
    (.setStrategy deflater strategy)
    (when dict
      (.setDictionary deflater dict))
    [[out deflater size] {:deflater deflater, :owner? owner?}]))

(gen-class
  :name gauchey.rfc.zlib.InflatingInputStream
  :extends java.util.zip.InflaterInputStream
  :implements [gauchey.rfc.zlib.XflatingStream])

(import '[gauchey.rfc.zlib XflatingStream DeflatingOutputStream InflatingInputStream])

;; (defn -close [this]
;;   (if (:owner? (.state this))
;;     (.close this)  ; FIXME: How can I call super.close() ?
;;     (doto this
;;       .flush
;;       .finish)))

;; constants

;; compression levels
(def Z_NO_COMPRESSION      Deflater/NO_COMPRESSION)
(def Z_BEST_SPEED          Deflater/BEST_SPEED)
(def Z_BEST_COMPRESSION    Deflater/BEST_COMPRESSION)
(def Z_DEFAULT_COMPRESSION Deflater/DEFAULT_COMPRESSION)

;; strategy
(def Z_DEFAULT_STRATEGY Deflater/DEFAULT_STRATEGY)
(def Z_FILTERED         Deflater/FILTERED)
(def Z_HUFFMAN_ONLY     Deflater/HUFFMAN_ONLY)
(def Z_RLE              3)
(def Z_FIXED            4)

(defnk open-deflating-port [drain
			    :compression-level Z_DEFAULT_COMPRESSION
			    :buffer-size 4096
			    :window-bits nil
			    :memory-level nil
			    :strategy Z_DEFAULT_STRATEGY
			    :dictionary nil
			    :owner? false]
  (when (or window-bits
	    memory-level
	    (= compression-level Z_RLE)
	    (= compression-level Z_FIXED))
    (throw (UnsupportedOperationException.)))
  (DeflatingOutputStream. drain compression-level buffer-size strategy dictionary owner?))

(defn open-inflating-port [source]
  nil)

;; xflating port methods
(defn -getTotalIn [this]
  (.getTotalIn (:deflater (.state this))))

(defn zstream-total-in [^XflatingStream xflating-port]
  (.getTotalIn xflating-port))

(defn -getTotalOut [this]
  (.getTotalOut (:deflater (.state this))))

(defn zstream-total-out [^XflatingStream xflating-port]
  (.getTotalOut xflating-port))

(defn -getAdler32 [this]
  (.getAdler (:deflater (.state this))))

(defn zstream-adler32 [^XflatingStream xflating-port]
  (.getAdler32 xflating-port))

(defn -getDataType [this]
  (throw (UnsupportedOperationException.)))

(defn zstream-data-type [^XflatingStream xflating-port]
  (.getDataType xflating-port))

(defn zstream-params-set! [deflating-port]
  nil)

(defn zstream-dictionary-adler32 [deflating-port]
  nil)

(defn deflating-port-full-flush [deflating-port]
  nil)

(defn inflate-sync [inflating-port]
  nil)

(defn zlib-version []
  nil)

(defn deflate-string [string]
  nil)

(defn inflate-string [string]
  nil)

(defn gzip-encode-string [string]
  nil)

(defn gzip-decode-string [string]
  nil)

(defn crc32 [string]
  nil)

(defn alder32 [string]
  nil)
