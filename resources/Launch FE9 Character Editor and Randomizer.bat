@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0
"%DIR%\bin\java" %JLINK_VM_OPTIONS% -m FE9Randomizer/contents.Main %*
