#! /bin/bash
./gradlew installDist
./build/install/lambda-calculator/bin/lambda-calculator -qq ./src/dist/std*.lc -vv <(echo "? $@")
