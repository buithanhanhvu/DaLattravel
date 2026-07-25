$nb = Get-Content 'Scripts/script.ipynb' -Raw | ConvertFrom-Json
$sql = ''
foreach ($cell in $nb.cells) {
    if ($cell.cell_type -eq 'code') {
        $sql += ($cell.source -join '') + "`r`n"
    }
}

# Fix 1: Replace CREATE DATABASE block with hardcoded path to simple version
$sql = $sql -replace '(?s)CREATE DATABASE \[WTF\]\r?\n.*?LEDGER = OFF', 'CREATE DATABASE [WTF]'

# Fix 2: Compatibility level 140 for SQL Server 2017
$sql = $sql -replace 'ALTER DATABASE \[WTF\] SET COMPATIBILITY_LEVEL = 160', 'ALTER DATABASE [WTF] SET COMPATIBILITY_LEVEL = 140'

# Fix 3: Remove WAIT_STATS_CAPTURE_MODE (not supported in SQL2017 RTM)
$sql = $sql -replace ',\s*WAIT_STATS_CAPTURE_MODE = ON', ''

# Fix 4: Remove ACCELERATED_DATABASE_RECOVERY (SQL2019+ only)
$sql = $sql -replace 'ALTER DATABASE \[WTF\] SET ACCELERATED_DATABASE_RECOVERY = OFF\s*\r?\nGO\r?\n', ''

$sql | Out-File -FilePath 'Scripts/database_fixed.sql' -Encoding UTF8
Write-Host "Done. Lines: $($sql.Split("`n").Count)"
