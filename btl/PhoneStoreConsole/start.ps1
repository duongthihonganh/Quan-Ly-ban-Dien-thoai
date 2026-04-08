[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
$JAR = "C:\Users\Uyen Nhi\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar"
$CLASSES = "target\classes"
$MAIN = "com.phonestore.Main"

# Bien dich
Write-Host "Dang bien dich..." -ForegroundColor Yellow
$files = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
$files | Out-File -FilePath "sources.txt" -Encoding ASCII
& javac --release 21 -encoding UTF-8 -cp $JAR -d $CLASSES `@sources.txt

if ($LASTEXITCODE -eq 0) {
    Write-Host "Bien dich thanh cong! Dang chay..." -ForegroundColor Green
    java.exe "-Dfile.encoding=UTF-8" -cp "$CLASSES;$JAR" $MAIN
} else {
    Write-Host "Bien dich that bai! Kiem tra lai code." -ForegroundColor Red
}
