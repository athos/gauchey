(ns gauchey.test.gauche.process
  (:use gauchey.gauche.process
	clojure.test))

(deftest test-shell-escape-string
  (is (= (shell-escape-string "")
	 "''"))
  (is (= (shell-escape-string "hoge'fuga")
	 "'hoge'\"'\"'fuga'"))
  (is (= (shell-escape-string "hoge fuga")
	 "'hoge fuga'")))
