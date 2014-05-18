# bedquilt

A JSON document store on top of PostgreSQL, using Clojure.

## Usage


Import the `bedquilt.core` namespace:
```
(require '[bedquilt.core :as bq])
```

Get a connection to a database:
```
(def db (bq/get-db {:db-host "localhost"
                    :db-name "bq_test"
                    :user "username"
                    :password "password"}))
```

Insert some documents:
```
(bq/save! db :people {:name "John Doe", :age 27})
; => "Xj0VokRuU05wSLzBrMyfvQ"

(bq/save! db :people {:name "Jane Roe", :age 31, :likes ["baseball", "music"]})
; => "7FhRRcD0D5253Mm6woyN5Q"
```
New collections (tables) are automatically created on first write.

Ids are automatically generated, unless the document includes an `_id` field:
```
(bq/save! db :people {:_id "mike.blow@example.com" :name "Mike Blow", :age 53})
; => "mike.blow@example.com"
```

Query for existing documents by their `_id` field:
```
(bq/find-one db :people "mike.blow@example.com")
; => {:_id "mike.blow@example.com", :name "Mike Blow", :age 53}
```


Delete existig documents:
```
(bq/delete! db :people "mike.blow@example.com")
; => true
```

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
