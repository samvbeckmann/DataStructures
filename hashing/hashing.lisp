;;; ******************************************
;;;            Project 3 -  Hashing
;;;            Author: Sam Beckmann
;;; ******************************************

(defun driver-function ()
	"Runs the program with specified parameters from project guidelines"
	(evaluate-instance 1019 10000 .6 #'mod nil)
	(evaluate-instance 1019 10000 .8 #'mod nil)
	(evaluate-instance 1019 10000 .6 #'mod t)
	(evaluate-instance 1019 10000 .8 #'mod t))

(defun evaluate-instance (size max-value load-factor hash-func quad)
	"Evaulates and prints the results of a instance of hash-table stats."
	(print-results (construct-table size max-value load-factor hash-func quad) max-value hash-func quad load-factor))

(defun construct-table (size range load-factor hash-func quad)
	"Constructs a hash table with the given parameters"
	(fill-array (make-array (list size)) range (round (* size load-factor)) hash-func quad))

(defun fill-array (array range num-fill hash-func quad)
	"Fills an array with num-fill items in the specified range"
	(dotimes (x num-fill array)
		(fill-one array range (1- (random range)) hash-func quad)))

(defun fill-one (array range key hash-func quad)
	"Inerts key into the array, if not already in it. Else insert new number to array"
	(if (first (search-table array key hash-func quad))
		(fill-one array range (1- (random range)) hash-func quad)
		(setf (aref array (third (search-table array key hash-func quad))) key)))

(defun search-table (array key hash-func quad &optional (num-searches 1))
	"Searches a hash table for a given value, returning if the value was found in the table"
	(test-search-value array key hash-func quad num-searches (get-probe-value key (array-total-size array) hash-func num-searches quad)))

(defun test-search-value (array key hash-func quad num-searches probe-value)
	"Checks if a given key is located at a given position in the table"
	(cond
		((null (aref array probe-value)) (list nil num-searches probe-value))
		((not (= key (aref array probe-value))) (search-table array key hash-func quad (1+ num-searches)))
		((= key (aref array probe-value)) (list t num-searches probe-value))))

(defun get-probe-value (key size hash-func num-searches quad)
	"Finds a probe value from the key, size of the hash table, hashing function, depth of search, and probing technique."
	(if quad
		(if (oddp num-searches)
			(mod (- (funcall hash-func key size) (expt (floor (/ num-searches 2)) 2)) size)
			(mod (+ (funcall hash-func key size) (expt (floor (/ num-searches 2)) 2)) size))
		(mod (+ (funcall hash-func key size) (1- num-searches)) size)))

(defun get-avg-search-times (table range hash-func quad &aux (success ()) (fail ()))
	"Gets the average successful and unsuccessful search times for every possible value in a hash table"
	(dotimes (x range)
		(let ((search-result (search-table table (1+ x) hash-func quad)))
			(if (first search-result)
				(push (second search-result) success)
				(push (second search-result) fail))))
	(list (average success) (average fail)))

(defun average (collection &aux (total 0))
	"Gets the average value of a list of numbers."
	(dolist (x collection (/ total (list-length collection)))
		(incf total x)))

(defun print-results (array range hash-func quad load-factor)
	"Prints the results from an instance"
	(print-hashing-stats array (get-avg-search-times array range hash-func quad) quad load-factor))

(defun print-hashing-stats (array results quad load-factor)
	"Prints the results form taking statistics on a hashing set"
		(format t "~%Hash table with ~a Hashing and Load factor of ~d:
	Avg. Successful search: ~d
	Avg. Unsuccessful search: ~d~%" (if quad "Quadradic" "Linear") load-factor (float (first results)) (float (second results))))
