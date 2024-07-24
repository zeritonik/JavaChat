#!/bin/bash

# sed

runner_dir="$(echo $0 | sed 's%/[^/]*$%%')";
cd "${runner_dir}"

connector="./mysql-connector-j-9.0.0.jar"
javafx_dir="./javafx_$1"
javafx_modules="javafx.fxml,javafx.controls"

classpath="./Release:${connector}:"
module_path="${javafx_dir}"
modules="${javafx_modules}"

echo \"java -cp "${classpath}" --module-path "${module_path}" --add-modules "${modules}" JavaChat.JavaChat\"
java -cp "${classpath}" --module-path "${module_path}" --add-modules "${modules}" JavaChat.JavaChat