(ns gauchey.srfi-1
  (:refer-clojure
   :rename {first core-first,
	    second core-second,
	    take core-take,
	    drop core-drop,
	    last core-last,
	    count core-count,
	    reduce core-reduce,
	    filter core-filter,
	    partition core-partition,
	    remove core-remove,
	    find core-find,
	    take-while core-take-while,
	    drop-while core-drop-while,
	    assoc core-assoc}))

(defn xcons [x y]
  (cons y x))

(defn cons* [& xs]
  (loop [[x & xs] xs, a []]
    (if (empty? xs)
      (seq (into a x))
      (recur xs (conj a x)))))

(defn list-tabulate [n init-proc]
  (for [n (range n)]
    (init-proc n)))

(defn circular-list []
  nil)

(defn iota [count & [start step]]
  (let [start (or start 0)
	step (or step 1)]
    (core-take count (iterate #(+ step %) start))))

(defn not-pair? [x]
  nil)

(defn list= [elt= & lists]
  (every? identity (apply map elt= lists)))

(defn first [pair]
  (core-first pair))

(defn second [pair]
  (core-second pair))

(defn third [pair]
  (core-first (nnext pair)))

(defn fourth [pair]
  (fnext (nnext pair)))

(defn fifth [pair]
  (nth pair 4))

(defn sixth [pair]
  (nth pair 5))

(defn seventh [pair]
  (nth pair 6))

(defn eighth [pair]
  (nth pair 7))

(defn ninth [pair]
  (nth pair 8))

(defn tenth [pair]
  (nth pair 9))

(defn car+cdr [pair]
  [(first pair) (rest pair)])

(defn take [x i]
  (core-take i x))

(defn drop [x i]
  (core-drop i x))

(defn take-right [flist i]
  (reverse (take (reverse flist) i)))

(defn drop-right [flist i]
  (reverse (drop (reverse flist) i)))

(defn take! [x i]
  nil)

(defn drop-right! [x i]
  nil)

(defn split-at! [x i]
  nil)

(defn last [pair]
  (core-last pair))

(defn length+ [x]
  nil)

(defn concatenate [list-of-lists]
  (apply concat list-of-lists))

(defn concatenate! []
  nil)

(defn append-reverse [rev-head tail]
  (core-reduce #(cons %2 %1) tail rev-head))

(defn append-reverse! []
  nil)

(defn zip [& clists]
  (apply map list clists))

(defn unzip1 [list]
  (map first list))

(defn unzip2 [list]
  [(map first list) (map second list)])

(defn unzip3 [list]
  [(map first list) (map second list) (map third list)])

(defn unzip4 [list]
  [(map first list) (map second list) (map third list) (map fourth list)])

(defn unzip5 [list]
  [(map first list) (map second list) (map third list) (map fourth list) (map fifth list)])

(defn count [pred & clists]
  (core-count
    (core-filter identity
		 (apply map (fn [& elts] (apply pred elts)) clists))))

(defn fold [kons knil & clists]
  (core-reduce #(apply kons (conj %2 %1)) knil (apply map vector clists)))

(defn fold-right [kons knil & clists]
  (core-reduce #(apply kons (conj %2 %1))
	       knil
	       (reverse (apply map vector clists))))

(defn unfold [p f g seed & [tail-gen]]
  (let [tail-gen (or tail-gen identity)]
    (loop [seed seed, ret []]
      (if (p seed)
	(seq (into ret (tail-gen seed)))
	(recur (g seed) (conj ret (f seed)))))))

(defn unfold-right [p f g seed & [tail]]
  (loop [seed seed, lis tail]
    (if (p seed)
      lis
      (recur (g seed) (cons (f seed) lis)))))

(defn pair-fold [kons knil & clists]
  (fold #(apply kons (conj %1 %2))
	knil
	(unfold #(some empty? %) vec #(map rest %) clists (constantly nil))))

(defn pair-fold-right [kons knil & clists]
  (fold-right #(apply kons (conj %1 %2))
	      knil
	      (unfold #(some empty? %) vec #(map rest %) clists (constantly nil))))

(defn reduce [f ridentity list]
  (if (empty? list)
    ridentity
    (fold f (first list) (rest list))))

(defn reduce-right [f ridentity list]
  (reduce f ridentity (reverse list)))
  
(defn append-map [f & clists]
  (apply mapcat f clists))

(defn append-map! []
  nil)

(defn map! []
  nil)

(defn map-in-order [f & clists]
  (apply map f clists))

(defn pair-for-each [f & clists]
  (loop [clists clists]
    (if (some empty? clists)
      nil
      (do (apply f clists)
	  (recur (map rest clists))))))

(defn filter-map [f & clists]
  (core-filter identity (apply map f clists)))

(defn filter [pred list]
  (core-filter pred list))

(defn filter! []
  nil)

(defn remove [pred list]
  (core-remove pred list))

(defn remove! []
  nil)

(defn partition [pred list]
  [(filter pred list) (remove pred list)])

(defn partition! []
  nil)

(defn find [pred clist]
  (loop [[x & xs :as xxs] clist]
    (cond (empty? xxs) nil
	  (pred x) x 
	  :else (recur xs))))

(defn find-tail [pred clist]
  (loop [[x & xs :as xxs] clist]
    (cond (empty? xxs) nil
	  (pred x) xxs
	  :else (recur xs))))

(defn take-while [pred clist]
  (core-take-while pred clist))

(defn take-while! [pred clist]
  nil)

(defn drop-while [pred clist]
  (core-drop-while pred clist))

(defn span [pred clist]
  [(core-take-while pred clist) (core-drop-while pred clist)])

(defn span! [pred clist]
  nil)

(defn break [pred clist]
  (span (complement pred) clist))

(defn break! [pred clist]
  nil)

(defn any [pred & clists]
  (some identity (apply map pred clists)))

(defn every [pred & clists]
  (every? identity (apply map pred clists)))

(defn list-index [pred & clists]
  (loop [clists clists, index 0]
    (cond (some empty? clists) nil
	  (apply pred (map first clists)) index
	  :else (recur (map rest clists) (inc index)))))

(defn delete [x list & [elt=]]
  (let [elt= (or elt= =)]
    (remove #(elt= x %) list)))

(defn delete! []
  nil)

(defn member [x list & [elt=]]
  (let [elt= (or elt= =)]
    (find-tail #(elt= x %) list)))

(defn delete-duplicates [list & [elt=]]
  (let [elt= (or elt= =)]
    (loop [[x & xs :as xxs] list, ret []]
      (if (empty? xxs)
	(seq ret)
	(recur (delete x xs) (conj ret x))))))

(defn delete-duplicates! []
  nil)

(defn assoc [x list & [elt=]]
  (let [elt= (or elt= =)]
    (find #(elt= x (first %)) list)))

(defn alist-cons [key datum alist]
  (cons (list key datum) alist))

(defn alist-copy [alist]
  (for [[key datum] alist]
    (list key datum)))

(defn alist-delete [key alist & [key=]]
  (let [key= (or key= =)]
    (remove #(= key (first %)) alist)))

(defn alist-delete! [key alist & [key=]]
  nil)

(defn- %lset2<= [= lis1 lis2]
  (every #(member % lis2 =) lis1))

(defn lset<= [elt= & lists]
  (or (nil? lists)
      (loop [s1 (first lists), r (rest lists)]
	(or (empty? r)
	    (let [[s2 & r] r]
	      (and (or (identical? s1 s2)
		       (%lset2<= = s1 s2))
		   (recur s2 r)))))))

(defn lset= [elt= & lists]
  (or (nil? lists)
      (loop [s1 (first lists), r (rest lists)]
	(or (empty? r)
	    (let [[s2 & r] r]
	      (and (or (identical? s1 s2)
		       (and (%lset2<= = s1 s2) (%lset2<= = s2 s1)))
		   (recur s2 r)))))))

(defn lset-adjoin [elt= list & elts]
  (fold (fn [elt ans] (if (member elt ans =) ans (cons elt ans)))
	list elts))

(defn lset-union [elt= & lists]
  (reduce (fn [lis ans]
	    (cond (empty? lis) ans
		  (empty? ans) lis
		  (identical? lis ans) ans
		  :else
		  (fold (fn [elt ans]
			  (if (any #(elt= % elt) ans)
			    ans
			    (cons elt ans)))
			ans lis)))
	  '() lists))

(defn lset-union! [elt= & lists]
  nil)

(defn lset-intersection [let= lis1 & lists]
  (let [lists (delete lis1 lists identical?)]
    (cond (any empty? lists) '()
	  (empty? lists) lis1
	  :else
	  (filter (fn [x] (every #(member x % =) lists))
		  lis1))))

(defn lset-intersection! [elt= lis1 & lists]
  nil)

(defn lset-difference [elt= lis1 & lists]
  (let [lists (filter (complement empty?) lists)]
    (cond (empty? lists) lis1
	  (member lis1 lists identical?) '()
	  :else
	  (filter (fn [x]
		    (every #(not (member x % =)) lists))
		  lis1))))

(defn lset-difference! [elt= lis1 & lists]
  nil)

(defn lset-diff+intersection [elt= lis1 & lists]
  (cond (every empty? lists) [lis1 '()]
	(member lis1 lists identical?) ['() lis1]
	:else
	(partition (fn [elt]
		     (not (any #(member elt % =)
			       lists)))
		   lis1)))

(defn lset-diff+intersection! []
  nil)

(defn lset-xor [elt= & lists]
  (reduce (fn [b a]
	    (let [[a-b a-int-b] (lset-diff+intersection = a b)]
	      (cond (empty? a-b) (lset-difference = b a)
		    (empty? a-int-b) (concat b a)
		    :else
		    (fold #(if (member %1 a-int-b =) %2 (cons %1 %2))
			  a-b
			  b))))
	  '() lists))

(defn lset-xor! [elt= & lists]
  nil)
