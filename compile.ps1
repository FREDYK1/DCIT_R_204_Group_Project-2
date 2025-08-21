Write-Host "Compiling UG Navigate Application..." -ForegroundColor Green

# Create bin directory if it doesn't exist
if (-not (Test-Path -Path "bin")) {
    New-Item -Path "bin" -ItemType Directory | Out-Null
}

# Find all Java files
Write-Host "Finding all Java files..." -ForegroundColor Cyan
$javaFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# Convert to a space-separated string for javac
$javaFilesStr = $javaFiles -join " "

# Compile all Java files
Write-Host "Compiling Java files..." -ForegroundColor Cyan
$compileCommand = "javac -d bin $javaFilesStr"

# Execute the compile command
$compileOutput = Invoke-Expression -Command $compileCommand

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nCompilation successful!" -ForegroundColor Green
    Write-Host "`nTo run the application, use: java -cp bin main.UGNavigateApp" -ForegroundColor Yellow
} else {
    Write-Host "`nCompilation failed! See errors above." -ForegroundColor Red
    Write-Host $compileOutput
}
