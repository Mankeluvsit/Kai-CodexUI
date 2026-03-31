@ECHO OFF
IF EXIST "%~dp0\gradle\wrapper\gradle-wrapper.jar" (
  "%JAVA_HOME%\bin\java.exe" -classpath "%~dp0\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
  EXIT /B %ERRORLEVEL%
)

gradle %*
