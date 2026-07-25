using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebDuLichDaLat.Models
{
    public class TouristPlace
    {
        [Required(ErrorMessage = "Mã địa điểm không được để trống")]
        [StringLength(6, MinimumLength = 6, ErrorMessage = "Phải có đúng 6 kí tự.")]
        public required string Id { get; set; }

        [Required(ErrorMessage = "Tên địa điểm không được để trống")]
        [StringLength(50, ErrorMessage = "Tên địa điểm không được quá 50 kí tự")]
        public required string Name { get; set; }

        public int? RegionId { get; set; }
        public virtual Region? Region { get; set; }

        public string? ImageUrl { get; set; }

        public List<string>? ImageUrls { get; set; }

        public string? Description { get; set; }
        public virtual Category? Category { get; set; }
        public int? CategoryId { get; set; }

        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string? ReviewContent { get; set; }
        public int Rating { get; set; } // Từ 1 đến 5
        public ICollection<Review> Reviews { get; set; } = new List<Review>();
        public ICollection<Hotel> Hotels { get; set; } = new List<Hotel>();
        public ICollection<Restaurant> Restaurants { get; set; } = new List<Restaurant>();
        public ICollection<TransportOption> TransportOptions { get; set; } = new List<TransportOption>();


    }
}
