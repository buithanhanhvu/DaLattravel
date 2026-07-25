$toDelete = @('.vs', 'DoAnCoSo/.vs', 'DoAnCoSo/WebDuLichDaLat/bin', 'DoAnCoSo/WebDuLichDaLat/obj')
foreach ($dir in $toDelete) {
    if (Test-Path $dir) {
        Remove-Item -Recurse -Force $dir
        Write-Host "Deleted: $dir"
    } else {
        Write-Host "Not found: $dir"
    }
}

# Untrack khoi git neu da bi track
git rm -r --cached .vs 2>$null
git rm -r --cached DoAnCoSo/.vs 2>$null
git rm -r --cached DoAnCoSo/WebDuLichDaLat/bin 2>$null
git rm -r --cached DoAnCoSo/WebDuLichDaLat/obj 2>$null

Write-Host "Done! Gio ban co the chay: git add . && git commit -m 'cleanup' && git push"
