(ns gauchey.test.rfc.base64
  (:use gauchey.rfc.base64
	clojure.test))

(def *pre-calculated-values*
  {"" "",
   "a" "YQ==",
   "hogefuga" "aG9nZWZ1Z2E="})

(defmacro deftest* [name & body]
  `(deftest ~name
     (doseq [[~'value ~'result] *pre-calculated-values*]
       ~@body)))

(deftest* test-base64-encode
  (is (= (with-out-str (with-in-str value (base64-encode)))
	 result)))

(deftest* test-base64-encode-string
  (is (= (base64-encode-string value)
	 result)))

(deftest* test-base64-decode
  (is (= (with-out-str (with-in-str result (base64-decode)))
	 value)))

(deftest* test-base64-decode-string
  (is (= (base64-decode-string result)
	 value)))
