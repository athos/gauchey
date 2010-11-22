(ns gauchey.gauche.process
  (:use [gauchey :only (defn*)]
	[clojure.contrib.io :only (with-in-reader with-out-writer read-lines)]
	[clojure.string :rename {replace str-replace, reverse _reverse}]))

(gen-class
  :name gauchey.gauche.process.ProcessAbnormalExitException
  :extends java.lang.RuntimeException)

(defrecord GProcess [command status process])

(defn make-process [& args]
  (let [kvs (apply hash-map args)]
    (GProcess. (:command kvs) (atom nil) (:process kvs))))

(defn process-pid [proc]
  nil)

(defn process-command [proc]
  (:command proc))

(defn process-exit-status [proc]
  @(:status proc))

(defn process-input [proc]
  (.getInputStream (:process proc)))

(defn process-output [proc]
  (.getOutputStream (:process proc)))

(defn process-error [proc]
  (.getErrorStream (:process proc)))

(defn- prepare-remote [host argv dir]
  (let [[_ proto user server port]
	(re-matches #"^(?:([\w-]+):)?(?:([\w-]+)@)?([\w._]+)(?::(\d+))?" host)]
    (if (or (not proto) (= proto "ssh"))
      nil ; FIXME
      `("ssh"
	~@(and user `("-l" ~user))
	~@(and port `("-p" ~port))
	~server
	~@(and dir `("cd" ~dir ";"))
	~@argv))))

(defn* run-process [command :key (input nil) (output nil) (error nil) (wait false)
		    (fork true) (host nil) (sigmask nil) ((:directory dir) nil)]
  (let [comm (map str command)
	argv (if host (prepare-remote host comm directory) comm)
	builder (java.lang.ProcessBuilder. argv)
	dir (if host nil directory)
	proc (.start builder)]
    (if fork
      (let [process (make-process :command (first comm) :process proc)]
	(when wait
	  (reset! (:status process) (.waitFor proc)))
	process)
      (let [ret (.waitFor proc)]
	(System/exit ret)))))

(defn process? [x]
  (= (class x) GProcess))

(defn process-alive? [^GProcess proc]
  (try
    (not (.exitValue (.process proc)))
    (catch IllegalThreadStateException e
      true)))

(defn* process-wait [process :optional (nohang? false) (raise-error? false)]
  (if (process-alive? process)
    (when (not nohang?)
      (let [ret (.waitFor (:process process))]
	(reset! (:status process) ret)
	(when raise-error? nil)
	true))
    (when (nil? (process-exit-status process))
      (reset! (:status process) (.exitValue (:process process)))
      true)))

(defn process-wait-any
  ([]
   (process-wait-any false))
  ([nohang?]
   (process-wait-any nohang? false))
  ([nohang? raise-error?]
   nil))

(defn process-send-signal []
  nil)

(defn process-kill []
  nil)

(defn process-stop []
  nil)

(defn process-continue []
  nil)

(defn process-list []
  nil)

(defn- apply-run-process [command stdin stdout stderr host]
  (apply run-process command
	             :input stdin
		     :output stdout
		     :host host
		     (if (string? stderr)
		       `(:error ~stderr)
		       '())))

(defn- check-normal-exit [process]
  (when-not (= (process-exit-status process) 0)
    (throw (gauchey.gauche.process.ProcessAbnormalExitException.
	    (format "%s exited abnormally with exit code %d" (str process) (:status 0))))))

(defn- handle-abnormal-exit [on-abnormal-exit process]
  (case on-abnormal-exit
    :error  (check-normal-exit process)
    :ignore nil
            (when-not (process-exit-status process)
	      (on-abnormal-exit process))))

(defn* open-input-process-port [command :key (input nil) (error nil) (host nil)
				:allow-other-keys rest]
  (let [p (apply-run-process command input :pipe error host)]
    [(process-input p) p]))

(defn* open-output-process-port [command :key (output nil) (error nil) (host nil)]
  (let [p (apply-run-process command :pipe output error host)]
    [(process-output p) p]))

(defn* call-with-input-process [command proc :optional (input nil) (error nil) (host nil)
				(on-abnormal-exit :error)
				:allow-other-keys rest]
  (let [p (apply-run-process command input :pipe error host)
        ret (with-open [i (process-input p)]
	      (proc i))]
    (process-wait p)
    (handle-abnormal-exit on-abnormal-exit p)
    ret))

(defn* call-with-output-process [command proc :optional (output nil) (error nil) (host nil)
				 (on-abnormal-exit :error)]
  (let [p (apply-run-process command :pipe output error host)
	ret (with-open [o (process-output p)]
	      (proc o))]
    (process-wait p)
    (handle-abnormal-exit on-abnormal-exit p)
    ret))

(defn with-input-from-process [command thunk & opts]
  (apply call-with-input-process command
	 #(with-in-reader % (thunk))
	 opts))

(defn with-output-to-process [command thunk & opts]
  (apply call-with-output-process command
	 #(with-out-writer % (thunk))
	 opts))

(defn* call-with-process-io [command proc :optional (error nil) (host nil)
			     (on-abnormal-exit :error)]
  (let [p (apply-run-process command :pipe :pipe error host)
	ret (with-open [i (process-input p), o (process-output p)]
	      (proc i o))]
    (process-wait p)
    (handle-abnormal-exit on-abnormal-exit p)
    ret))

(defn process-output->string [command & opts]
  (apply call-with-input-process command
	 #(str-replace (slurp %) #"[ ]+" " ")
	 opts))

(defn process-output->string-list [command & opts]
  (apply call-with-input-process command read-lines opts))

(defn shell-escape-string [s]
  (cond (= s "") "''"
	(re-find #"[\s\\\"'*?$<>!\[\](){}]" s)
	(str "'" (str-replace s #"'" "'\"'\"'") "'")
	:else s))
