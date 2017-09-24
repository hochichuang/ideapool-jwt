#!/bin/bash

echo "generating 55 ideas..."

for i in {1..55}
do
    curl -H "x-access-token: $_token" -H "Content-Type: application/json" -d'{"content":"seq:'"$i"'","impact":5,"ease":6,"confidence":'"$(($i % 10 + 1))"'}' localhost:8080/ideas
done
