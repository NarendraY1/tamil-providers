# PowerShell script to update AndroidManifest.xml files
Get-ChildItem -Recurse -Filter "AndroidManifest.xml" | ForEach-Object {
    $content = Get-Content $_.FullName
    $updatedContent = $content -replace 'package="[^"]+"', ''
    Set-Content -Path $_.FullName -Value $updatedContent
    Write-Host "Updated $($_.FullName)"
}

Write-Host "All AndroidManifest.xml files updated successfully!" 