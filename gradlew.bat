@ECHO OFF
REM Gradle wrapper script for Windows
SET DIR=%~dp0
SET APP_VERSION=6.8
SET APP_HOME=%DIR%gradle\bin
SET APP_JAR=%APP_HOME%\gradle-%APP_VERSION%-all.jar
SET APP_TGZ=%APP_HOME%\gradle-%APP_VERSION%-bin.zip
IF NOT EXIST "%APP_JAR%" (
    ECHO "Gradle distribution not found."
    EXIT /B 1
)
java -cp "%APP_JAR%" org.gradle.wrapper.GradleWrapperMain %*
