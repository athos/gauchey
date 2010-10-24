(ns gauchey.test.util.digest
  (:use gauchey.util.digest
	clojure.test))

(deftest test-digest-hexify
  (is (= (digest-hexify "abc") "616263")))
