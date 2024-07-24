runner_dir="$(echo $0 | sed 's%/[^/]*$%%')";
cd "${runner_dir}"

connector="mysql-connector-j-9.0.0.jar"
javafx_dir="javafx_$1"
javafx_modules="javafx.fxml,javafx.controls"

source_dir="JavaChat"
build_dir="Release"

module_path="${javafx_dir}"
modules="${javafx_modules}"

javac --module-path "${module_path}" --add-modules "${modules}" -d "${build_dir}" "${source_dir}"/*.java
cp "${source_dir}/"*.fxml "${build_dir}/JavaChat"
tar -czvf "JavaChat_$1.tar" "${connector}" "${javafx_dir}" "${build_dir}" "runJavaChat.bash"