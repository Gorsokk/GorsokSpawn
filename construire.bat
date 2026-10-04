@echo off
cd /d "%~dp0"
echo Construction du plugin GorsokSpawn...
call gradlew.bat build --no-daemon > build-log.txt 2>&1
if errorlevel 1 (echo ECHEC >> build-log.txt) else (echo SUCCES >> build-log.txt)
java -version >> build-log.txt 2>&1
dir build\libs >> build-log.txt 2>&1
echo Termine. Vous pouvez fermer cette fenetre.
timeout /t 10
