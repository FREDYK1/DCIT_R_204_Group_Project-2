@echo off
echo Compiling UG Navigate Application...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Use dir /s /b to get all .java files recursively
echo Finding all Java files...
dir /s /b src\main\*.java > sources.txt

:: Compile all Java files at once so dependencies are resolved properly
echo Compiling Java files...
javac -d bin @sources.txt

if %errorlevel% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    echo To run the application, use: java -cp bin main.UGNavigateApp
    echo.
) else (
    echo.
    echo Compilation failed! See errors above.
    echo.
)
