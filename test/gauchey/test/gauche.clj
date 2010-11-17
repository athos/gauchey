(ns gauchey.test.gauche
  (:use gauchey.gauche
	clojure.test))

(deftest test-sys-pipe
  (let [[in out] (sys-pipe)]
    (spit out "hoge")
    (is (= (slurp in) "hoge"))))
