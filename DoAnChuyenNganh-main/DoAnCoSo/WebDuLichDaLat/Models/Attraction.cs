using System.ComponentModel.DataAnnotations;

namespace WebDuLichDaLat.Models
{
    public class Attraction
    {
        [Required]
        [StringLength(6)]
        public string TouristPlaceId { get; set; }
        public TouristPlace TouristPlace { get; set; }
        public int Id { get; set; }
        public string Name { get; set; }
        public decimal TicketPrice { get; set; }
    }
} 