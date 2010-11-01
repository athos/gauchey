(ns gauchey.rfc.base64
  (:use [clojure.contrib.def :only (defnk)]
	[clojure.set :only (map-invert)]))

(def *decode-table*
  (into {}
	(map (fn [c i] [c i])
	     (str "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef"
		  "ghijklmnopqrstuvwxyz0123456789+/")
	     (iterate inc 0))))

(def *encode-table*
  (assoc (map-invert *decode-table*) (count *decode-table*) \=))

(defn- byte* [b]
  (let [b (.byteValue b)]
    (if (neg? b)
      (+ b 256)
      b)))

(defn- read-byte []
  (.read *in*))

(defn- write-byte [b]
  (.write *out* b))

(defnk base64-encode [:line-width 76]
  (let [maxcol (and line-width (> line-width 0) (dec line-width))]
    (letfn [(emit* [col & [index & indices]]
	      (if (nil? index)
		col
		(do (write-byte (int (*encode-table* index)))
		    (let [col2 (if (= col maxcol)
				 (do (newline) 0)
				 (inc col))]
		      (apply emit* col2 indices)))))
	    (state [c on-eof cont]
	      (if (neg? c)
		(on-eof)
		#(cont)))
	    (e0 [c col]
	      (state c
		(constantly nil)
		#(e1 (read-byte) (mod c 4) (emit* col (quot c 4)))))
	    (e1 [c hi col]
	      (state c
		#(emit* col (* hi 16) 64 64)
		#(e2 (read-byte) (mod c 16)
		     (emit* col (+ (* hi 16) (quot c 16))))))
	    (e2 [c hi col]
	      (state c
		#(emit* col (* hi 4) 64)
		#(e0 (read-byte)
		     (emit* col (+ (* hi 4) (quot c 64)) (mod c 64)))))]
      (trampoline #(e0 (read-byte) 0)))))

(defnk base64-encode-string [string :line-width 76]
  (with-out-str
    (with-in-str string
      (base64-encode :line-width line-width))))

(defn base64-decode []
  (letfn [(state [c on-found on-not-found]
	    (or (neg? c)
		(let [c (char c)]
		  (or (= c \=)
		      (if-let [v (*decode-table* c)]
			#(on-found v))
		      #(on-not-found)))))
	  (d0 [c]
	    (state c
	      #(d1 (read-byte) %)
	      #(d0 (read-byte))))
	  (d1 [c hi]
	    (state c
	      #(do (write-byte (+ (* hi 4) (quot % 16)))
		   (d2 (read-byte) (mod % 16)))
	      #(d1 (read-byte) hi)))
	  (d2 [c hi]
	    (state c
	      #(do (write-byte (+ (* hi 16) (quot % 4)))
		   (d3 (read-byte) (mod % 4)))
	      #(d2 (read-byte) hi)))
	  (d3 [c hi]
	    (state c
	      #(do (write-byte (+ (* hi 64) %))
		   (d0 (read-byte)))
	      #(d3 (read-byte) hi)))]
    (trampoline #(d0 (read-byte)))))

(defn base64-decode-string [string]
  (with-out-str
    (with-in-str string
      (base64-decode))))
