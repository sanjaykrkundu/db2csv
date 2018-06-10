echo off
For /F "tokens=1* delims==" %%A IN (input/config.properties) DO (
    IF "%%A"=="JAVA_PATH" set javaPath=%%B
)
@echo "javaPath" %javaPath%
PATH=%javaPath%

java -jar db2csv.jar
pause