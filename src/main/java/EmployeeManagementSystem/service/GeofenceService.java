package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.OfficeLocation;
import EmployeeManagementSystem.repository.OfficeLocationRepository;
import EmployeeManagementSystem.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeofenceService {

    private static final Logger log =
            LoggerFactory.getLogger(GeofenceService.class);

    private final OfficeLocationRepository repository;

    private static final String MAIN_OFFICE = "Main Office";

    /*
     * Main Office fallback coordinates
     */
    private static final double DEFAULT_LATITUDE = 28.585422;
    private static final double DEFAULT_LONGITUDE = 77.313452;
    private static final double DEFAULT_ALLOWED_RADIUS = 200.0;


    // ============================================================
    // GET OFFICE
    // ============================================================

    public OfficeLocation getOffice() {

        OfficeLocation office =
                repository.findByOfficeName(MAIN_OFFICE)
                        .orElseGet(() -> {

                            log.warn(
                                    "Main Office not found in database. Creating default office."
                            );

                            OfficeLocation newOffice =
                                    OfficeLocation.builder()
                                            .officeName(MAIN_OFFICE)
                                            .latitude(DEFAULT_LATITUDE)
                                            .longitude(DEFAULT_LONGITUDE)
                                            .allowedRadius(DEFAULT_ALLOWED_RADIUS)
                                            .build();

                            return repository.save(newOffice);
                        });

        log.info("================================================");
        log.info("GEOFENCE OFFICE");
        log.info("Office ID        : {}", office.getId());
        log.info("Office Name      : {}", office.getOfficeName());
        log.info("Office Latitude  : {}", office.getLatitude());
        log.info("Office Longitude : {}", office.getLongitude());
        log.info("Allowed Radius   : {} meters", office.getAllowedRadius());
        log.info("================================================");

        return office;
    }


    // ============================================================
    // CALCULATE DISTANCE
    // ============================================================

    public double getDistance(
            Double latitude,
            Double longitude) {

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(
                    "GPS latitude and longitude are required for WFO login."
            );
        }

        OfficeLocation office = getOffice();

        if (office.getLatitude() == null ||
                office.getLongitude() == null) {

            throw new IllegalStateException(
                    "Office latitude/longitude are not configured."
            );
        }

        log.info("================================================");
        log.info("GEOFENCE REQUEST");
        log.info("User Latitude   : {}", latitude);
        log.info("User Longitude  : {}", longitude);
        log.info("Office Latitude : {}", office.getLatitude());
        log.info("Office Longitude: {}", office.getLongitude());

        /*
         * ========================================================
         * ACTUAL REQUEST CALCULATION
         * ========================================================
         */
        double distance =
                DistanceCalculator.calculateDistance(
                        latitude,
                        longitude,
                        office.getLatitude(),
                        office.getLongitude()
                );

        log.info(
                "ACTUAL CALCULATED DISTANCE: {} meters",
                distance
        );

        log.info("================================================");

        return distance;
    }


    // ============================================================
    // CHECK OFFICE GEOFENCE
    // ============================================================

    public boolean isInsideOffice(
            Double latitude,
            Double longitude) {

        OfficeLocation office = getOffice();

        double distance =
                getDistance(
                        latitude,
                        longitude
                );

        double allowedRadius =
                office.getAllowedRadius() == null
                        ? DEFAULT_ALLOWED_RADIUS
                        : office.getAllowedRadius();

        boolean inside =
                distance <= allowedRadius;

        log.info("================================================");
        log.info("GEOFENCE RESULT");
        log.info("Distance        : {} meters", distance);
        log.info("Allowed Radius  : {} meters", allowedRadius);
        log.info("Inside Office   : {}", inside);
        log.info("================================================");

        return inside;
    }
}