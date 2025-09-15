(ns todo-app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

;; Atom to store the list of todos
(defonce todos (r/atom []))

;; Atom to store the current input
(defonce new-todo (r/atom ""))

(defn add-todo []
  (let [trimmed (clojure.string/trim @new-todo)]
    (when (not (clojure.string/blank? trimmed))
      (swap! todos conj {:id (random-uuid) :text trimmed :done false})
      (reset! new-todo ""))))

(defn toggle-done [id]
  (swap! todos (fn [ts]
                 (mapv (fn [t]
                         (if (= (:id t) id)
                           (update t :done not)
                           t))
                       ts))))

(defn delete-todo [id]
  (swap! todos (fn [ts]
                 (vec (remove #(= (:id %) id) ts)))))

(defn todo-item [{:keys [id text done]}]
  [:li
   {:style {:text-decoration (when done "line-through")}}
   [:input {:type "checkbox"
            :checked done
            :on-change #(toggle-done id)}]
   text
   " "
   [:button {:on-click #(delete-todo id)} "Delete"]])

(defn todo-list []
  [:div
   [:h2 "My To-Do List"]
   [:input {:type "text"
            :value @new-todo
            :on-change #(reset! new-todo (.. % -target -value))
            :placeholder "What needs to be done?"}]
   [:button {:on-click add-todo} "Add"]
   [:ul
    (for [todo @todos]
      ^{:key (:id todo)} [todo-item todo])]])

(defn init []
  (rdom/render [todo-list] (.getElementById js/document "app")))
