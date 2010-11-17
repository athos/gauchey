(ns gauchey.gauche.process
  (:use [clojure.contrib.def :only (defnk)]
	[clojure.contrib.io :only (with-in-reader with-out-writer)]
	[gauchey.gauche :only (sys-pipe)]))

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

(defnk run-process [command :input nil :output nil :error nil
		            :wait false :fork true
		            :host nil :sigmask nil :directory nil]
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

(defn process-wait
  ([process]
   (process-wait process false))
  ([process nohang?]
   (process-wait process nohang? false))
  ([process nohang? raise-error?]
   (if (process-alive? process)
     (when (not nohang?)
       (let [ret (.waitFor (:process process))]
	 (reset! (:status process) ret)
	 (when raise-error? nil)
	 true))
     (when (nil? (process-exit-status process))
       (reset! (:status process) (.exitValue (:process process)))
       true))))

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

(defnk open-input-process-port [command :input nil :error nil :host nil]
  (let [p (apply-run-process command input :pipe error host)]
    [(process-input p) p]))

(defnk open-output-process-port [command :output nil :error nil :host nil]
  (let [p (apply-run-process command :pipe output error host)]
    [(process-output p) p]))

(defnk call-with-input-process [command proc :input nil :error nil :host nil
				:on-abnormal-exit :error]
  (let [p (apply-run-process command input :pipe error host)
        ret (with-open [i (process-input p)]
	      (proc i))]
    (process-wait p)
    (handle-abnormal-exit on-abnormal-exit p)
    ret))

(defnk call-with-output-process [command proc :output nil :error nil :host nil
				 :on-abnormal-exit :error]
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

(defnk call-with-process-io [command proc :error nil :host nil
			    :on-abnormal-exit :error]
  (let [p (apply-run-process command :pipe :pipe error host)
	ret (with-open [i (process-input p), o (process-output p)]
	      (proc i o))]
    (process-wait p)
    (handle-abnormal-exit on-abnormal-exit p)
    ret))

(defn process-output->string []
  nil)

(defn process-output->string-list []
  nil)

(defn shell-escape-string []
  nil)
