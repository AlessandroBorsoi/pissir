#!/usr/bin/env bash

export CSV_DIRECTORY="/path/to/the/open/pflow/csv/folder" # default /tmp/data
gradle :integration-tests:test --tests it.uniupo.disit.pissir.it.HeavyLoadingTest
