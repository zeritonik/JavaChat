#!/bin/bash

build_dir="/home/artem/code/JavaChat/build"
package="JavaChat"
javafx="/home/artem/code/JavaChat/javafx_linux"
fxmodules="javafx.fxml,javafx.controls"
classpath=".:${build_dir}:/home/artem/code/JavaChat/mysql-connector-j-9.0.0.jar"

javac --module-path "${javafx}" --add-modules "${fxmodules}" -d "${build_dir}" *.java
if [ $? -ne 0 ] 
then
    echo "Compilation error"
    exit 1
fi

cp *.fxml "${build_dir}/${package}/"
cd "${build_dir}"
java -cp "${classpath}" --module-path "${javafx}" --add-modules "${fxmodules}" JavaChat.JavaChat