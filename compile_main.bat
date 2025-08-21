@echo off
echo Compiling UG Navigate Application...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile the main application file, which should pull in dependencies
echo Compiling UGNavigateApp.java...
javac -d bin src\main\UGNavigateApp.java

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
