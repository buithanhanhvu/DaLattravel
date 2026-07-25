using WebDuLichDaLat.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

    /// <summary>
    /// Controller quan ly cac bai viet blog va tin tuc du lich.
    /// </summary>
    public class BlogController : Controller
    {
        private readonly ApplicationDbContext _context;

        public BlogController(ApplicationDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Hien thi danh sach tat ca cac bai viet blog, sap xep theo thoi gian moi nhat.
        /// </summary>
        /// <returns>View danh sach bai viet</returns>
        public async Task<IActionResult> Index()
        {
            var posts = await _context.BlogPosts
                .OrderByDescending(p => p.PostedDate)
                .ToListAsync();
            return View(posts);
        }

        /// <summary>
        /// Hien thi noi dung chi tiet cua mot bai viet blog.
        /// </summary>
        /// <param name="id">Ma dinh danh bai viet</param>
        /// <returns>View chi tiet bai viet</returns>
        public async Task<IActionResult> Detail(int id)
        {
            var post = await _context.BlogPosts.FindAsync(id);
            if (post == null) return NotFound();
            return View(post);
        }
    }

