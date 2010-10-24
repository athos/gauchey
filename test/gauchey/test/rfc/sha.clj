(ns gauchey.test.rfc.sha
  (:use gauchey.rfc.sha
	gauchey.util.digest
	clojure.test)
  (:import java.security.MessageDigest))

(def hash-value
  (let [algorithm (MessageDigest/getInstance "SHA")]
    (.update algorithm (.getBytes "hoge"))
    (.digest algorithm)))

(defn bytes= [& vs]
  (map #(= (seq %1) (seq %2)) vs (rest vs)))

(deftest test-digest
  (is (bytes= (with-in-str "hoge"
		(digest <sha1>))
	      (digest-string <sha1> "hoge")
	      (with-in-str "hoge"
		(sha1-digest))
	      (sha1-digest-string "hoge")
	      hash-value)))
