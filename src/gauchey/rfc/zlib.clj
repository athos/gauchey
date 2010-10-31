(ns gauchey.rfc.zlib
  (:use [clojure.contrib.def :only (defnk)])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [java.util.zip Deflater Inflater
	    DeflaterOutputStream InflaterInputStream
	    GZIPOutputStream GZIPInputStream
	    CRC32 Adler32]))

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
  :exposes {def {:get getXflater, :set setXflater}}
  :constructors {[java.io.OutputStream int int int "[B" boolean]
		 [java.io.OutputStream java.util.zip.Deflater int]})

(defn -init-deflating [out clevel size strategy dict owner?]
  (let [deflater (Deflater. clevel)]
    (.setStrategy deflater strategy)
    (when dict
      (.setDictionary deflater dict))
    [[out deflater size] {:owner? owner?}]))

(gen-class
  :name gauchey.rfc.zlib.InflatingInputStream
  :extends java.util.zip.InflaterInputStream
  :implements [gauchey.rfc.zlib.XflatingStream]
  :init init-inflating
  :exposes {inf {:get getXflater, :set setXflater}}
  :constructors {[java.io.InputStream int "[B" boolean]
		 [java.io.InputStream java.util.zip.Inflater int]})

(defn -init-inflating [in size dict owner?]
  (let [inflater (Inflater.)]
    (when dict
      (.setDictionary inflater dict))
    [[in inflater size] {:owner? owner?}]))

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

(defnk open-inflating-port [source
			    :buffer-size 4096
			    :window-bits nil
			    :dictionary nil
			    :ower? false]
  (when window-bits
    (throw (UnsupportedOperationException.)))
  (InflatingInputStream. source buffer-size dictionary ower?))


;; xflating port methods
(defn -getTotalIn [this]
  (.getTotalIn (.getXflater this)))

(defn zstream-total-in [^XflatingStream xflating-port]
  (.getTotalIn xflating-port))

(defn -getTotalOut [this]
  (.getTotalOut (.getXflater this)))

(defn zstream-total-out [^XflatingStream xflating-port]
  (.getTotalOut xflating-port))

(defn -getAdler32 [this]
  (.getAdler (.getXflater this)))

(defn zstream-adler32 [^XflatingStream xflating-port]
  (.getAdler32 xflating-port))

(defn -getDataType [this]
  (throw (UnsupportedOperationException.)))

(defn zstream-data-type [^XflatingStream xflating-port]
  (.getDataType xflating-port))

(defnk zstream-params-set! [deflating-port
			   :compression-level nil
			   :strategy nil]
  (when compression-level
    (.setLevel (.getXflater deflating-port) compression-level))
  (when strategy
    (.setStrategy (.getXflater deflating-port) strategy)))

(defn zstream-dictionary-adler32 [deflating-port]
  (throw (UnsupportedOperationException.)))

(defn deflating-port-full-flush [deflating-port]
  (throw (UnsupportedOperationException.)))

(defn inflate-sync [inflating-port]
  (throw (UnsupportedOperationException.)))


;; miscellaneous API
(defn- bytes* [bs]
  (if (string? bs) (.getBytes bs) bs))

(defn zlib-version []
  nil)

(defn deflate-string [string & options]
  (let [baos (ByteArrayOutputStream.)
	p (apply open-deflating-port baos options)]
    (.write p (bytes* string))
    (.close p)
    (.toByteArray baos)))

(defn inflate-string [string & options]
  (let [data (bytes* string)
	p (apply open-inflating-port (ByteArrayInputStream. data) options)]
    (.getBytes (slurp p))))

(defn gzip-encode-string [string]
  nil)

(defn gzip-decode-string [string]
  nil)

(defn crc32 [string]
  (doto (CRC32.)
    (.update (bytes* string))
    (.getValue)))

(defn adler32 [string]
  (doto (Adler32.)
    (.update (bytes* string))
    (.getValue)))
