using WebDuLichDaLat.Areas.Admin.Controllers.Repositories;
using WebDuLichDaLat.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace WebDuLichDaLat.Controllers
{
    /// <summary>
    /// Controller quan ly cac thao tac lien quan den Dia diem du lich (Tourist Places).
    /// Bao gom cac tinh nang: Danh sach, Chi tiet, Tim kiem, Ban do va Danh gia.
    /// </summary>
    public class TouristPlaceController : Controller
    {
        private readonly ITouristPlaceRepository _touristPlaceRepository;
        private readonly ICategoryRepository _categoryRepository;
        private readonly IRegionRepository _regionRepository;
        private readonly ApplicationDbContext _context;

        public TouristPlaceController(
            ITouristPlaceRepository touristPlaceRepository,
            ICategoryRepository categoryRepository,
            IRegionRepository regionRepository,
            ApplicationDbContext context)
        {
            _touristPlaceRepository = touristPlaceRepository;
            _categoryRepository = categoryRepository;
            _regionRepository = regionRepository;
            _context = context;
        }

        /// <summary>
        /// Hien thi danh sach cac dia diem du lich voi tinh nang loc theo danh muc va khu vuc.
        /// </summary>
        /// <param name="categoryId">Ma danh muc can loc</param>
        /// <param name="regionId">Ma khu vuc can loc</param>
        /// <returns>View danh sach dia diem</returns>
        public IActionResult Index(int? categoryId, int? regionId)
        {
            var categories = _categoryRepository.GetAllCategories();
            var regions = _regionRepository.GetAllRegions();

            ViewBag.Categories = categories;
            ViewBag.Regions = regions;
            ViewBag.Manufacturers = regions; // Su dung cho tuong thich voi View hien tai

            var allTouristPlaces = _context.TouristPlaces
                .Include(p => p.Category)
                .Include(p => p.Region)
                .Include(p => p.Reviews)
                .AsQueryable();

            if (categoryId.HasValue)
                allTouristPlaces = allTouristPlaces.Where(p => p.CategoryId == categoryId.Value);

            if (regionId.HasValue)
                allTouristPlaces = allTouristPlaces.Where(p => p.RegionId == regionId.Value);

            // Tinh toan xep hang trung binh dua tren cac danh gia (Reviews)
            var placesList = allTouristPlaces.ToList();
            foreach (var place in placesList)
            {
                if (place.Reviews != null && place.Reviews.Any())
                {
                    place.Rating = (int)Math.Round(place.Reviews.Average(r => r.Rating));
                }
            }

            return View(placesList);
        }

        /// <summary>
        /// Hien thi chi tiet mot dia diem du lich cu the.
        /// </summary>
        /// <param name="id">Ma dinh danh cua dia diem</param>
        /// <returns>View chi tiet dia diem</returns>
        public IActionResult Display(string id)
        {
            var touristPlace = _context.TouristPlaces
                .Include(p => p.Reviews)
                .Include(p => p.Category)
                .Include(p => p.Region)
                .FirstOrDefault(p => p.Id == id);

            if (touristPlace == null)
                return NotFound();

            // Tinh toan so lieu thong ke ve xep hang
            if (touristPlace.Reviews != null && touristPlace.Reviews.Any())
            {
                ViewBag.AverageRating = touristPlace.Reviews.Average(r => r.Rating);
                ViewBag.RatingCount = touristPlace.Reviews.Count();
            }
            else
            {
                ViewBag.AverageRating = 0;
                ViewBag.RatingCount = 0;
            }

            return View(touristPlace);
        }

        /// <summary>
        /// Tim kiem dia diem du lich theo tu khoa (Ten hoac Mo ta).
        /// </summary>
        /// <param name="query">Tu khoa tim kiem</param>
        /// <returns>View Index kem ket qua tim kiem</returns>
        public IActionResult Search(string query)
        {
            if (string.IsNullOrWhiteSpace(query))
                return RedirectToAction("Index");

            var touristPlaces = _touristPlaceRepository.GetAll()
                .Where(p =>
                    (!string.IsNullOrEmpty(p.Name) && p.Name.Contains(query, StringComparison.OrdinalIgnoreCase)) ||
                    (!string.IsNullOrEmpty(p.Description) && p.Description.Contains(query, StringComparison.OrdinalIgnoreCase)))
                .ToList();

            ViewBag.Categories = _categoryRepository.GetAllCategories();
            ViewBag.Regions = _regionRepository.GetAllRegions();

            return View("Index", touristPlaces);
        }


        /// <summary>
        /// Hien thi giao dien ban do tương tac cho tat ca cac dia diem du lich.
        /// </summary>
        /// <returns>View Google Map/Leaflet Map</returns>
        public IActionResult Map()
        {
            var categories = _categoryRepository.GetAllCategories();
            var regions = _regionRepository.GetAllRegions();
            var allTouristPlaces = _touristPlaceRepository.GetAll().ToList();

            ViewBag.Categories = categories;
            ViewBag.Manufacturers = regions;
            ViewBag.TouristPlacesJson = System.Text.Json.JsonSerializer.Serialize(
                allTouristPlaces.Select(tp => new { tp.Id, tp.Name, tp.Latitude, tp.Longitude, tp.CategoryId })
            );

            return View(allTouristPlaces);
        }

        public IActionResult Privacy()
        {
            return View();
        }

        /// <summary>
        /// API: Tiep nhan danh gia va xep hang tu nguoi dung (Yeu cau dang nhap).
        /// </summary>
        /// <param name="touristPlaceId">Ma dia diem</param>
        /// <param name="rating">So sao (1-5)</param>
        /// <returns>HTTP Status Code</returns>
        [HttpPost]
        [Authorize]
        public IActionResult Rate(string touristPlaceId, int rating)
        {
            if (string.IsNullOrEmpty(touristPlaceId) || rating < 1 || rating > 5)
                return BadRequest();

            var touristPlace = _context.TouristPlaces.Find(touristPlaceId);
            if (touristPlace == null)
                return NotFound();

            var review = new Review
            {
                TouristPlaceId = touristPlaceId,
                Rating = rating,
                CreatedAt = DateTime.Now
            };

            _context.Reviews.Add(review);
            _context.SaveChanges();

            return Ok();
        }

    }
}
