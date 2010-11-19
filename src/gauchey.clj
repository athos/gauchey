(ns gauchey)

;; definitions of several utilities

(defmacro defn* [fn-name args & body]
  (let [[args rest-args] (split-with #(not= % :allow-other-keys) args)
	[args keys] (split-with #(not= % :key) args)
	[args optionals] (split-with #(not= % :optional) args)
	rest-args (if (empty? rest-args) nil (second rest-args))
	key-kvs (if (empty? keys) nil (rest keys))
	opt-kvs (if (empty? optionals) nil (rest optionals))
	g-opts (gensym)
	g-rest (gensym)
	g-kvs (gensym)]
    `(defn ~fn-name [~@args & ~g-opts]
       (let [~@(and opt-kvs
		    `([~@(for [[key val] opt-kvs] key) & ~g-rest] ~g-opts
		      ~@(mapcat (fn [[key val]]
				  `(~key (or ~key ~val)))
				opt-kvs)))
	     ~@(and key-kvs
		    `(~g-kvs (apply hash-map ~(if opt-kvs g-rest g-opts))
		      ~@(mapcat (fn [[key val]]
				  (if (coll? key)
				    `(~(second key) (or (~g-kvs ~(first key)) ~val))
				    `(~key (or (~g-kvs ~(keyword key)) ~val))))
				key-kvs)))
	     ~@(and rest-args
		    `(~rest-args (dissoc ~(if key-kvs
					    g-kvs
					    `(apply hash-map
						    ~(if opt-kvs g-rest g-opts)))
					 ~@(for [[key _] key-kvs]
					     (if (coll? key)
					       (first key)
					       (keyword key))))))]
	 ~@body))))
