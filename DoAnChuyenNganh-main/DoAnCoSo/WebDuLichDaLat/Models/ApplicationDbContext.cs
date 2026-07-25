using WebDuLichDaLat.Models;
using WebDuLichDaLat.Data;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace WebDuLichDaLat.Models
{
    /// <summary>
    /// Lop quan ly ngu canh co so du lieu cua ung dung (Entity Framework Core DbContext).
    /// Khai bao cac bang du lieu (DbSet) va thiet lap cac moi quan he giua cac thuc the.
    /// </summary>
    public class ApplicationDbContext : IdentityDbContext<User>
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options) { }

        // Danh muc cac thuc the chinh trong he thong
        public DbSet<Category> Categories { get; set; }
        public DbSet<TouristPlace> TouristPlaces { get; set; }
        public DbSet<Region> Regions { get; set; }
        public DbSet<Favorite> Favorites { get; set; }
        public DbSet<BlogPost> BlogPosts { get; set; }
        public DbSet<Review> Reviews { get; set; }
        public DbSet<Contact> Contacts { get; set; }
        public DbSet<Festival> Festivals { get; set; }

        // Danh muc cac thuc the mo rong (Dich vu va Tin tuc)
        public DbSet<TransportOption> TransportOptions { get; set; }
        public DbSet<Hotel> Hotels { get; set; }
        public DbSet<Restaurant> Restaurants { get; set; }
        public DbSet<Attraction> Attractions { get; set; }
        public DbSet<LegacyLocation> LegacyLocations { get; set; }
        public DbSet<TransportPriceHistory> TransportPriceHistories { get; set; }
        public DbSet<LocalTransport> LocalTransports { get; set; }
        public DbSet<RoutePrice> RoutePrices { get; set; }
        public DbSet<NearbyPlace> NearbyPlaces { get; set; }

        // Danh muc cac thuc the lien quan den tinh nang di chung xe (Carpooling)
        public DbSet<Vehicle> Vehicles { get; set; }
        public DbSet<Passenger> Passengers { get; set; }
        public DbSet<PassengerGroup> PassengerGroups { get; set; }
        public DbSet<PendingCarpoolRequest> PendingCarpoolRequests { get; set; }
        public DbSet<CompletedTrip> CompletedTrips { get; set; }
        public DbSet<CompletedTripPassenger> CompletedTripPassengers { get; set; }
        public DbSet<VehiclePricingConfig> VehiclePricingConfigs { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Khoi tao du lieu mau (Seeding) cho danh muc
            modelBuilder.Entity<Category>().HasData(
                new Category { Id = 1, Name = "Khách sạn" },
                new Category { Id = 2, Name = "Nhà hàng/Quán ăn" },
                new Category { Id = 3, Name = "Địa điểm du lịch" }
            );

            // Cau hinh thuc the Contact
            modelBuilder.Entity<Contact>(entity =>
            {
                entity.Property(c => c.Name).IsRequired().HasMaxLength(100);
                entity.Property(c => c.Email).IsRequired().HasMaxLength(100);
                entity.Property(c => c.Subject).IsRequired().HasMaxLength(150);
                entity.Property(c => c.Message).IsRequired();
            });

            // Thiet lap quan he cho TouristPlace
            modelBuilder.Entity<TouristPlace>()
                .HasOne(p => p.Category)
                .WithMany(c => c.TouristPlaces)
                .HasForeignKey(p => p.CategoryId)
                .OnDelete(DeleteBehavior.SetNull);

            modelBuilder.Entity<TouristPlace>()
                .HasOne(p => p.Region)
                .WithMany(c => c.TouristPlaces)
                .HasForeignKey(p => p.RegionId)
                .OnDelete(DeleteBehavior.SetNull);

            modelBuilder.Entity<TransportPriceHistory>()
              .HasOne(p => p.TransportOption)
              .WithMany(t => t.PriceHistories)
              .HasForeignKey(p => p.TransportOptionId);

            modelBuilder.Entity<TransportPriceHistory>()
                .HasOne(p => p.LegacyLocation)
                .WithMany(l => l.PriceHistories)
                .HasForeignKey(p => p.LegacyLocationId);
            
            // Luu y: Du lieu Seed cho Regions va Locations duoc thuc hien thong qua 
            // cac file migration rieng biet hoac script SQL de dam bao tinh nhat quan.
            
            // Toi uu hoa truy van thong qua viec thiet lap Index
            modelBuilder.Entity<LegacyLocation>()
                .HasIndex(l => new { l.Latitude, l.Longitude })
                .HasDatabaseName("IX_LegacyLocation_GPS");
            
            modelBuilder.Entity<LegacyLocation>()
                .HasIndex(l => l.IsActive)
                .HasDatabaseName("IX_LegacyLocation_IsActive");
            
            modelBuilder.Entity<LegacyLocation>()
                .HasIndex(l => l.IsMergedLocation)
                .HasDatabaseName("IX_LegacyLocation_IsMerged");
            
            modelBuilder.Entity<TransportPriceHistory>()
                .HasIndex(p => new { p.LegacyLocationId, p.TransportOptionId })
                .HasDatabaseName("IX_TransportPrice_LocationTransport")
                .IsUnique();

            // Thiet lap quan he cho tinh nang Carpooling
            modelBuilder.Entity<Passenger>()
                .HasOne(p => p.Group)
                .WithMany(g => g.Passengers)
                .HasForeignKey(p => p.GroupId)
                .OnDelete(DeleteBehavior.SetNull);

            modelBuilder.Entity<PendingCarpoolRequest>()
                .HasOne(p => p.Group)
                .WithMany()
                .HasForeignKey(p => p.GroupId)
                .OnDelete(DeleteBehavior.SetNull);

            modelBuilder.Entity<CompletedTripPassenger>()
                .HasOne(p => p.CompletedTrip)
                .WithMany(t => t.Passengers)
                .HasForeignKey(p => p.CompletedTripId)
                .OnDelete(DeleteBehavior.Cascade);
        }

    }
}
