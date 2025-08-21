@echo off
echo Compiling UG Navigate Application...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile the main application file with explicit sourcepath
echo Compiling UGNavigateApp.java with source path...
javac -d bin -sourcepath src src\main\UGNavigateApp.java

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
