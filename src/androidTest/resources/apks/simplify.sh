#!/bin/bash

workdir=`pwd`
re='(.*?)/(.*?)\.apk'

for file in */*.apk; do
    if [[ $file =~ $re ]]; then
        basename="${BASH_REMATCH[2]}"
        tempdir="${BASH_REMATCH[1]}/${BASH_REMATCH[2]}"

        unzip "$file" -d "$tempdir"
        cd "$tempdir"
          rm -rf META-INF assets lib r resources.arsc
          zip -9 "${basename}.apk" * && mv "${basename}.apk" ../
        cd "$workdir"
          rm -rf "$tempdir"
    fi
done
