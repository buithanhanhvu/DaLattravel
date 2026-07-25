using WebDuLichDaLat.Models;
using System.Collections.Generic;
using System.Linq;

namespace WebDuLichDaLat.Services
{
    /// <summary>
    /// Dich vu thuc hien thuat toan K-Means Clustering de phan nhom hanh khach dua tren vi tri dia ly.
    /// Muc tieu: Toi uu hoa viec chia cac nhom hanh khach vao cac phuong tiện di chung.
    /// </summary>
    public class KMeansClusteringService
    {
        /// <summary>
        /// Dai dien cho mot diem du lieu trong thuat toan phan cum.
        /// </summary>
        public class ClusterPoint
        {
            public double Latitude { get; set; }
            public double Longitude { get; set; }
            public int PassengerId { get; set; }
            public Passenger? Passenger { get; set; }
        }

        /// <summary>
        /// Dai dien cho mot cum (Cluster) sau khi phan loai.
        /// </summary>
        public class Cluster
        {
            public double CenterLatitude { get; set; }
            public double CenterLongitude { get; set; }
            public List<ClusterPoint> Points { get; set; } = new List<ClusterPoint>();
        }

        /// <summary>
        /// Thuc hien phan cum danh sach hanh khach thanh k nhom khac nhau.
        /// </summary>
        /// <param name="passengers">Danh sach hanh khach can phan cum</param>
        /// <param name="k">So luong cum mong muon</param>
        /// <returns>Danh sach cac cum chua hanh khach tuong ung</returns>
        public List<Cluster> ClusterPassengers(List<Passenger> passengers, int k)
        {
            if (passengers == null || !passengers.Any())
                return new List<Cluster>();

            // Chuyen doi thong tin hanh khach sang dang cac diem toa do (Points)
            var points = passengers.Select(p => new ClusterPoint
            {
                Latitude = p.PickupLatitude,
                Longitude = p.PickupLongitude,
                PassengerId = p.Id,
                Passenger = p
            }).ToList();

            if (points.Count <= k)
            {
                // Truong hop so luong diem thap hon hoac bang k: moi diem se tro thanh mot cum rieng biet
                return points.Select(p => new Cluster
                {
                    CenterLatitude = p.Latitude,
                    CenterLongitude = p.Longitude,
                    Points = new List<ClusterPoint> { p }
                }).ToList();
            }

            // Khoi tao cac diem trung tam (Centroids) ngau nhien tu danh sach cac diem co san
            var random = new Random();
            var clusters = new List<Cluster>();
            for (int i = 0; i < k; i++)
            {
                var randomPoint = points[random.Next(points.Count)];
                clusters.Add(new Cluster
                {
                    CenterLatitude = randomPoint.Latitude,
                    CenterLongitude = randomPoint.Longitude,
                    Points = new List<ClusterPoint>()
                });
            }

            // Bat dau qua trinh lap de toi uu hoa cac cum (K-Means Algorithm Iterations)
            bool changed = true;
            int maxIterations = 100;
            int iteration = 0;

            while (changed && iteration < maxIterations)
            {
                iteration++;
                changed = false;

                // Lam moi danh sach cac diem trong moi cum o moi vong lap
                foreach (var cluster in clusters)
                {
                    cluster.Points.Clear();
                }

                // Gan tung diem toa do vao cum co trung tam gan nhat
                foreach (var point in points)
                {
                    var nearestCluster = clusters
                        .OrderBy(c => CalculateDistance(
                            point.Latitude, point.Longitude,
                            c.CenterLatitude, c.CenterLongitude))
                        .First();

                    nearestCluster.Points.Add(point);
                }

                // Cap nhat lai toa do trung tam cua moi cum dua tren gia tri trung binh cua cac diem trong cum do
                foreach (var cluster in clusters)
                {
                    if (cluster.Points.Any())
                    {
                        double newLat = cluster.Points.Average(p => p.Latitude);
                        double newLng = cluster.Points.Average(p => p.Longitude);

                        // Kiem tra su thay doi cua centroid de quyet dinh co tiep tuc lap hay không
                        if (Math.Abs(cluster.CenterLatitude - newLat) > 0.0001 ||
                            Math.Abs(cluster.CenterLongitude - newLng) > 0.0001)
                        {
                            changed = true;
                            cluster.CenterLatitude = newLat;
                            cluster.CenterLongitude = newLng;
                        }
                    }
                }
            }

            // Bo qua cac cum khong chua bat ky diem du lieu nao
            return clusters.Where(c => c.Points.Any()).ToList();
        }

        private double CalculateDistance(double lat1, double lon1, double lat2, double lon2)
        {
            const double R = 6371; // Ban kinh Trai Dat trung binh (km)
            double dLat = ToRadians(lat2 - lat1);
            double dLon = ToRadians(lon2 - lon1);
            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                       Math.Cos(ToRadians(lat1)) * Math.Cos(ToRadians(lat2)) *
                       Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return R * c;
        }

        private double ToRadians(double angle) => angle * Math.PI / 180;
    }
}




















