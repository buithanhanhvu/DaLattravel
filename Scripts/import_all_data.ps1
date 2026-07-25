# Script to import all TouristPlaces, TransportOptions, VehiclePricingConfigs from script.ipynb into MySQL dalattravel_db

$nb = Get-Content 'Scripts/script.ipynb' -Raw | ConvertFrom-Json
$mysqlCmd = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

$sqlLines = @()
$sqlLines += "USE dalattravel_db;"
$sqlLines += "SET FOREIGN_KEY_CHECKS = 0;"

foreach ($cell in $nb.cells) {
    foreach ($line in $cell.source) {
        if ($line -match "^INSERT\s+\[dbo\]\.\[(\w+)\]") {
            $tableName = $matches[1]
            
            # Map SQL Server table names to MySQL table names
            $mySqlTable = $tableName.ToLower()
            if ($tableName -eq 'TouristPlaces') { $mySqlTable = 'tourist_places' }
            elseif ($tableName -eq 'TransportOptions') { $mySqlTable = 'transport_options' }
            elseif ($tableName -eq 'VehiclePricingConfigs') { $mySqlTable = 'vehicle_pricing_configs' }
            elseif ($tableName -eq 'Hotels') { $mySqlTable = 'hotels' }
            elseif ($tableName -eq 'Restaurants') { $mySqlTable = 'restaurants' }
            elseif ($tableName -eq 'Attractions') { $mySqlTable = 'attractions' }
            elseif ($tableName -eq 'BlogPosts') { $mySqlTable = 'blog_posts' }
            elseif ($tableName -eq 'Festivals') { $mySqlTable = 'festivals' }

            # Replace T-SQL syntax with MySQL syntax
            $converted = $line -replace "INSERT \[dbo\]\.\[$tableName\]", "INSERT INTO `$mySqlTable`"
            $converted = $converted -replace "\[", "`"
            $converted = $converted -replace "\]", "`"
            $converted = $converted -replace "N'", "'"
            $converted = $converted -replace "CAST\(N'([^']+)' AS DateTime2\)", "'$1'"
            $converted = $converted -replace "CAST\(([\d\.]+) AS Decimal\([\d,\s]+\)\)", "$1"

            $sqlLines += $converted
        }
    }
}

$sqlLines += "SET FOREIGN_KEY_CHECKS = 1;"

$tempFile = "Scripts/import_generated.sql"
$sqlLines | Out-File -FilePath $tempFile -Encoding UTF8

Write-Host "Generated SQL script $tempFile with $($sqlLines.Count) statements."

# Execute into MySQL
& $mysqlCmd -u root -p123456 < $tempFile

Write-Host "Successfully imported all data from script.ipynb into MySQL dalattravel_db!"
