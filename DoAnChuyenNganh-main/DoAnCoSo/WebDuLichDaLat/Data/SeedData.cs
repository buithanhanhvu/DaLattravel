using Microsoft.EntityFrameworkCore;
using WebDuLichDaLat.Models;

namespace WebDuLichDaLat.Data
{
    /// <summary>
    /// Class để seed dữ liệu ban đầu cho database
    /// </summary>
    public static class SeedData
    {
        /// <summary>
        /// Khoi tao du lieu cho cac tinh thanh, bao gom ca cac tinh da sap nhap theo quy dinh moi.
        /// </summary>
        public static void SeedLegacyLocations(ModelBuilder modelBuilder)
        {
            var locations = new List<LegacyLocation>
            {
                // Danh sach cac dia phương da thuc hien sap nhap hanh chinh
                
                // 1. Long An (cu) sap nhap vao Tay Ninh (moi)
                new LegacyLocation
                {
                    Id = 1,
                    OldName = "Long An",
                    CurrentName = "Tây Ninh",
                    Latitude = 10.5368,
                    Longitude = 106.4149,
                    IsMergedLocation = true,
                    MergeDate = new DateTime(2025, 7, 1),
                    MergeNote = "Long An sáp nhập vào Tây Ninh từ 01/07/2025",
                    IsActive = true
                },
                
                // 2. Dak Nong (cu) sap nhap vao Lam Dong (moi)
                new LegacyLocation
                {
                    Id = 2,
                    OldName = "Đắk Nông",
                    CurrentName = "Lâm Đồng",
                    Latitude = 12.2646,
                    Longitude = 107.6098,
                    IsMergedLocation = true,
                    MergeDate = new DateTime(2025, 7, 1),
                    MergeNote = "Đắk Nông sáp nhập vào Lâm Đồng từ 01/07/2025",
                    IsActive = true
                },
                
                // 3. Binh Thuan (cu) sap nhap vao Lam Dong (moi)
                new LegacyLocation
                {
                    Id = 3,
                    OldName = "Bình Thuận",
                    CurrentName = "Lâm Đồng",
                    Latitude = 11.0904,
                    Longitude = 108.0721,
                    IsMergedLocation = true,
                    MergeDate = new DateTime(2025, 7, 1),
                    MergeNote = "Bình Thuận sáp nhập vào Lâm Đồng từ 01/07/2025",
                    IsActive = true
                },
                
                // Danh sach cac tinh thanh giu nguyen dia gioi hanh chinh
                
                // 4. Tay Ninh (Nguyen trang)
                new LegacyLocation
                {
                    Id = 4,
                    OldName = "Tây Ninh",
                    CurrentName = "Tây Ninh",
                    Latitude = 11.3100,
                    Longitude = 106.0983,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 5. Thanh pho Ho Chi Minh
                new LegacyLocation
                {
                    Id = 5,
                    OldName = "TP. Hồ Chí Minh",
                    CurrentName = "TP. Hồ Chí Minh",
                    Latitude = 10.7769,
                    Longitude = 106.7009,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 6. Dong Nai
                new LegacyLocation
                {
                    Id = 6,
                    OldName = "Đồng Nai",
                    CurrentName = "Đồng Nai",
                    Latitude = 10.9472,
                    Longitude = 106.8446,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 7. Ba Ria - Vung Tau
                new LegacyLocation
                {
                    Id = 7,
                    OldName = "Bà Rịa - Vũng Tàu",
                    CurrentName = "Bà Rịa - Vũng Tàu",
                    Latitude = 10.5417,
                    Longitude = 107.2429,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 8. Khanh Hoa
                new LegacyLocation
                {
                    Id = 8,
                    OldName = "Khánh Hòa",
                    CurrentName = "Khánh Hòa",
                    Latitude = 12.2388,
                    Longitude = 109.1967,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 9. Ninh Thuan
                new LegacyLocation
                {
                    Id = 9,
                    OldName = "Ninh Thuận",
                    CurrentName = "Ninh Thuận",
                    Latitude = 11.6739,
                    Longitude = 108.8629,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 10. Binh Phuoc
                new LegacyLocation
                {
                    Id = 10,
                    OldName = "Bình Phước",
                    CurrentName = "Bình Phước",
                    Latitude = 11.7511,
                    Longitude = 106.7234,
                    IsMergedLocation = false,
                    IsActive = true
                },
                
                // 11. Lam Dong (Hien tai)
                new LegacyLocation
                {
                    Id = 11,
                    OldName = "Lâm Đồng",
                    CurrentName = "Lâm Đồng",
                    Latitude = 11.9404,
                    Longitude = 108.4583,
                    IsMergedLocation = false,
                    IsActive = true
                }
            };
            
            modelBuilder.Entity<LegacyLocation>().HasData(locations);
        }
        
        /// <summary>
        /// Khoi tao du lieu lich su gia van chuyen cho cac tuyen duong va loai hinh van tai.
        /// </summary>
        public static void SeedTransportPriceHistory(ModelBuilder modelBuilder)
        {
            var prices = new List<TransportPriceHistory>
            {
                // Du lieu gia cho loai hinh Xe Khach (TransportOptionId = 1)
                
                new TransportPriceHistory { Id = 1, LegacyLocationId = 1, TransportOptionId = 1, Price = 350000 },
                new TransportPriceHistory { Id = 2, LegacyLocationId = 2, TransportOptionId = 1, Price = 180000 },
                new TransportPriceHistory { Id = 3, LegacyLocationId = 3, TransportOptionId = 1, Price = 200000 },
                new TransportPriceHistory { Id = 4, LegacyLocationId = 4, TransportOptionId = 1, Price = 400000 },
                new TransportPriceHistory { Id = 5, LegacyLocationId = 5, TransportOptionId = 1, Price = 300000 },
                new TransportPriceHistory { Id = 6, LegacyLocationId = 6, TransportOptionId = 1, Price = 280000 },
                new TransportPriceHistory { Id = 7, LegacyLocationId = 7, TransportOptionId = 1, Price = 320000 },
                new TransportPriceHistory { Id = 8, LegacyLocationId = 8, TransportOptionId = 1, Price = 200000 },
                new TransportPriceHistory { Id = 9, LegacyLocationId = 9, TransportOptionId = 1, Price = 180000 },
                new TransportPriceHistory { Id = 10, LegacyLocationId = 10, TransportOptionId = 1, Price = 250000 },
                
                // Du lieu gia cho loai hinh Xe Limousine (TransportOptionId = 2)
                
                new TransportPriceHistory { Id = 11, LegacyLocationId = 1, TransportOptionId = 2, Price = 600000 },
                new TransportPriceHistory { Id = 12, LegacyLocationId = 5, TransportOptionId = 2, Price = 550000 },
                new TransportPriceHistory { Id = 13, LegacyLocationId = 4, TransportOptionId = 2, Price = 700000 },
                
                // Cac tuyen noi tinh tai Lam Dong
                new TransportPriceHistory { Id = 14, LegacyLocationId = 11, TransportOptionId = 1, Price = 50000 }
            };
            
            modelBuilder.Entity<TransportPriceHistory>().HasData(prices);
        }


        /// <summary>
        /// Seed dữ liệu Regions (Khu vực địa lý trong Đà Lạt)
        /// </summary>
        public static void SeedRegions(ModelBuilder modelBuilder)
        {
            var regions = new List<Region>
            {
                new Region { Id = 1, Name = "Trung tâm thành phố" },
                new Region { Id = 2, Name = "Langbiang" },
                new Region { Id = 3, Name = "Hồ Tuyền Lâm" },
                new Region { Id = 4, Name = "Xã Tà Nung" },
                new Region { Id = 5, Name = "Hồ Xuân Hương" },
                new Region { Id = 6, Name = "Đồi Cù" },
                new Region { Id = 7, Name = "Thung lũng Tình Yêu" },
                new Region { Id = 8, Name = "Cam Ly" },
                new Region { Id = 9, Name = "Trại Mát" },
                new Region { Id = 10, Name = "Cầu Đất" },
                new Region { Id = 11, Name = "D'ran" },
                new Region { Id = 12, Name = "Lạc Dương" },
                new Region { Id = 13, Name = "Đức Trọng" },
                new Region { Id = 14, Name = "Đơn Dương" },
                new Region { Id = 15, Name = "Đam Rông" },
                new Region { Id = 16, Name = "Ngoại thành" }
            };

            modelBuilder.Entity<Region>().HasData(regions);
        }

        /// <summary>
        /// Seed dữ liệu Regions trực tiếp vào database (dùng khi database đã tồn tại)
        /// </summary>
        public static async Task SeedRegionsAsync(ApplicationDbContext context)
        {
            // Kiểm tra xem đã có dữ liệu chưa
            if (await context.Regions.AnyAsync())
            {
                return; // Đã có dữ liệu, không seed lại
            }

            var regions = new List<Region>
            {
                new Region { Id = 1, Name = "Trung tâm thành phố" },
                new Region { Id = 2, Name = "Langbiang" },
                new Region { Id = 3, Name = "Hồ Tuyền Lâm" },
                new Region { Id = 4, Name = "Xã Tà Nung" },
                new Region { Id = 5, Name = "Hồ Xuân Hương" },
                new Region { Id = 6, Name = "Đồi Cù" },
                new Region { Id = 7, Name = "Thung lũng Tình Yêu" },
                new Region { Id = 8, Name = "Cam Ly" },
                new Region { Id = 9, Name = "Trại Mát" },
                new Region { Id = 10, Name = "Cầu Đất" },
                new Region { Id = 11, Name = "D'ran" },
                new Region { Id = 12, Name = "Lạc Dương" },
                new Region { Id = 13, Name = "Đức Trọng" },
                new Region { Id = 14, Name = "Đơn Dương" },
                new Region { Id = 15, Name = "Đam Rông" },
                new Region { Id = 16, Name = "Ngoại thành" }
            };

            await context.Regions.AddRangeAsync(regions);
            await context.SaveChangesAsync();
        }
    }
}

