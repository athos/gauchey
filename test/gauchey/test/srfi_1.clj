(ns gauchey.test.srfi-1
  (:require [gauchey.srfi-1 :as srfi-1])
  (:use clojure.test))

(deftest test-xcons
  (is (= (srfi-1/xcons '(2 3) 1)
	 '(1 2 3))))

(deftest test-cons*
  (is (= (srfi-1/cons* 1 2 '(3))
	 '(1 2 3))))

(deftest test-list-tabulate
  (is (= (srfi-1/list-tabulate 4 identity)
	 '(0 1 2 3))))

(deftest test-circular-list
  nil)

(deftest test-iota
  (is (= (srfi-1/iota 5)
	 '(0 1 2 3 4)))
  (is (= (srfi-1/iota 5 0 -0.5)
	 '(0 -0.5 -1.0 -1.5 -2.0))))

(deftest test-not-pair?
  nil)

(deftest test-list=
  (is (srfi-1/list= (fn [[x0 y0] [x1 y1]] (and (= x0 x1) (= y0 y1)))
		    '((0 1) (2 3) (4 5))
		    '((0 1) (2 3) (4 5))))
  (is (not (srfi-1/list= (fn [[x0 y0] [x1 y1]] (and (= x0 x1) (= y0 y1)))
			 '((0 1) (2 3) (4 5))
			 '((0 1) (2 2) (4 5))))))

(deftest test-first
  (is (= (srfi-1/first '(1 2 3))
	 1)))

(deftest test-second
  (is (= (srfi-1/second '(1 2 3))
	 2)))

(deftest test-third
  (is (= (srfi-1/third '(1 2 3))
	 3)))

(deftest test-fourth
  (is (= (srfi-1/fourth '(1 2 3 4 5 6 7 8 9 10))
	 4)))

(deftest test-fifth
  (is (= (srfi-1/fifth '(1 2 3 4 5 6 7 8 9 10))
	 5)))

(deftest test-sixth
  (is (= (srfi-1/sixth '(1 2 3 4 5 6 7 8 9 10))
	 6)))

(deftest test-seventh
  (is (= (srfi-1/seventh '(1 2 3 4 5 6 7 8 9 10))
	 7)))

(deftest test-eighth
  (is (= (srfi-1/eighth '(1 2 3 4 5 6 7 8 9 10))
	 8)))

(deftest test-ninth
  (is (= (srfi-1/ninth '(1 2 3 4 5 6 7 8 9 10))
	 9)))

(deftest test-tenth
  (is (= (srfi-1/tenth '(1 2 3 4 5 6 7 8 9 10))
	 10)))

(deftest test-car+cdr
  (is (= (srfi-1/car+cdr '(1 2))
	 [1 '(2)])))

(deftest test-take
  (is (= (srfi-1/take '(a b c d e) 2)
	 '(a b))))

(deftest test-drop
  (is (= (srfi-1/drop '(a b c d e) 2)
	 '(c d e))))

(deftest test-take-right
  (is (= (srfi-1/take-right '(a b c d e) 2)
	 '(d e))))

(deftest test-drop-right
  (is (= (srfi-1/drop-right '(a b c d e) 2)
	 '(a b c))))

(deftest test-take!
  nil)

(deftest test-drop-right!
  nil)

(deftest test-split-at!
  nil)

(deftest test-last
  (is (= (srfi-1/last '(1 2 3))
	 3)))

(deftest test-length+
  nil)

(deftest test-concatenate
  (is (= (srfi-1/concatenate '((0 1) (2 3) (4 5)))
	 '(0 1 2 3 4 5))))

(deftest test-concatenate!
  nil)

(deftest test-append-reverse
  (is (= (srfi-1/append-reverse '(2 1 0) '(3 4))
	 '(0 1 2 3 4))))

(deftest test-append-reverse!
  nil)

(deftest test-zip
  (is (= (srfi-1/zip '(one two three)
		     '(1 2 3)
		     '(odd even odd even odd even odd even))
	 '((one 1 odd) (two 2 even) (three 3 odd)))))

(deftest test-unzip1
  (is (= (srfi-1/unzip1 '((1 one) (2 two) (3 three)))
	 '(1 2 3))))

(deftest test-unzip2
  (is (= (srfi-1/unzip2 '((1 one) (2 two) (3 three)))
	 '[(1 2 3) (one two three)])))

(deftest test-unzip3
  (is (= (srfi-1/unzip3 '((1 one a) (2 two b) (3 three c)))
	 '[(1 2 3) (one two three) (a b c)])))

(deftest test-unzip4
  (is (= (srfi-1/unzip4 '((1 one a alpha) (2 two b beta) (3 three c gamma)))
	 '[(1 2 3) (one two three) (a b c) (alpha beta gamma)])))

(deftest test-unzip5
  (is (= (srfi-1/unzip5 '((1 one a alpha i) (2 two b beta ii) (3 three c gamma iii)))
	 '[(1 2 3) (one two three) (a b c) (alpha beta gamma) (i ii iii)])))

(deftest test-count
  (is (= (srfi-1/count even? '(3 1 4 1 5 9 2 5 6))
	 3))
  (is (= (srfi-1/count < '(1 2 4 8) '(2 4 6 8 10 12 14 16))
	 3)))

(deftest test-fold
  (is (= (srfi-1/fold + 0 '(3 1 4 1 5 9))
	 23))
  (is (= (srfi-1/fold cons '() '(a b c d e))
	 '(e d c b a)))
  (is (= (srfi-1/fold srfi-1/cons* '() '(a b c) '(1 2 3 4 5))
	 '(c 3 b 2 a 1))))

(deftest test-fold-right
  (is (= (srfi-1/fold-right cons '() '(a b c d e))
	 '(a b c d e)))
  (is (= (srfi-1/fold-right srfi-1/cons* '() '(a b c) '(1 2 3 4 5))
	 '(a 1 b 2 c 3))))

(deftest test-pair-fold
  (is (= (srfi-1/pair-fold cons '() '(a b c d e))
	 '((e) (d e) (c d e) (b c d e) (a b c d e))))
  (is (= (srfi-1/pair-fold srfi-1/cons* '() '(a b c) '(1 2 3 4 5))
	 '((c) (3 4 5) (b c) (2 3 4 5) (a b c) (1 2 3 4 5)))))

(deftest test-pair-fold-right
  (is (= (srfi-1/pair-fold-right cons '() '(a b c d e))
	 '((a b c d e) (b c d e) (c d e) (d e) (e))))
  (is (= (srfi-1/pair-fold-right srfi-1/cons* '() '(a b c) '(1 2 3 4 5))
	 '((a b c) (1 2 3 4 5) (b c) (2 3 4 5) (c) (3 4 5)))))

(deftest test-reduce
  (is (= (srfi-1/reduce list '() '(1 2 3))
	 '(3 (2 1))))
  (is (= (srfi-1/reduce * 1 '())
	 1)))

(deftest test-reduce-right
  (is (= (srfi-1/reduce-right list '() '(1 2 3))
	 '(1 (2 3))))
  (is (= (srfi-1/reduce * 1 '())
	 1)))

(deftest test-unfold
  (is (= (srfi-1/unfold empty? first rest '(1 2 3))
	 '(1 2 3)))
  (is (= (srfi-1/unfold empty? identity rest '(1 2 3))
	 '((1 2 3) (2 3) (3)))))

(deftest test-unfold-right
  (is (= (srfi-1/unfold-right empty? first rest '(1 2 3))
	 '(3 2 1)))
  (is (= (srfi-1/unfold-right empty? identity rest '(1 2 3))
	 '((3) (2 3) (1 2 3)))))

(deftest test-append-map
  (is (= (srfi-1/append-map list '(a b c))
	 '(a b c)))
  (is (= (srfi-1/append-map list '(a b c) '(1 2 3))
	 '(a 1 b 2 c 3))))

(deftest test-append-map!
  nil)

(deftest test-map!
  nil)

(deftest test-map-in-order
  (let [a (atom 0)]
    (letfn [(f [x] (let [ret [x @a]] (swap! a inc) ret))]
      (is (= (srfi-1/map-in-order f '(0 1 2))
	     '([0 0] [1 1] [2 2]))))))

(deftest test-pair-for-each
  (let [a (atom [])]
    (is (= (do (srfi-1/pair-for-each #(swap! a conj %) '(a b c))
	       @a)
	   '[(a b c) (b c) (c)]))))

(deftest test-filter-map
  (is (= (srfi-1/filter-map #(and (number? %) (* % %))
		     '(a 1 b 3 c 7))
	 '(1 9 49))))

(deftest test-filter
  (is (= (srfi-1/filter odd? '(3 1 4 5 9 2 6))
	 '(3 1 5 9))))

(deftest test-filter!
  nil)

(deftest test-remove
  (is (= (srfi-1/remove odd? '(3 1 4 5 9 2 6))
	 '(4 2 6))))

(deftest test-remove!
  nil)

(deftest test-partition
  (is (= (srfi-1/partition odd? '(3 1 4 5 9 2 6))
	 '[(3 1 5 9) (4 2 6)])))

(deftest test-partition!
  nil)

(deftest test-find
  (is (= (srfi-1/find odd? '(0 2 4 6 7 8))
	 7))
  (is (= (srfi-1/find odd? '(0 2 4 6 8))
	 nil)))

(deftest test-find-tail
  (is (= (srfi-1/find-tail odd? '(0 2 4 6 7 8))
	 '(7 8)))
  (is (= (srfi-1/find-tail odd? '(0 2 4 6 8))
	 nil)))

(deftest test-take-while
  (is (= (srfi-1/take-while even? '(0 2 4 6 7 8))
	 '(0 2 4 6)))
  (is (= (srfi-1/take-while even? '())
	 '())))

(deftest test-take-while!
  nil)

(deftest test-drop-while
  (is (= (srfi-1/drop-while even? '(0 2 4 6 7 8))
	 '(7 8)))
  (is (= (srfi-1/drop-while even? '())
	 '())))

(deftest test-span
  (is (= (srfi-1/span even? '(0 2 4 6 7 8))
	 '[(0 2 4 6) (7 8)])))

(deftest test-span!
  nil)

(deftest test-break
  (is (= (srfi-1/break odd? '(0 2 4 6 7 8))
	 '[(0 2 4 6) (7 8)])))

(deftest test-break!
  nil)

(deftest test-any
  (is (srfi-1/any odd? '(0 2 4 6 7 8)))
  (is (srfi-1/any #(or (odd? %1) (odd? %2)) '(0 2 4) '(6 7 8)))
  (is (not (srfi-1/any odd? '(0 2 4 6 8)))))

(deftest test-every
  (is (srfi-1/every even? '(0 2 4 6 8)))
  (is (not (srfi-1/every even? '(0 2 4 6 7 8))))
  (is (not (srfi-1/every #(and (even? %1) (even? %2)) '(0 2 4) '(6 7 8)))))

(deftest test-list-index
  (is (= (srfi-1/list-index odd? '(0 2 4 6 7 8))
	 4))
  (is (= (srfi-1/list-index odd? '(0 2 4 6 8))
	 nil)))

(deftest test-delete
  (is (= (srfi-1/delete 1 '(0 1 2))
	 '(0 2)))
  (is (= (srfi-1/delete 3 '(0 1 2))
	 '(0 1 2))))

(deftest test-delete!
  nil)

(deftest test-member
  (is (= (srfi-1/member 1 '(0 1 2))
	 '(1 2)))
  (is (= (srfi-1/member 3 '(0 1 2))
	 nil)))

(deftest test-delete-duplicates
  (is (= (srfi-1/delete-duplicates '(0 1 2 1 3 3 3 2))
	 '(0 1 2 3))))

(deftest test-delete-duplicates!
  nil)

(deftest test-assoc
  (is (= (srfi-1/assoc 1 '((0 a) (1 b) (2 c)))
	 '(1 b)))
  (is (= (srfi-1/assoc 3 '((0 a) (1 b) (2 c)))
	 nil)))

(deftest test-alist-cons
  (is (= (srfi-1/alist-cons 'a 0 '((b 1) (c 2)))
	 '((a 0) (b 1) (c 2)))))

(deftest test-alist-copy
  (let [x '((a 0) (b 1) (c 2))
	c (srfi-1/alist-copy x)]
    (is (and (= x c) (not (identical? x c))))))

(deftest test-alist-delete
  (is (= (srfi-1/alist-delete 'b '((a 0) (b 1) (c 2) (b 4)))
	 '((a 0) (c 2))))
  (is (= (srfi-1/alist-delete 'd '((a 0) (b 1) (c 2)))
	 '((a 0) (b 1) (c 2)))))

(deftest test-alist-delete!
  nil)

(deftest test-lset<=
  (is (srfi-1/lset<= = '(b e a) '(a e b) '(e e b a)))
  (is (not (srfi-1/lset<= = '(2 0 1 3) '(1 0 2))))
  (is (not (srfi-1/lset<= = '(2 0 1) '(1 0 4)))))

(deftest test-lset=
  (is (srfi-1/lset= = '(2 0 1) '(1 0 2)))
  (is (not (srfi-1/lset= = '(2 0 1 3) '(1 0 2))))
  (is (not (srfi-1/lset= = '(2 0 1) '(1 3 0 2)))))

(deftest test-lset-adjoin
  (is (srfi-1/lset= =
                    (srfi-1/lset-adjoin = '(2 0 1) 3)
		    '(2 0 1 3)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-adjoin = '(2 0 1) 1)
		    '(2 0 1))))

(deftest test-lset-union
  (is (srfi-1/lset= =
                    (srfi-1/lset-union = '(2 0 1) '(3 5))
		    '(2 0 1 3 5)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-union = '(2 0 1) '(1 3))
		    '(2 0 1 3)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-union = '() '(1 0 2))
		    '(1 0 2))))

(deftest test-lset-union!
  nil)

(deftest test-lset-intersection
  (is (srfi-1/lset= =
                    (srfi-1/lset-intersection = '(2 0 1) '(1 0 3))
		    '(1 0)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-intersection = '(2 0 1) '(4 5))
		    '()))
  (is (srfi-1/lset= =
                    (srfi-1/lset-intersection = '() '(1 0 2))
		    '())))

(deftest test-lset-intersection!
  nil)

(deftest test-lset-difference
  (is (srfi-1/lset= =
                    (srfi-1/lset-difference = '(2 0 1) '(1 0 3))
		    '(2)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-difference = '(2 0 1) '(4 5))
		    '(2 0 1)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-difference = '() '(1 0 2))
		    '())))

(deftest test-lset-difference!
  nil)

(deftest test-lset-diff+intersection
  (is (srfi-1/lset= =
                    (srfi-1/lset-diff+intersection = '(2 0 1) '(1 0 3))
		    '[(2) (0 1)]))
  (is (srfi-1/lset= =
                    (srfi-1/lset-diff+intersection = '(2 0 1) '(4 5))
		    '[(2 0 1) ()]))
  (is (srfi-1/lset= =
                    (srfi-1/lset-diff+intersection = '() '(1 0 2))
		    '[() ()])))

(deftest test-lset-diff+intersection!
  nil)

(deftest test-lset-xor
  (is (srfi-1/lset= =
                    (srfi-1/lset-xor = '(2 0 1) '(1 0 3))
		    '(2 3)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-xor = '(2 0 1) '(4 5))
		    '(2 0 1 4 5)))
  (is (srfi-1/lset= =
                    (srfi-1/lset-xor = '() '(1 0 2))
		    '(1 0 2))))

(deftest test-lset-xor!
  nil)
