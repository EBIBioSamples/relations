#!/bin/bash
set -e

#read environmental config from here...
source `basename $0`/load_env.sh

#clean any existing content
rm -rf $NEO_DATA/graph.db.tmp

#create new content
time nice $NEO4J_BIN/neo4j-import --bad-tolerance 10000 --into $NEO_DATA/graph.db.tmp --i-type string --nodes:Sample "$IMPORTER/csv/sample_header.csv,$IMPORTER/output/sample.csv" --nodes:Group "$IMPORTER/csv/group_header.csv,$IMPORTER/output/group.csv" --relationships:MEMBERSHIP "$IMPORTER/csv/membership_header.csv,$IMPORTER/output/membership.csv" --relationships:DERIVATION "$IMPORTER/csv/derivation_header.csv,$IMPORTER/output/derivation.csv"
#Indexes are not created during the import. Instead you’ll need to add indexes afterwards
       
#create indexes
echo "Creating indexes..."
time nice $NEO4J_BIN/neo4j-shell -path $NEO_DATA/graph.db.tmp -file $IMPORTER/indexes.cypher

#replace graph 
rm -rf $NEO_DATA/graph.db
mv $NEO_DATA/graph.db.tmp $NEO_DATA/graph.db
    
echo "All Done!"
    