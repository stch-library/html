(ns stch.html.macros
  "HTML macros for ClojureScript.")

(defmacro defhtml
  "Define a function that returns an HTML string."
  [name bindings & body]
  `(defn ~name ~bindings
     (stch.html/->html ~@body)))

(defmacro deffrag
  "Define a function that returns a list of Elements."
  [name bindings & body]
  `(defn ~name ~bindings
     (list ~@body)))
