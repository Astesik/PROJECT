package com.example.ioproject.controllers;

import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Endpoint: Pobierz listę pojazdów (dla wszystkich autoryzowanych użytkowników)
    @GetMapping("/get")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // Endpoint: Pobierz pojazd po ID (dla wszystkich autoryzowanych użytkowników)
    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Optional<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    // Endpoint: Pobierz zdjecie po url (dla wszystkich autoryzowanych użytkowników)
    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getVehiclePhoto(@PathVariable String filename) {
        try {
            // Tworzenie ścieżki do pliku
            Path filePath = Paths.get("uploads/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Ustalanie typu MIME na podstawie zawartości pliku
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Endpoint: Dodaj nowy pojazd (dla admina)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> addVehicle(@RequestParam("file") MultipartFile file,
                                              @RequestParam("make") String make,
                                              @RequestParam("model") String model,
                                              @RequestParam("production_year") int productionYear,
                                              @RequestParam("license_plate") String licensePlate,
                                              @RequestParam("type") String type,
                                              @RequestParam("mileage") int mileage,
                                              @RequestParam("technical_condition") String technicalCondition,
                                              @RequestParam("price_per_day") double pricePerDay) {
        try{
            // Pobranie typu pliku
            String contentType = file.getContentType();
            System.out.println(contentType);
            String extension = "";

            // Mapowanie typu na rozszerzenie
            if (contentType != null) {
                switch (contentType) {
                    case "image/jpeg":
                        extension = ".jpg";
                        break;
                    case "image/png":
                        extension = ".png";
                        break;
                    case "image/gif":
                        extension = ".gif";
                        break;
                    default:
                        return null;
                }
            }

            // Tworzenie ścieżki zapisu
            String fileName = UUID.randomUUID().toString() + "_" + extension;
            Path filePath = Paths.get("uploads/" + fileName);

            // Zapis na dysku
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);

            // Zwrócenie URL-a pliku
            String fileUrl = "/files/" + fileName;

            Vehicle vehicle = new Vehicle(make, model, productionYear,
                                            licensePlate, type, mileage,
                                            technicalCondition, pricePerDay, fileUrl);

            return ResponseEntity.ok(vehicleService.saveVehicle(vehicle, fileUrl));
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint: Zaktualizuj pojazd (dla admina)
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public Vehicle updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
//        vehicle.setId(id); // Ustawiamy ID, aby dokonać aktualizacji istniejącego pojazdu
//        return vehicleService.saveVehicle(vehicle);
//    }

    // Endpoint: Usuń pojazd (dla admina)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}