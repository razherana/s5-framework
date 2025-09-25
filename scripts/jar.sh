#!/usr/bin/sh

cd /home/razherana/Documents/S5/MrNaina-Framework/framework/ || exit

if [ -d out ]; then
  rm -rf out
fi

mkdir out
find src/main -name "*.java" -print0 | xargs -0 javac -cp lib/* -parameters -d out

cd out || exit
jar cf ../framework.jar .
cd .. || exit
rm -rf out

echo "Jar created at framework/framework.jar"

cp framework.jar ../framework_test/lib

echo "Jar copied to lib"
