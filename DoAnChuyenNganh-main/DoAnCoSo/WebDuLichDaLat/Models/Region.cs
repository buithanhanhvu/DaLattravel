using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebDuLichDaLat.Models
{
    public class Region
    {
        [Column("RegionId")]
        public int Id { get; set; }

        [Column("RegionName")]
        [Required, StringLength(50)]
        public string Name { get; set; } = string.Empty;


        public ICollection<TouristPlace> TouristPlaces { get; set; } = new List<TouristPlace>();
    }

}
