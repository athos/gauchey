(ns gauchey.test.rfc.zlib
  (:use gauchey.rfc.zlib
	clojure.test)
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [java.util.zip DeflaterOutputStream
	                  InflaterInputStream
	                  GZIPOutputStream
	                  GZIPInputStream]))

(defn compress-by [os-maker]
  (let [baos (ByteArrayOutputStream.)]
    (spit (os-maker baos) "hoge")
    (.toByteArray baos)))

(def compressed-by-deflate
  (compress-by #(DeflaterOutputStream. %)))

(def compressed-by-gzip
  (compress-by #(GZIPOutputStream. %)))

(defn bytes= [v1 v2]
  (= (seq v1) (seq v2)))

(deftest test-open-deflating-port
  (is (bytes= (compress-by open-deflating-port)
	      compressed-by-deflate)))

(defn decompress-by [is-maker data]
  (let [bais (ByteArrayInputStream. data)]
    (.getBytes (slurp (is-maker bais)))))

(deftest test-open-inflating-port
  (is (bytes= (decompress-by #(open-inflating-port %)
			     compressed-by-deflate)
	      (decompress-by #(InflaterInputStream. %)
			     compressed-by-deflate))))
