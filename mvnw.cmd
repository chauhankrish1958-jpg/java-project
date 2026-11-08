@REM ProTrack Maven Wrapper for Windows
@echo off
setlocal
set DIR=%~dp0
if exist "%DIR%maven\bin\mvn.cmd" (
    "%DIR%maven\bin\mvn.cmd" %*
) else (
    mvn %*
)
endlocal
