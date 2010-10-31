(ns gauchey.rfc.zlib
  (:use [clojure.contrib.def :only (defnk)])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [java.util.zip Deflater Inflater CRC32 Adler32]))

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
  :constructors {[java.io.OutputStream java.util.zip.Deflater int boolean]
		 [java.io.OutputStream java.util.zip.Deflater int]})

(defn -init-deflating [out deflater size owner?]
  [[out deflater size] {:owner? owner?}])

(gen-class
  :name gauchey.rfc.zlib.InflatingInputStream
  :extends java.util.zip.InflaterInputStream
  :implements [gauchey.rfc.zlib.XflatingStream]
  :init init-inflating
  :exposes {inf {:get getXflater, :set setXflater}}
  :constructors {[java.io.InputStream java.util.zip.Inflater int boolean]
		 [java.io.InputStream java.util.zip.Inflater int]})

(defn -init-inflating [in inflater size owner?]
  [[in inflater size] {:owner? owner?}])

(gen-class
  :name gauchey.rfc.zlib.GZIPOutputStream
  :extends java.util.zip.GZIPOutputStream
  :implements [gauchey.rfc.zlib.XflatingStream]
  :init init-gzip-output
  :exposes {def {:get getXflater, :set setXflater}}
  :constructors {[java.io.OutputStream int boolean]
		 [java.io.OutputStream int]})

(defn -init-gzip-output [out size owner?]
  [[out size] {:owner? owner?}])

(gen-class
  :name gauchey.rfc.zlib.GZIPInputStream
  :extends java.util.zip.GZIPInputStream
  :implements [gauchey.rfc.zlib.XflatingStream]
  :init init-gzip-input
  :exposes {inf {:get getXflater, :set setXflater}}
  :constructors {[java.io.InputStream int boolean]
		 [java.io.InputStream int]})

(defn -init-gzip-input [in size owner?]
  [[in size] {:owner? owner?}])

(import '[gauchey.rfc.zlib XflatingStream
	                   DeflatingOutputStream InflatingInputStream
	                   GZIPOutputStream GZIPInputStream])

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
			    :window-bits 15
			    :memory-level nil
			    :strategy Z_DEFAULT_STRATEGY
			    :dictionary nil
			    :owner? false]
  (when (or memory-level
	    (= compression-level Z_RLE)
	    (= compression-level Z_FIXED))
    (throw (UnsupportedOperationException.)))
  (let [os (cond (<= 8 window-bits 15)
		 (let [deflater (Deflater. compression-level)]
		   (DeflatingOutputStream. drain deflater buffer-size owner?))
		 (<= 24 window-bits 31)
		 (GZIPOutputStream. drain buffer-size owner?)
		 :else (throw (UnsupportedOperationException.)))
	deflater (.getXflater os)]
    (.setStrategy deflater strategy)
    (when dictionary
      (.setDictionary deflater dictionary))
    os))

(defnk open-inflating-port [source
			    :buffer-size 4096
			    :window-bits 15
			    :dictionary nil
			    :owner? false]
  (let [is (cond (<= 8 window-bits 15)
		 (let [inflater (Inflater.)]
		   (InflatingInputStream. source inflater buffer-size owner?))
		 (<= 24 window-bits 31)
		 (GZIPInputStream. source buffer-size owner?)
		 :else (throw (UnsupportedOperationException.)))
	inflater (.getXflater is)]
    (when dictionary
      (.setDictionary inflater dictionary))
    is))

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

(defn gzip-encode-string [string & options]
  (apply deflate-string string :window-bits (+ 15 16) options))

(defn gzip-decode-string [string & options]
  (apply inflate-string string :window-bits (+ 15 16) options))

(defn crc32 [string]
  (doto (CRC32.)
    (.update (bytes* string))
    (.getValue)))

(defn adler32 [string]
  (doto (Adler32.)
    (.update (bytes* string))
    (.getValue)))
