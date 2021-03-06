(ns sudoku
  (:require [clojure.set :as set]))

(def board identity)

(def all-values #{1 2 3 4 5 6 7 8 9})

(defn value-at [board coord]
  (get-in board coord))


;(value-at sudoku-board [0 1]) ;=> 3
;(value-at sudoku-board [0 0]) ;=> 5

(defn has-value? [board coord]
  (not= 0 (value-at board coord)))

;(has-value? sudoku-board [0 0]) ;=> true
;(has-value? sudoku-board [0 2]) ;=> false

(defn row-values [board coord]
  (set (get board (first coord))))

;(row-values sudoku-board [0 2]) ;=> #{0 5 3 7}
;(row-values sudoku-board [3 2]) ;=> #{0 8 6 3}

(defn col-values [board coord]
  (let [[row col] coord]
    (set (map (fn [x] (value-at board [x col])) (range 9)))))

;(col-values sudoku-board [0 2]) ;=> #{0 8}
;(col-values sudoku-board [4 8]) ;=> #{3 1 6 0 5 9}

(defn coord-pairs [coords]
  (for [fv coords
        sv coords]
    [fv sv]))

;(coord-pairs [0 1])
;(coord-pairs [0 1 2]) ;=> [[0 0] [0 1] [0 2]
                      ;    [1 0] [1 1] [1 2]
                      ;    [2 0] [2 1] [2 2]]

(defn block-coord [row col]
  (let [rval (* (quot row 3) 3)
        cval (* (quot col 3) 3)]
    (for [r (range rval (+ rval 3))
          c (range cval (+ cval 3))]
      [r c])))

;(block-coord 8 8)

(defn block-values [board coord]
  (let [[row col] coord
        lst (block-coord row col)]
    (set (map (fn [x] (value-at board x)) lst))))

; (block-values sudoku-board [0 2]) ;=> #{0 5 3 6 8 9}
; (block-values sudoku-board [4 5]) ;=> #{0 6 8 3 2}

(defn valid-values-for [board coord]
  (if (not= 0 (value-at board coord))
      #{}
      (let [rval (row-values board coord)
            cval (col-values board coord)
            bval (block-values board coord)]
        (set/difference all-values (set/union rval cval bval)))))

;(valid-values-for sudoku-board [0 0]) ;=> #{}
;(valid-values-for sudoku-board [0 2]) ;=> #{1 2 4})

(defn filled? [board]
  (not= 0 (some #{0} (flatten board))))

;(filled? solved-board)


(defn rows [board]
  (map (fn [r] (row-values board [r 0])) (range 9)))

;(rows sudoku-board)
;(rows solved-board)

(defn valid-rows? [board]
  (every? (fn [x] (= x all-values)) (rows board)))

;(valid-rows? solved-board)

(defn cols [board]
  (map (fn [c] (col-values board [0 c])) (range 9)))

;(cols sudoku-board)
;(cols solved-board)

(defn valid-cols? [board]
  (every? (fn [x] (= x all-values)) (cols board)))

;(valid-cols? solved-board)
;(coord-pairs [0 3 6])

(defn blocks [board]
  (map (fn [bval] (block-values board bval)) (coord-pairs [0 3 6])))

(defn valid-blocks? [board]
  (every? (fn [x] (= x all-values)) (blocks board)))

;(blocks sudoku-board)
;(blocks solved-board)
;(valid-blocks? solved-board)

(defn valid-solution? [board]
  (and (valid-rows? board)
       (valid-cols? board)
       (valid-blocks? board)))

;(valid-solution? solved-board)

(defn set-value-at [board coord new-value]
   (assoc-in board coord new-value))


;(= after-change (set-value-at before-change [2 1] 4))

(coord-pairs (range 9))
(defn find-empty-point [board]
  (first (filter (fn [x] (not (has-value? board x))) (coord-pairs (range 9)))))

;(find-empty-point sudoku-board)
;(find-empty-point solved-board)

(defn solve [board]
  (if (filled? board)
    (if (valid-solution? board) board [])
    (let [coord (find-empty-point board)
          solutions (lazy-seq (map (fn [x] (solve (set-value-at board coord x))) (valid-values-for board coord)))] ;; Can we have laziness here like Haskell ?
      (first (filter (fn [sol] (not (empty? sol))) solutions)))))


;(solve sudoku-board)
