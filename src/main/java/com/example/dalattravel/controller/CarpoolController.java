package com.example.DaLattravel.controller;

import com.example.DaLattravel.dto.CarpoolMatchResult;
import com.example.DaLattravel.dto.CarpoolTripInfo;
import com.example.DaLattravel.dto.CarpoolViewModel;
import com.example.DaLattravel.dto.RouteMatchResult;
import com.example.DaLattravel.model.*;
import com.example.DaLattravel.repository.*;
import com.example.DaLattravel.service.CarpoolMatchingService;
import com.example.DaLattravel.service.RouteMatchingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carpool")
public class CarpoolController {

    private final VehicleRepository vehicleRepository;
    private final PassengerRepository passengerRepository;
    private final PassengerGroupRepository passengerGroupRepository;
    private final PendingCarpoolRequestRepository pendingRequestRepository;
    private final CompletedTripRepository completedTripRepository;
    private final CarpoolMatchingService carpoolMatchingService;
    private final RouteMatchingService routeMatchingService;

    public CarpoolController(
            VehicleRepository vehicleRepository,
            PassengerRepository passengerRepository,
            PassengerGroupRepository passengerGroupRepository,
            PendingCarpoolRequestRepository pendingRequestRepository,
            CompletedTripRepository completedTripRepository,
            CarpoolMatchingService carpoolMatchingService,
            RouteMatchingService routeMatchingService) {
        this.vehicleRepository = vehicleRepository;
        this.passengerRepository = passengerRepository;
        this.passengerGroupRepository = passengerGroupRepository;
        this.pendingRequestRepository = pendingRequestRepository;
        this.completedTripRepository = completedTripRepository;
        this.carpoolMatchingService = carpoolMatchingService;
        this.routeMatchingService = routeMatchingService;
    }

    @GetMapping
    public String index(Model model) {
        List<Vehicle> activeVehicles = vehicleRepository.findByActiveTrue();
        List<CarpoolTripInfo> openTrips = activeVehicles.stream().map(v -> CarpoolTripInfo.builder()
                .tripId(v.getId())
                .driverName(v.getDriverName())
                .driverPhone(v.getLicensePlate())
                .licensePlate(v.getLicensePlate())
                .pickupAddress(v.getPickupAddress() != null ? v.getPickupAddress() : "")
                .dropoffAddress(v.getDropoffAddress() != null ? v.getDropoffAddress() : "")
                .departureTime(v.getDepartureTime())
                .totalSeats(v.getTotalSeats())
                .availableSeats(v.getAvailableSeats())
                .vehicleType(v.getVehicleType())
                .costPerSeat(v.getCostPerKm() != null ? v.getCostPerKm() : BigDecimal.ZERO)
                .build()).collect(Collectors.toList());

        CarpoolViewModel viewModel = CarpoolViewModel.builder()
                .openTrips(openTrips)
                .seatOptions(List.of(4, 7, 9))
                .build();

        model.addAttribute("model", viewModel);
        return "carpool/index";
    }

    @PostMapping("/request")
    public String submitRequest(@ModelAttribute("model") CarpoolViewModel inputModel, Model model) {
        if (inputModel.getPassengerName() == null || inputModel.getPassengerName().trim().isEmpty()) {
            inputModel.setStatusMessage("Vui lòng nhập tên khách hàng.");
            return index(model);
        }

        Passenger passenger = Passenger.builder()
                .name(inputModel.getPassengerName())
                .phoneNumber(inputModel.getPhoneNumber())
                .pickupAddress(inputModel.getPickupAddress())
                .pickupLatitude(inputModel.getPickupLatitude())
                .pickupLongitude(inputModel.getPickupLongitude())
                .dropoffAddress(inputModel.getDropoffAddress())
                .dropoffLatitude(inputModel.getDropoffLatitude())
                .dropoffLongitude(inputModel.getDropoffLongitude())
                .preferredDepartureTime(inputModel.getPreferredDepartureTime() != null ? inputModel.getPreferredDepartureTime() : LocalDateTime.now().plusHours(1))
                .matched(false)
                .build();

        passengerRepository.save(passenger);

        List<Vehicle> availableVehicles = vehicleRepository.findByActiveTrue();
        List<Passenger> unmatchedPassengers = passengerRepository.findByMatchedFalse();
        List<PassengerGroup> groups = passengerGroupRepository.findAll();

        List<CarpoolMatchResult> matchResults = carpoolMatchingService.matchPassengersToVehicles(availableVehicles, unmatchedPassengers, groups);

        if (!matchResults.isEmpty()) {
            CarpoolMatchResult topMatch = matchResults.get(0);
            passenger.setMatched(true);
            passenger.setMatchedVehicleId(topMatch.getVehicleId());
            passengerRepository.save(passenger);

            Optional<Vehicle> matchedVehicleOpt = vehicleRepository.findById(topMatch.getVehicleId());
            if (matchedVehicleOpt.isPresent()) {
                Vehicle matchedVehicle = matchedVehicleOpt.get();
                matchedVehicle.setAvailableSeats(topMatch.getAvailableSeats());
                vehicleRepository.save(matchedVehicle);
            }

            inputModel.setStatusMessage("Đã ghép xe thành công! Mã chuyến: " + topMatch.getVehicleId() + ", Tài xế: " + topMatch.getDriverName());
        } else {
            PendingCarpoolRequest pendingReq = PendingCarpoolRequest.builder()
                    .passengerName(inputModel.getPassengerName())
                    .phoneNumber(inputModel.getPhoneNumber())
                    .pickupAddress(inputModel.getPickupAddress())
                    .pickupLatitude(inputModel.getPickupLatitude())
                    .pickupLongitude(inputModel.getPickupLongitude())
                    .dropoffAddress(inputModel.getDropoffAddress())
                    .dropoffLatitude(inputModel.getDropoffLatitude())
                    .dropoffLongitude(inputModel.getDropoffLongitude())
                    .preferredDepartureTime(inputModel.getPreferredDepartureTime() != null ? inputModel.getPreferredDepartureTime() : LocalDateTime.now().plusHours(1))
                    .requiredSeats(inputModel.getNumberOfPassengers())
                    .requestedVehicleSeats(inputModel.getRequestedVehicleSeats())
                    .status(RequestStatus.PENDING)
                    .passengerId(passenger.getId())
                    .build();

            pendingRequestRepository.save(pendingReq);

            inputModel.setStatusMessage("Hiện chưa tìm thấy chuyến xe phù hợp. Yêu cầu của bạn đã được ghi nhận và đang chờ ghép.");
        }

        return index(model);
    }
}
