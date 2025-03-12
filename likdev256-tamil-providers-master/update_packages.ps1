# PowerShell script to update package names and references
$oldPackage = "com.likdev256"
$newPackage = "com.narendray1"
$oldRepo = "LikDev-256/likdev256-tamil-providers"
$newRepo = "NarendraY1/tamil-providers"

# Update package names in Kotlin files
Get-ChildItem -Recurse -Filter "*.kt" | ForEach-Object {
    (Get-Content $_.FullName) | ForEach-Object {
        $_ -replace "package $oldPackage", "package $newPackage"
    } | Set-Content $_.FullName
}

# Update package names in AndroidManifest.xml files
Get-ChildItem -Recurse -Filter "AndroidManifest.xml" | ForEach-Object {
    (Get-Content $_.FullName) | ForEach-Object {
        $_ -replace "package=`"$oldPackage`"", "package=`"$newPackage`""
    } | Set-Content $_.FullName
}

# Update repository URLs in build.gradle.kts files
Get-ChildItem -Recurse -Filter "build.gradle.kts" | ForEach-Object {
    (Get-Content $_.FullName) | ForEach-Object {
        $_ -replace "https://raw.githubusercontent.com/$oldRepo/master", "https://raw.githubusercontent.com/$newRepo/master" `
           -replace "https://github.com/$oldRepo", "https://github.com/$newRepo" `
           -replace "authors = listOf\(`"LikDev-256`"\)", "authors = listOf(`"NarendraY1`", `"LikDev-256`")" `
           -replace "authors = listOf\(`"darkdemon, likdev256`"\)", "authors = listOf(`"NarendraY1`", `"darkdemon`", `"LikDev-256`")"
    } | Set-Content $_.FullName
}

Write-Host "Package names and references updated successfully!" 