$json = Get-Content 'Scripts/script.ipynb' -Raw | ConvertFrom-Json
$sql = @("USE dalattravel_db;", "SET FOREIGN_KEY_CHECKS = 0;")

foreach ($cell in $json.cells) {
    foreach ($line in $cell.source) {
        if ($line -like "*INSERT*") {
            $l = $line.Trim()
            if ($l.StartsWith("INSERT [dbo].[TouristPlaces]")) {
                $l = $l.Replace("INSERT [dbo].[TouristPlaces] ([Id], [Name], [RegionId], [ImageUrl], [ImageUrls], [Description], [CategoryId], [Latitude], [Longitude], [ReviewContent], [Rating])", "REPLACE INTO tourist_places (id, name, region_id, image_url, description, category_id, latitude, longitude, review_content, rating)")
                $l = $l -replace ", NULL, NULL, ", ", NULL, "
            } elseif ($l.StartsWith("INSERT [dbo].[TransportOptions]")) {
                $l = $l.Replace("INSERT [dbo].[TransportOptions] ([Id], [Name], [Type], [Price], [FixedPrice], [IsSelfDrive], [FuelConsumption], [FuelPrice], [TouristPlaceId])", "REPLACE INTO transport_options (id, name, type, base_price, is_self_drive, fuel_consumption, fuel_price)")
                $l = $l -replace ", NULL\)", ")"
                $l = $l -replace "VALUES \((\d+),\s*N'([^']+)',\s*N'([^']+)',\s*CAST\(([\d\.]+)\s+AS\s+Decimal\(18,\s*2\)\),\s*CAST\(([\d\.]+)\s+AS\s+Decimal\(18,\s*2\)\),", "VALUES (`$1, '$2', '$3', `$5,"
            } elseif ($l.StartsWith("INSERT [dbo].[VehiclePricingConfigs]")) {
                $l = $l.Replace("INSERT [dbo].[VehiclePricingConfigs] ([Id], [SeatCapacity], [VehicleTypeName], [FuelPricePerKm], [DriverSalaryPerTrip], [TollFee], [ProfitMargin], [MinimumTripCost], [IsActive], [CreatedAt])", "REPLACE INTO vehicle_pricing_configs (id, seat_capacity, vehicle_type_name, fuel_price_per_km, driver_salary_per_trip, toll_fee, profit_margin, minimum_trip_cost, active, created_at)")
            } else {
                continue
            }

            $l = $l.Replace("N'", "'")
            $l = $l.Replace("CAST(N'", "'")
            $l = $l.Replace("CAST(", "")
            $l = $l.Replace(" AS Decimal(18, 2))", "")
            $l = $l.Replace(" AS Decimal(5, 2))", "")
            $l = $l.Replace(" AS DateTime2)", "")

            if (-not $l.EndsWith(";")) {
                $l += ";"
            }

            $sql += $l
        }
    }
}

$sql += "SET FOREIGN_KEY_CHECKS = 1;"
$sql | Out-File -FilePath 'Scripts/import_clean.sql' -Encoding UTF8
Write-Host "Generated Scripts/import_clean.sql matching schema. Statements: $($sql.Count)"
