#!/usr/bin/env bash
#
# Copyright 2022- IBM Inc. All rights reserved
# SPDX-License-Identifier: Apache-2.0
#
#set -euo pipefail
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
cd "${SCRIPT_DIR}"


source ./config.sh
export USE_GEDS_SHUFFLE=1

rerunServices(){
  kubectl delete -f ./sql-go/geds-go.yml
  kubectl delete -f ./sql/geds.yml

  kubectl apply -f ./sql-go/geds-go.yml
  kubectl apply -f ./sql/geds.yml
}

experiments(){
     for BENCHMARK in  sql sql-go sql-go-pubsub; do
       rerunServices
       echo "running:"
       echo ./$BENCHMARK/run_tpcds.sh
       USE_GEDS_SHUFFLE=1 ./$BENCHMARK/run_tpcds.sh
     done
}

experiments

experiments

experiments

experiments

experiments