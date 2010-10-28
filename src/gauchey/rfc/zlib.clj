(ns gauchey.rfc.zlib
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
  :state deflater
  :constructors {[java.io.OutputStream int int]
		 [java.io.OutputStream java.util.zip.Deflater int]})

(defn -init-deflating [out size level]
  (let [deflater (Deflater.)]
    [[out deflater size] deflater]))

(gen-class
  :name gauchey.rfc.zlib.InflatingInputStream
  :extends java.util.zip.InflaterInputStream
  :implements [gauchey.rfc.zlib.XflatingStream])

(import '[gauchey.rfc.zlib XflatingStream
	                   DeflatingOutputStream
	                   InflatingInputStream])

(defn open-deflating-port [drain]
  (DeflatingOutputStream. drain 4092 0))

(defn open-inflating-port [source]
  nil)

;; xflating port methods
(defn -getTotalIn [this]
  (.getTotalIn (.deflater this)))

(defn zstream-total-in [^XflatingStream xflating-port]
  (.getTotalIn xflating-port))

(defn -getTotalOut [this]
  (.getTotalOut (.deflater this)))

(defn zstream-total-out [^XflatingStream xflating-port]
  (.getTotalOut xflating-port))

(defn -getAdler32 [this]
  (.getAdler (.deflater this)))

(defn zstream-adler32 [^XflatingStream xflating-port]
  (.getAdler32 xflating-port))

(defn -getDataType [this]
  nil)

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
