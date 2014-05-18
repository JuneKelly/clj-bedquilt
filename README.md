# bedquilt

A JSON document store on top of PostgreSQL, using Clojure.

## Usage

Lets presume we have a PostgreSQL 9.3 installation, with a database
`bedquilt_test`, created as:
```sql
CREATE DATABASE bedquilt_test
  WITH OWNER = some_user
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'C'
       LC_CTYPE = 'C'
       CONNECTION LIMIT = -1;
```


Import the `bedquilt.core` namespace:
```clojure
(require '[bedquilt.core :as bq])
```

Get a connection to a database:
```clojure
(def db (bq/get-db {:db-host "localhost"
                    :db-name "bedquilt_test"
                    :user "some_user"
                    :password "password"}))
```

Insert some documents:
```clojure
(bq/save! db :people {:name "John Doe", :age 27})
; => "Xj0VokRuU05wSLzBrMyfvQ"

(bq/save! db :people {:name "Jane Roe", :age 31, :likes ["baseball", "music"]})
; => "7FhRRcD0D5253Mm6woyN5Q"
```
New collections (tables) are automatically created on first write.

Ids are automatically generated, unless the document includes an `_id` field:
```clojure
(bq/save! db :people {:_id "mike.blow@example.com" :name "Mike Blow", :age 53})
; => "mike.blow@example.com"
```

Query for existing documents by their `_id` field:
```clojure
(bq/find-one db :people "mike.blow@example.com")
; => {:_id "mike.blow@example.com", :name "Mike Blow", :age 53}
```


Delete existig documents:
```clojure
(bq/delete! db :people "mike.blow@example.com")
; => true
```

## Warnings
- This is alpha-quality work right now, don't use in production
- The performance is pretty bad, I'll figure out how to make in not suck later


## License

Copyright © 2014 Shane Kilkelly

MIT License, do whatever you want.