#!/bin/bash
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
cd $SHELL_FOLDER
git add *
git commit -m '自动提交' 
git push origin v0.0.1

