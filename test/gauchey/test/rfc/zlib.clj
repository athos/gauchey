(ns gauchey.test.rfc.zlib
  (:use gauchey.rfc.zlib
	clojure.test)
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [java.util.zip DeflaterOutputStream InflaterInputStream
	                  GZIPOutputStream GZIPInputStream
	                  CRC32 Adler32]))

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
	      compressed-by-deflate))
  (is (bytes= (compress-by #(open-deflating-port % :window-bits 31))
	      compressed-by-gzip)))

(defn decompress-by [is-maker data]
  (let [bais (ByteArrayInputStream. data)]
    (.getBytes (slurp (is-maker bais)))))

(deftest test-open-inflating-port
  (is (bytes= (decompress-by #(open-inflating-port %)
			     compressed-by-deflate)
	      (decompress-by #(InflaterInputStream. %)
			     compressed-by-deflate)))
  (is (bytes= (decompress-by #(open-inflating-port % :window-bits 31)
			     compressed-by-gzip)
	      (decompress-by #(GZIPInputStream. %)
			     compressed-by-gzip))))

(deftest test-deflate-string
  (is (bytes= (deflate-string "hoge")
	      compressed-by-deflate)))

(deftest test-inflate-string
  (is (bytes= (inflate-string compressed-by-deflate)
	      (.getBytes "hoge"))))

(deftest test-gzip-encode-string
  (is (bytes= (gzip-encode-string "hoge")
	      compressed-by-gzip)))

(deftest test-gzip-decode-string
  (is (bytes= (gzip-decode-string compressed-by-gzip)
	      (.getBytes "hoge"))))

(deftest test-crc32
  (is (crc32 "hoge")
      (doto (CRC32.)
	(.update (.getBytes "hoge"))
	(.getValue))))

(deftest test-adler32
  (is (adler32 "hoge")
      (doto (Adler32.)
	(.update (.getBytes "hoge"))
	(.getValue))))