using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebDuLichDaLat.Models
{
    public class Review
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string TouristPlaceId { get; set; }

        [ForeignKey("TouristPlaceId")]
        public TouristPlace TouristPlace { get; set; }

        [Range(1, 5)]
        public int Rating { get; set; }

        public string? Comment { get; set; }

        public DateTime CreatedAt { get; set; } = DateTime.Now;
    }
}
