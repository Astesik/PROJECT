package com.example.ioproject.security.services;

import com.example.ioproject.models.Vehicle;
import com.example.ioproject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

//    public Optional<Vehicle> getVehiclePhotoByUrl(String file_url) {
//        return vehicleRepository.findByImage(file_url);
//    }

    public Vehicle saveVehicle(Vehicle vehicle, String photoUrl) {
        vehicle.setImagePath(photoUrl);
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}