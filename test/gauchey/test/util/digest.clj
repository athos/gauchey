(ns gauchey.test.util.digest
  (:use gauchey.util.digest
	clojure.test))

(defrecord TestAlgorithmMeta []
  DigestAlgorithmMeta
   (digest [this] (slurp *in*)))

(deftest test-default-digest-string
  (is (= (default-digest-string (TestAlgorithmMeta.) "hoge")
	 "hoge")))

(deftest test-digest-hexify
  (is (= (digest-hexify "abc") "616263")))
