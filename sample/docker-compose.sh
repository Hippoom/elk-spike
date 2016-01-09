#!/usr/bin/env bash -ex

PWD="$( cd "$( dirname "${BASH_SOURCE[0]}" )/" && pwd )"

export WORKSPACE=$PWD

arg1=$1

docker-compose $arg1
