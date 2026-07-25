using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebDuLichDaLat.Models
{
    public class Hotel
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [StringLength(150)]
        public string Name { get; set; }

        [StringLength(300)]
        public string Address { get; set; }

        [StringLength(15)]
        public string Phone { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public decimal PricePerNight { get; set; }
        [Column(TypeName = "float")]
        public double Latitude { get; set; }

        [Column(TypeName = "float")]
        public double Longitude { get; set; }

        
        [StringLength(6)] 
        public string? TouristPlaceId { get; set; }
        public TouristPlace? TouristPlace { get; set; }
    }
}