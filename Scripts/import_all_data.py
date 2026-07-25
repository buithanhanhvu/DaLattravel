import json
import re
import subprocess

def main():
    with open('Scripts/script.ipynb', 'r', encoding='utf-8') as f:
        nb = json.load(f)

    sql_statements = ["USE dalattravel_db;", "SET FOREIGN_KEY_CHECKS = 0;"]

    table_map = {
        'TouristPlaces': 'tourist_places',
        'TransportOptions': 'transport_options',
        'VehiclePricingConfigs': 'vehicle_pricing_configs',
        'Hotels': 'hotels',
        'Restaurants': 'restaurants',
        'Attractions': 'attractions',
        'BlogPosts': 'blog_posts',
        'Festivals': 'festivals'
    }

    count = 0
    for cell in nb.get('cells', []):
        for line in cell.get('source', []):
            line_str = "".join(line) if isinstance(line, list) else line
            line_str = line_str.strip()
            if line_str.startswith('INSERT'):
                m = re.search(r'INSERT\s+\[dbo\]\.\[(\w+)\]\s*\((.*?)\)\s*VALUES\s*\((.*?)\);?', line_str, re.IGNORECASE)
                if m:
                    table_name, cols_str, vals_str = m.group(1), m.group(2), m.group(3)
                    mysql_table = table_map.get(table_name, table_name.lower())

                    # Clean column names
                    cols = [c.strip('[] ') for c in cols_str.split(',')]
                    
                    # Map column names (e.g. CamelCase to snake_case)
                    clean_cols = []
                    for c in cols:
                        if c == 'Id': clean_cols.append('id')
                        elif c == 'Name': clean_cols.append('name')
                        elif c == 'RegionId': clean_cols.append('region_id')
                        elif c == 'ImageUrl': clean_cols.append('image_url')
                        elif c == 'ImageUrls': clean_cols.append('image_urls')
                        elif c == 'Description': clean_cols.append('description')
                        elif c == 'CategoryId': clean_cols.append('category_id')
                        elif c == 'Latitude': clean_cols.append('latitude')
                        elif c == 'Longitude': clean_cols.append('longitude')
                        elif c == 'ReviewContent': clean_cols.append('review_content')
                        elif c == 'Rating': clean_cols.append('rating')
                        elif c == 'Price': clean_cols.append('price')
                        elif c == 'FixedPrice': clean_cols.append('base_price')
                        elif c == 'IsSelfDrive': clean_cols.append('is_self_drive')
                        elif c == 'FuelConsumption': clean_cols.append('fuel_consumption')
                        elif c == 'FuelPrice': clean_cols.append('fuel_price')
                        elif c == 'TouristPlaceId': clean_cols.append('tourist_place_id')
                        elif c == 'SeatCapacity': clean_cols.append('seat_capacity')
                        elif c == 'VehicleTypeName': clean_cols.append('vehicle_type_name')
                        elif c == 'FuelPricePerKm': clean_cols.append('fuel_price_per_km')
                        elif c == 'DriverSalaryPerTrip': clean_cols.append('driver_salary_per_trip')
                        elif c == 'TollFee': clean_cols.append('toll_fee')
                        elif c == 'ProfitMargin': clean_cols.append('profit_margin')
                        elif c == 'MinimumTripCost': clean_cols.append('minimum_trip_cost')
                        elif c == 'IsActive': clean_cols.append('is_active')
                        elif c == 'CreatedAt': clean_cols.append('created_at')
                        elif c == 'Address': clean_cols.append('address')
                        elif c == 'Phone': clean_cols.append('phone')
                        elif c == 'PricePerNight': clean_cols.append('price_per_night')
                        elif c == 'AveragePricePerPerson': clean_cols.append('average_price_per_person')
                        elif c == 'Title': clean_cols.append('title')
                        elif c == 'Content': clean_cols.append('content')
                        elif c == 'Author': clean_cols.append('author')
                        else:
                            # Convert CamelCase to snake_case
                            clean_cols.append(re.sub(r'(?<!^)(?=[A-Z])', '_', c).lower())

                    # Clean values
                    vals = vals_str.replace("N'", "'")
                    vals = re.sub(r'CAST\(N?\'([^\']+)\'\s+AS\s+DateTime2\)', r"'\1'", vals, flags=re.IGNORECASE)
                    vals = re.sub(r'CAST\(([\d\.]+)\s+AS\s+Decimal\([\d,\s]+\)\)', r"\1", vals, flags=re.IGNORECASE)

                    cols_join = ", ".join([f"`{c}`" for c in clean_cols])
                    stmt = f"INSERT INTO `{mysql_table}` ({cols_join}) VALUES ({vals});"
                    sql_statements.append(stmt)
                    count += 1

    sql_statements.append("SET FOREIGN_KEY_CHECKS = 1;")

    output_path = "Scripts/import_script_generated.sql"
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write("\n".join(sql_statements))

    print(f"Generated {count} MySQL INSERT statements in {output_path}.")

    # Run command in mysql
    cmd = ['C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe', '-u', 'root', '-p123456', 'dalattravel_db']
    with open(output_path, 'r', encoding='utf-8') as f:
        p = subprocess.run(cmd, stdin=f, capture_output=True, text=True)
        if p.returncode == 0:
            print("Successfully imported all data from Scripts/script.ipynb into MySQL dalattravel_db!")
        else:
            print(f"MySQL import error: {p.stderr}")

if __name__ == '__main__':
    main()
