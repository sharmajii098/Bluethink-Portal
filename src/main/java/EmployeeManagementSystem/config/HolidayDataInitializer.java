package EmployeeManagementSystem.config;

import EmployeeManagementSystem.entity.Holiday;
import EmployeeManagementSystem.repository.HolidayRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds Bluethink's 2026 organization-wide holiday calendar once.
 * Existing records are never overwritten, so HR can continue to manage
 * holidays from the Holiday Calendar screen after the initial seed.
 */
@Component
public class HolidayDataInitializer implements ApplicationRunner {

    private final HolidayRepository holidayRepository;

    public HolidayDataInitializer(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<SeedHoliday> holidays = List.of(
                new SeedHoliday(LocalDate.of(2026, 1, 1), "New Year"),
                new SeedHoliday(LocalDate.of(2026, 1, 26), "Republic Day"),
                new SeedHoliday(LocalDate.of(2026, 3, 4), "Holi"),
                new SeedHoliday(LocalDate.of(2026, 3, 5), "Holi"),
                new SeedHoliday(LocalDate.of(2026, 8, 15), "Independence Day"),
                new SeedHoliday(LocalDate.of(2026, 10, 2), "Mahatma Gandhi Jayanti"),
                new SeedHoliday(LocalDate.of(2026, 11, 8), "Diwali/Deepavali"),
                new SeedHoliday(LocalDate.of(2026, 12, 25), "Christmas Day")
        );

        for (SeedHoliday seed : holidays) {
            if (holidayRepository.existsByHolidayDateAndNameIgnoreCaseAndLocationIgnoreCase(
                    seed.date(), seed.name(), "ALL")) {
                continue;
            }
            Holiday holiday = new Holiday();
            holiday.setHolidayDate(seed.date());
            holiday.setName(seed.name());
            holiday.setHolidayType("PUBLIC");
            holiday.setLocation("ALL");
            holiday.setOptionalHoliday(false);
            holiday.setActive(true);
            holiday.setDescription("Bluethink organization holiday");
            holidayRepository.save(holiday);
        }
    }

    private record SeedHoliday(LocalDate date, String name) {}
}
