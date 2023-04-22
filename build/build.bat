@echo off
echo Building EXE

set LAUNCH4J_PATH="C:\Program Files (x86)\Launch4j\launch4jc.exe"

if exist %LAUNCH4J_PATH% (
%LAUNCH4J_PATH% launch4j.xml
) else (
    echo Launch4j doesn't exist! Change the executable path to a valid installation of Launch4j.
)