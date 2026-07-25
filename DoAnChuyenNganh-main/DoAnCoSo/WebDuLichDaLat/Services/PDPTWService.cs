using WebDuLichDaLat.Models;
using System.Collections.Generic;
using System.Linq;

namespace WebDuLichDaLat.Services
{
    /// <summary>
    /// Dich vu giai quyet bai toan Pickup and Delivery Problem with Time Windows (PDPTW).
    /// Giup toi uu hoa thu tu don va tra khach dua tren toa do dia ly va thoi gian yeu cau.
    /// </summary>
    public class PDPTWService
    {
        /// <summary>
        /// Dai dien cho mot diem dung (don hoac tra khach) trong lo trinh.
        /// </summary>
        public class PDPTWNode
        {
            public double Latitude { get; set; }
            public double Longitude { get; set; }
            public string Address { get; set; }
            public string Type { get; set; } // Gia tri: "pickup" hoac "dropoff"
            public int? PassengerId { get; set; }
            public DateTime EarliestTime { get; set; }
            public DateTime LatestTime { get; set; }
            public int ServiceTime { get; set; } = 5; // Thoi gian dung cho moi diem (phut)
        }

        /// <summary>
        /// Ket qua lo trinh sau khi da duoc toi uu hoa.
        /// </summary>
        public class PDPTWRoute
        {
            public List<PDPTWNode> Nodes { get; set; } = new List<PDPTWNode>();
            public double TotalDistance { get; set; }
            public DateTime StartTime { get; set; }
            public DateTime EndTime { get; set; }
            public decimal TotalCost { get; set; }
        }

        /// <summary>
        /// Thuc hien toi uu hoa lo trinh di chuyen cho phuong tien va danh sach hanh khach.
        /// </summary>
        /// <param name="vehicle">Thong tin phuong tien van chuyen</param>
        /// <param name="passengers">Danh sach hanh khach can don/tra</param>
        /// <returns>Doi tuong PDPTWRoute chua lo trinh toi uu</returns>
        public PDPTWRoute OptimizeRoute(Vehicle vehicle, List<Passenger> passengers)
        {
            if (!passengers.Any())
            {
                return new PDPTWRoute
                {
                    Nodes = new List<PDPTWNode>(),
                    TotalDistance = 0,
                    StartTime = vehicle.DepartureTime,
                    EndTime = vehicle.DepartureTime,
                    TotalCost = 0
                };
            }

            // Khoi tao danh sach cac diem nut (Nodes) cho viec don va tra khach
            var nodes = new List<PDPTWNode>();

            foreach (var passenger in passengers)
            {
                nodes.Add(new PDPTWNode
                {
                    Latitude = passenger.PickupLatitude,
                    Longitude = passenger.PickupLongitude,
                    Address = passenger.PickupAddress ?? "",
                    Type = "pickup",
                    PassengerId = passenger.Id,
                    EarliestTime = passenger.PreferredDepartureTime.AddMinutes(-30),
                    LatestTime = passenger.PreferredDepartureTime.AddHours(2),
                    ServiceTime = 5
                });

                nodes.Add(new PDPTWNode
                {
                    Latitude = passenger.DropoffLatitude,
                    Longitude = passenger.DropoffLongitude,
                    Address = passenger.DropoffAddress ?? "",
                    Type = "dropoff",
                    PassengerId = passenger.Id,
                    EarliestTime = passenger.PreferredArrivalTime?.AddMinutes(-30) ?? passenger.PreferredDepartureTime.AddHours(1),
                    LatestTime = passenger.PreferredArrivalTime?.AddHours(2) ?? passenger.PreferredDepartureTime.AddHours(4),
                    ServiceTime = 5
                });
            }

            // Su dung thuat toan Greedy (Nearest Neighbor) ket hop voi cac rang buoc ve thoi gian
            var route = new PDPTWRoute
            {
                Nodes = new List<PDPTWNode>(),
                StartTime = vehicle.DepartureTime
            };

            var unvisited = new List<PDPTWNode>(nodes);
            var currentTime = vehicle.DepartureTime;
            double currentLat = vehicle.PickupLatitude;
            double currentLng = vehicle.PickupLongitude;
            var pickedUpPassengers = new HashSet<int>();

            while (unvisited.Any())
            {
                PDPTWNode nextNode = null;
                double minDistance = double.MaxValue;

                foreach (var node in unvisited)
                {
                    // Rang buoc nghiep vu: Hanh khach phai duoc don (pickup) truoc khi tra (dropoff)
                    if (node.Type == "dropoff" && !pickedUpPassengers.Contains(node.PassengerId.Value))
                        continue;

                    // Rang buoc ve cua so thoi gian (Time Window)
                    if (currentTime > node.LatestTime)
                        continue;

                    double distance = CalculateDistance(currentLat, currentLng, node.Latitude, node.Longitude);
                    double tempTravelTime = distance / 50.0 * 60; // Gia thiet van toc trung binh la 50km/h

                    DateTime tempArrivalTime = currentTime.AddMinutes(tempTravelTime);
                    if (tempArrivalTime < node.EarliestTime)
                        tempArrivalTime = node.EarliestTime;

                    if (tempArrivalTime <= node.LatestTime)
                    {
                        // Lua chon diem dung tiep theo dua tren tieu chi khoang cach va thoi gian hop le
                        double score = distance;
                        if (tempArrivalTime < node.EarliestTime)
                            score += 1000; // Ap dung hinh phat (Penalty) neu den qua som so voi cua so thoi gian

                        if (score < minDistance)
                        {
                            minDistance = score;
                            nextNode = node;
                        }
                    }
                }

                if (nextNode == null)
                {
                    // Truong hop khong tim thay diem dung hop le theo thoi gian, lua chon diem gan nhat con lai
                    nextNode = unvisited
                        .OrderBy(n => CalculateDistance(currentLat, currentLng, n.Latitude, n.Longitude))
                        .FirstOrDefault();

                    if (nextNode == null)
                        break;
                }

                // Cap nhat thong tin trang thai lo trinh
                double distanceToNode = CalculateDistance(currentLat, currentLng, nextNode.Latitude, nextNode.Longitude);
                double travelTime = distanceToNode / 50.0 * 60;
                DateTime arrivalTime = currentTime.AddMinutes(travelTime);

                if (arrivalTime < nextNode.EarliestTime)
                    arrivalTime = nextNode.EarliestTime;

                currentTime = arrivalTime.AddMinutes(nextNode.ServiceTime);
                currentLat = nextNode.Latitude;
                currentLng = nextNode.Longitude;

                route.Nodes.Add(nextNode);
                unvisited.Remove(nextNode);

                if (nextNode.Type == "pickup")
                    pickedUpPassengers.Add(nextNode.PassengerId.Value);
                else if (nextNode.Type == "dropoff")
                    pickedUpPassengers.Remove(nextNode.PassengerId.Value);
            }

            // Tong hop ket qua lo trinh (Tong khoang cach va chi phi)
            route.TotalDistance = 0;
            double routeLat = vehicle.PickupLatitude;
            double routeLng = vehicle.PickupLongitude;

            foreach (var node in route.Nodes)
            {
                route.TotalDistance += CalculateDistance(routeLat, routeLng, node.Latitude, node.Longitude);
                routeLat = node.Latitude;
                routeLng = node.Longitude;
            }

            route.EndTime = currentTime;
            route.TotalCost = (decimal)route.TotalDistance * vehicle.CostPerKm;

            return route;
        }

        private double CalculateDistance(double lat1, double lon1, double lat2, double lon2)
        {
            const double R = 6371; // Ban kinh Trai Dat (km)
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






