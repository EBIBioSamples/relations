#!/bin/bash
set -e

#clean any existing content
rm -rf graph.db

#create new content
bin/neo4j-import --bad-tolerance 0 \
	--into graph.db --id-type string \
    --nodes:Sample samples.csv \
    --nodes:Submission submissions.csv \
    --nodes:Group groups.csv \
    --relationships:OWNERSHIP group_ownership.csv \
    --relationships:OWNERSHIP sample_ownership.csv \
    --relationships:MEMBERSHIP sample_membership.csv
    
    
#Indexes are not created during the import. Instead you’ll need to add indexes afterwards
    