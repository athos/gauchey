(ns gauchey.test.rfc.zlib
  (:use gauchey.rfc.zlib
	clojure.test
	[clojure.contrib.io :only (with-out-writer)])
  (:import java.io.ByteArrayOutputStream
	   [java.util.zip DeflaterOutputStream
	                  InflaterInputStream
	                  GZIPOutputStream
	                  GZIPInputStream]))

(defn compress-by [os-maker]
  (let [baos (ByteArrayOutputStream.)]
    (with-out-writer (os-maker baos)
      (print "hoge"))
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
