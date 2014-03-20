(ns stch.html.helpers.form
  "Generate HTML form elements."
  (:use [stch.html :only [option select
                          input label]])
  (:require [cljs.core :as core]))

(defn- indexed
  "Returns a lazy sequence of [index, item] pairs,
  where items come from s and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map-indexed vector s))

(defn ->options
  "Converts a sequence of value/display pairs to option
  elements. Optionally, provide the selected option."
  ([options]
   (->options options nil))
  ([options selected]
   (for [[value display] options
         :let [attrs {:value value}]]
     (option (if (= value selected)
               (assoc attrs :selected "selected")
               attrs)
             display))))

(defn ->select
  "Returns an HTML select element given an element
  name, a sequence of value/display pairs, an optional map
  of attributes (e.g., name, class), and an optional selected
  value."
  ([name options]
   (->select name options nil))
  ([name options selected]
   (select {:name (core/name name)}
           (->options options selected))))

(defn ->radio
  "Returns HTML radio elements given an element
  name, a sequence of value/display pairs, an optional
  map of attributes (e.g., name, class), and an optional
  checked value."
  ([name values]
   (->radio name values nil))
  ([name values checked]
   (for [[index [value display]]
         (indexed values)
         :let [name (core/name name)
               id (str name "-" index)
               input-attrs {:type "radio"
                            :name name
                            :value (core/name value)
                            :id id}]]
     [(input
       (if (= value checked)
         (assoc input-attrs :checked "checked")
         input-attrs))
      (label {:class "form-radio-label"
              :for id}
             display)])))

(defn ->checkbox
  "Returns HTML checkbox elements given an element
  name, a sequence of value/display pairs, an optional
  map of attributes (e.g., name, class), and an optional
  checked value."
  ([name values]
   (->checkbox name values nil))
  ([name values checked]
   (for [[index [value display]]
         (indexed values)
         :let [name (core/name name)
               id (str name "-" index)
               input-attrs {:type "checkbox"
                            :name name
                            :value (core/name value)
                            :id id}]]
     [(input
       (if (= value checked)
         (assoc input-attrs :checked "checked")
         input-attrs))
      (label {:class "form-checkbox-label"
              :for id}
             display)])))

(defn capture-image
  [n]
  (input {:type "file"
          :name n
          :accept "image/*"
          :capture "capture"}))

(defn capture-video
  [n]
  (input {:type "file"
          :name n
          :accept "video/*"
          :capture "capture"}))

(defn capture-audio
  [n]
  (input {:type "file"
          :name n
          :accept "audio/*"
          :capture "capture"}))





