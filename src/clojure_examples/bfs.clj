(ns clojure-examples.bfs
  "Demonstrates Breadth-first search graph algorithm.

   Check:
   - Clojure for Data Science book
   - https://github.com/Engelberg/ubergraph
   - https://github.com/aysylu/loom")

(defn- bfs-step
  "Does one bfs step on given graph.
     to-explore set contains all nodes that should be explored
     visited set contains all nodes that have already been visited
     parent-map contains child-parent relationships."
  [graph-adj-list to-explore visited parent-map]
  (let [neighbors
        (->> to-explore
             (map (fn [vertex] (graph-adj-list vertex)))
             (filter (fn [neighbor] (not (visited neighbor)))))
]

    )
  )


(defn bfs
  "Do the breadth-first search on graph represented as an adjacency list.
   Returns the path or nil if no path has been found."
  [graph-adj-list start goal]
  (if (= start goal)
    []
    (bfs-step graph-adj-list #{start} #{start} {}))
  )


;;; Examples
(bfs {:v1 [:v2 :v3]
      :v3 [:v4]
      :v4 [:v1 :v5 :v6 :v7]
      :v6 [:v7]}
     :v1 :v4)
