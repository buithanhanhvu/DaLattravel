using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc.Rendering;

namespace WebDuLichDaLat.Models
{
    public class TripPlannerViewModel
    {
        public decimal Budget { get; set; }
        public List<string> SelectedTouristPlaceIds { get; set; } = new List<string>();
        public int NumberOfDays { get; set; }
        public string TransportType { get; set; }
        public List<TransportOption> TransportOptions { get; set; } = new List<TransportOption>();
        public List<Hotel> Hotels { get; set; } = new List<Hotel>();
        public List<Restaurant> Restaurants { get; set; } = new List<Restaurant>();
        public List<Attraction> Attractions { get; set; } = new List<Attraction>();
        public List<string> Suggestions { get; set; } = new List<string>();
        public List<TouristPlace> TouristPlaces { get; set; } = new List<TouristPlace>();
        public string StartLocation { get; set; }
        public double? StartLatitude { get; set; }
        public double? StartLongitude { get; set; }
        public double DistanceKm { get; set; }
        public List<Category> Categories { get; set; } = new List<Category>();
        public int? SelectedCategoryId { get; set; }
        public int? SelectedTransportId { get; set; }
        public IEnumerable<SelectListItem> TransportSelectList { get; set; } = new List<SelectListItem>();
        
        /// <summary>
        /// Số lượng địa điểm tối đa muốn tham quan (0 = không giới hạn, để hệ thống tự quyết định)
        /// </summary>
        public int? MaxPlaces { get; set; }
    }
}
