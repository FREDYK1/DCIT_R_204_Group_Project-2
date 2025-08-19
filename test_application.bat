@echo off
echo ========================================
echo UG Navigate - Campus Route Finder
echo University of Ghana
echo DCIT 204 - Data Structures and Algorithms 1
echo ========================================
echo.

echo Compiling the application...
javac -cp src -source 1.8 -target 1.8 src/main/utils/*.java src/main/models/*.java src/main/services/*.java src/main/algorithms/pathfinding/*.java src/main/algorithms/sorting/*.java src/main/algorithms/optimization/*.java src/main/gui/*.java src/main/UGNavigateApp.java src/main/demo/AlgorithmDemo.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo Choose an option:
echo 1. Run Algorithm Demo (Console)
echo 2. Run GUI Application
echo 3. Exit
echo.
set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" (
    echo.
    echo Running Algorithm Demo...
    echo.
    java -cp src main.demo.AlgorithmDemo
    echo.
    pause
) else if "%choice%"=="2" (
    echo.
    echo Launching GUI Application...
    echo.
    java -cp src main.UGNavigateApp
) else if "%choice%"=="3" (
    echo Exiting...
    exit /b 0
) else (
    echo Invalid choice!
    pause
    exit /b 1
)
