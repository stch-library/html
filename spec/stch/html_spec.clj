(ns stch.html-spec
  (:use [speclj.core]
        [stch.html]
        [stch.html.form]
        [stch.schema :only [with-fn-validation]]
        [stch.util :only [with-private-fns]]))

(describe "stch.html internals"
  (around [it]
    (with-fn-validation (it)))
  (context "escape"
    (it "script tag"
      (should= "&lt;script&gt;location=&quot;mysite.org&quot;&lt;/script&gt;"
               (escape "<script>location=\"mysite.org\"</script>")))
    (it "single quote"
      (should= "&#39;"
               (escape "'")))
    (it "ampersand"
      (should= "&amp;"
               (escape "&"))))
  (with-private-fns [stch.html [self-closing-tags]]
    (it "self-closing-tags"
      (should= "br"
               (self-closing-tags "br"))))
  (with-private-fns [stch.html [first-keyword?]]
    (it "first-keyword?"
      (should= true
               (first-keyword? [:class "bold"]))))
  (with-private-fns [stch.html [split-args]]
    (it "split-args"
      (should= ['((:class "bold") (:id "1234"))
                `((~(br) nil))]
               (split-args `(:class "bold" :id "1234" ~(br))))))
  (with-private-fns [stch.html [flatten-children]]
    (it "flatten-children"
      (should= [(div) (div)]
               (flatten-children `((~(div) ~(div)))))))
  (with-private-fns [stch.html [parse-args]]
    (context "parse-args"
      (it "Element"
        (should= [{} [(div) (div)]]
                 (parse-args (list (div) (div)))))
      (it "keyword pairs"
        (should= [{:class "bold" :id "1234"} [(br)]]
                 (parse-args `(:class "bold" :id "1234" ~(br)))))
      (it "map of attributes"
        (should= [{:class "bold" :id "1234"} [(br)]]
                 (parse-args `({:class "bold" :id "1234"} ~(br)))))
      (it "empty"
        (should= [{} '()]
                 (parse-args '())))))
  (with-private-fns [stch.html [combine-attrs]]
    (context "combine-attrs"
      (it "class"
        (should= {:class "big bad bold"}
                 (combine-attrs
                   {:class "big bold"}
                   {:class "bold bad"})))
      (it "mixed"
        (should= {:class "product", :id "A1234", :data-id "product-A1234"}
                 (combine-attrs
                   {:id "A1234"
                    :class "product"}
                   {:data-id "product-A1234"})))))
  (with-private-fns [stch.html [classes->set]]
    (context "classes->set"
      (it "String"
        (should= #{"big" "bad" "bold"}
                 (classes->set "big bad bold")))
      (it "Set"
        (should= #{"big" "bad" "bold"}
                 (classes->set #{"big" "bad" "bold"})))
      (it "Sequential"
        (should= #{"big" "bad" "bold"}
                 (classes->set ["big" "bad" "bold"])))))
  (context "class"
    (it "String"
      (should= "<div class=\"big bold\"></div>"
               (->html (div :class "big bold"))))
    (it "Set"
      (should= "<div class=\"big bold\"></div>"
               (->html (div :class #{"big" "bold"}))))
    (it "Sequential"
      (should= "<div class=\"big bold\"></div>"
               (->html (div :class ["big" "bold"])))))
  (with-private-fns [stch.html [extend-elem]]
    (context "extend-elem"
      (it "empty"
        (should= (div)
                 (extend-elem (div) '())))
      (it "class"
        (should= (div :class "big bold")
                 (extend-elem (div :class "big") '(:class "bold"))))
      (it "children"
        (should= (ul (li "Billy")
                     (li "Bobby")
                     (li "Tommy"))
                 (extend-elem (ul (li "Billy")
                                  (li "Bobby"))
                              (list (li "Tommy")))))))
  (with-private-fns [stch.html [attrs->str]]
    (it "attrs->str"
      (should= " class=\"bold\" id=\"A1234\""
               (attrs->str {:class "bold"
                            :id "A1234"}))))
  (with-private-fns [stch.html [elem->str]]
    (context "elem->str"
      (it "element"
        (should= "<div></div>"
                 (elem->str "div" "" "")))
      (it "attributes"
        (should= "<div id=\"A1234\"></div>"
                 (elem->str "div" " id=\"A1234\"" "")))
      (it "children"
        (should= "<ul><li></li></ul>"
                 (elem->str "ul" "" "<li></li>")))))
  (it "raw"
    (should= (->RawHTML "<div></div>")
             (raw "<div></div>")))
  (context "IHTMLBuilder"
    (it "Element"
      (should= "<ul id=\"names\"><li>Billy</li><li>Bobby</li></ul>"
               (-html (ul :id "names"
                          (li "Billy")
                          (li "Bobby")))))
    (it "String"
      (should= "&lt;div&gt;&lt;/div&gt;"
               (-html "<div></div>")))
    (it "Sequential"
      (should= "<div></div><div></div>"
               (-html [(div) (div)])))
    (it "RawHTML"
      (should= "<div></div>"
               (-html (raw "<div></div>"))))
    (context "Doctype"
      (it "html5"
        (should= "<!DOCTYPE html>"
                 (-html (html5))))
      (it "html4-strict"
        (should= "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\">"
                 (-html (html4-strict))))
      (it "html4-trans"
        (should= "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\">"
                 (-html (html4-trans)))))
    (context "Object"
      (it "Number"
        (should= "5" (-html 5)))
      (it "Boolean"
        (should= "true" (-html true))))))

(defhtml main [users]
  (html5)
  (html
    (head
      (title "My Page"))
    (body
      (section
        (header
          (h1 "Users"))
        (ul :id "users"
          (for [x users]
            (li x))))
      (footer "Copyright 2014"))))

(deffrag user [id first-name]
  (div :id id first-name))

(describe "stch.html"
  (around [it]
    (with-fn-validation (it)))
  (context "->html"
    (it "one arg"
        (should= "<div></div>"
                 (->html (div))))
    (it "two args"
        (should= "<div></div><div></div>"
                 (->html (div) (div))))
    (it "three args"
        (should= "<div></div><div></div><div></div>"
                 (->html (div) (div) (div)))))
  (it "defhtml"
    (should= "<!DOCTYPE html><html><head><title>My Page</title></head><body><section><header><h1>Users</h1></header><ul id=\"users\"><li>Billy</li><li>Bobby</li></ul></section><footer>Copyright 2014</footer></body></html>"
             (main ["Billy" "Bobby"])))
  (it "deffrag"
    (should= "<div id=\"1234\">Billy</div>"
             (->html (user 1234 "Billy"))))
  (context "element fns"
    (it "a"
      (should= "<a href=\"/users\">Users</a>"
               (->html (a :href "/users" "Users"))))
    (it "b,i"
      (should= "<b>bold</b><i>italic</i>"
               (->html (b "bold") (i "italic"))))
    (it "html,head,title,body"
      (should= "<html><head><title>My Page</title></head><body>Content</body></html>"
               (->html (html (head (title "My Page"))
                             (body "Content")))))
    (it "form,label,input,textarea,button"
      (should= "<form method=\"post\"><label>First Name:</label><input type=\"text\" value=\"Billy\"><textarea></textarea><button type=\"submit\">Submit</button></form>"
               (->html
                 (form :method "post"
                   (label "First Name:")
                   (input :type "text" :value "Billy")
                   (textarea)
                   (button :type "submit" "Submit")))))
    (it "h1,h2,h3,h4,h5,h6"
      (should= "<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>"
               (->html (h1) (h2) (h3) (h4) (h5) (h6))))
    (it "table,thead,tbody,tr,td"
      (should= "<table><thead><tr><td>Name</td><td>Age</td></tr></thead><tbody><tr><td>Billy</td><td>35</td></tr></tbody></table>"
               (->html (table
                         (thead
                           (tr (td "Name")
                               (td "Age")))
                         (tbody
                           (tr (td "Billy")
                               (td 35)))))))
    (it "ul,li"
      (should= "<ul><li>Clojure</li><li>ClojureScript</li></ul>"
               (->html (ul (li "Clojure")
                           (li "ClojureScript")))))
    (it "ol,li"
      (should= "<ol><li>Clojure</li><li>ClojureScript</li></ol>"
               (->html (ol (li "Clojure")
                           (li "ClojureScript")))))
    (it "style"
      (should= "<style type=\"text/css\">.name{font-weight: bold;}</style>"
               (->html (style :type "text/css"
                         ".name{font-weight: bold;}"))))
    (it "script"
      (should= "<script type=\"text/javascript\">alert(\"hello\");</script>"
               (->html (script :type "text/javascript"
                         (raw "alert(\"hello\");")))))
    (it "span"
      (should= "<span></span>"
               (->html (span))))))

(def states [["CA" "California"]
             ["FL" "Florida"]])

(describe "stch.html.form"
  (around [it]
    (with-fn-validation (it)))
  (context "->options"
    (it "one arg"
      (should= (list (option :value "CA" "California")
                     (option :value "FL" "Florida"))
               (->options states)))
    (it "two args"
      (should= (list (option :value "CA" :selected "selected"
                       "California")
                     (option :value "FL" "Florida"))
               (->options states "CA"))))
  (context "->select"
    (it "two args"
      (should= (select :name "states"
                 (option :value "CA" "California")
                 (option :value "FL" "Florida"))
               (->select "states" states)))
    (it "three args"
      (should= (select :name "states"
                 (option :value "CA" :selected "selected"
                   "California")
                 (option :value "FL" "Florida"))
               (->select "states" states "CA"))))
  (context "->radio"
    (it "two args"
      (should= (list [(input :type "radio" :name "states" :value "CA" :id "states-0")
                      (label :class "form-radio-label" :for "states-0" "California")]
                     [(input :type "radio" :name "states" :value "FL" :id "states-1")
                      (label :class "form-radio-label" :for "states-1" "Florida")])
               (->radio "states" states)))
    (it "three args"
      (should= (list [(input :checked "checked" :type "radio" :name "states" :value "CA" :id "states-0")
                      (label :class "form-radio-label" :for "states-0" "California")]
                     [(input :type "radio" :name "states" :value "FL" :id "states-1")
                      (label :class "form-radio-label" :for "states-1" "Florida")])
               (->radio "states" states "CA"))))
  (context "->checkbox"
    (it "two args"
      (should= (list [(input :type "checkbox" :name "states" :value "CA" :id "states-0")
                      (label :class "form-checkbox-label" :for "states-0" "California")]
                     [(input :type "checkbox" :name "states" :value "FL" :id "states-1")
                      (label :class "form-checkbox-label" :for "states-1" "Florida")])
               (->checkbox "states" states)))
    (it "three args"
      (should= (list [(input :checked "checked" :type "checkbox" :name "states" :value "CA" :id "states-0")
                      (label :class "form-checkbox-label" :for "states-0" "California")]
                     [(input :type "checkbox" :name "states" :value "FL" :id "states-1")
                      (label :class "form-checkbox-label" :for "states-1" "Florida")])
               (->checkbox "states" states "CA")))))























