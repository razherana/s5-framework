#!/bin/bash

# Définir les répertoire
DIR="$(basename "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)")"
PROJECT_NAME=${1:-$(cat .projectname 2>/dev/null || $DIR)}
TEMP_DIR="build"
SOURCE_FOLDER="src"
CLASSES_FOLDER="src/main/webapp/WEB-INF/classes"
LIB_FOLDER="lib"
TOMCAT_DIR=/home/razherana/Documents/apache-tomcat-10.1.34
JAVAC_ARGS="-parameters --release 17"

printf "\033[34m[INFO]\033[0m -> Building %s...\n" "$PROJECT_NAME"

rm -rf $CLASSES_FOLDER
mkdir -p $CLASSES_FOLDER

if [ -d "$TEMP_DIR" ]; then
    printf "\033[34m[INFO]\033[0m -> Removing temp directory : %s\n" "$TEMP_DIR"
    rm -r "$TEMP_DIR"
fi

mkdir -p "$TEMP_DIR"

mkdir -p "$CLASSES_FOLDER"

printf "\033[34m[INFO]\033[0m -> Compiling and copying classes...\n"

find "$SOURCE_FOLDER" -name '*.java' -print0 | xargs -0 javac $JAVAC_ARGS -d "$CLASSES_FOLDER" -cp .:"$LIB_FOLDER/*"

printf "\033[34m[INFO]\033[0m -> Copying webapp files...\n"

cp -r "src/main/webapp/." "$TEMP_DIR"

printf "\033[34m[INFO]\033[0m -> Copying lib files...\n"

cp -r "$LIB_FOLDER" "$TEMP_DIR/WEB-INF/"

printf "\033[34m[INFO]\033[0m -> Bootstraping all and making war file...\n"

jar -cvf "$PROJECT_NAME".war -C "$TEMP_DIR" .

printf "\033[34m[INFO]\033[0m -> Cleaning up...\n"

rm -r $TEMP_DIR

printf "\033[34m[INFO]\033[0m -> Copying to tomcat webapp folder...\n"

cp "$PROJECT_NAME".war "$TOMCAT_DIR"/webapps/

printf "\033[32m[SUCCESS]\033[0m -> Done.\n"
