#!/bin/bash
re='(.*?)/com\.tencent\.mm_(.*?)_.*?\.apk'
for file in */*.apk; do
    if [[ $file =~ $re ]]; then
        mv "$file" "${BASH_REMATCH[1]}/wechat-v${BASH_REMATCH[2]}.apk"
    fi
done
