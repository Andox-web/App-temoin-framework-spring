@echo off
setlocal enabledelayedexpansion

REM Set variables
set PROJECT_NAME=Ticketing
set SRC_DIR=src\main\java
set LIB_DIR=lib
set WEB_DIR=web
set BUILD_DIR=build
set WEB_INF_CLASSES=%BUILD_DIR%\WEB-INF\classes
set WEB_INF_LIB=%BUILD_DIR%\WEB-INF\lib
set TOMCAT_HOME=E:\logiciel\tomcat
set WAR_NAME=%PROJECT_NAME%.war

REM Clean previous build
echo Cleaning previous build...
if exist %BUILD_DIR% rd /s /q %BUILD_DIR%

REM Create necessary directories
echo Creating directories...
mkdir %BUILD_DIR%
robocopy %WEB_DIR% %BUILD_DIR% /s /e

mkdir %WEB_INF_CLASSES%
mkdir %WEB_INF_LIB%

REM Find all Java files
echo Finding Java files...
dir /S /B %SRC_DIR%\*.java > %BUILD_DIR%\sources.txt

REM Check if sources.txt is empty and delete it if it is
for %%i in (%BUILD_DIR%\sources.txt) do if %%~zi==0 del %BUILD_DIR%\sources.txt

REM Compile Java files if any are found
if exist %BUILD_DIR%\sources.txt (
    echo Compiling Java files...
    javac -d %WEB_INF_CLASSES% -cp "%LIB_DIR%\*" @%BUILD_DIR%\sources.txt > %BUILD_DIR%\compile.log 2>&1
    if errorlevel 1 (
        echo Compilation failed. See compile.log for details.
        type %BUILD_DIR%\compile.log
        exit /b 1
    )
) else (
    echo No Java files found to compile.
)

REM Copy non-Java files and empty directories to WEB-INF\classes
echo Copying non-Java files and empty directories...
robocopy %SRC_DIR% %WEB_INF_CLASSES% /s /e /xf *.java

REM Copy libraries
echo Copying libraries...
robocopy %LIB_DIR% %WEB_INF_LIB% /s /e

REM Package into WAR file
echo Packaging into WAR file...
cd %BUILD_DIR%
jar -cvf %WAR_NAME% *
cd ..

REM Deploy to Tomcat
echo Deploying to Tomcat...
xcopy %BUILD_DIR%\%WAR_NAME% %TOMCAT_HOME%\webapps\ /Y

echo Build and deployment completed successfully.

endlocal
