#!/usr/bin/env bash
sh ./build.sh
adb install -r bin/app-debug.apk
adb shell am start -W com.sososeen09.multidexbuild/.MainActivity