package com.example.holidayplanner.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.holidayplanner.exception.InvalidParameterException;
import com.example.holidayplanner.model.AvailableCountry;
import com.example.holidayplanner.service.NagerDateApiService;

@Component
@RequiredArgsConstructor
@Slf4j
public class InputParameterValidator {

    private final NagerDateApiService nagerDateApiService;

    @Value("${max.number.of.holidays.can-be-returned:12}")
    private int maxHolidayDays;

    @Value("${min.holiday.search.supported.year:1975}")
    private int minHolidaySupportedYear;

    @Value("${max.holiday.search.supported.year:2075}")
    private int maxHolidaySupportedYear;

    private HashSet<String> filterInvalidCountryCode(Set<String> inputCountryCodes) {
        return new HashSet<>(inputCountryCodes.stream()
                .filter(item -> List.of(Locale.getISOCountries()).contains(item))
                .toList());
    }

    private void checkIfValidCountryCode(StringBuilder invalidCountryCodes, String inputCountryCode) {
        if (StringUtils.isEmpty(inputCountryCode) || inputCountryCode.length() != 2) {
            if (!invalidCountryCodes.isEmpty()) {
                invalidCountryCodes.append(", ");
            }
            invalidCountryCodes.append("'").append(StringUtils.isNotEmpty(inputCountryCode) ? inputCountryCode.toUpperCase() : "").append("'");
            return;
        }

        if (Arrays.stream(Locale.getISOCountries()).noneMatch(isoCode -> isoCode.equalsIgnoreCase(inputCountryCode))) {
            if (!invalidCountryCodes.isEmpty()) {
                invalidCountryCodes.append(", ");
            }
            invalidCountryCodes.append("'").append(inputCountryCode.toUpperCase()).append("'");
        }
    }

    private void validateInputCountryCodes(Set<String> inputCountryCodes, ArrayList<String> errorMessages) {
        StringBuilder invalidCountryCodes = new StringBuilder();
        for (String countryCode : inputCountryCodes) {
            checkIfValidCountryCode(invalidCountryCodes, countryCode);
        }

        if (!invalidCountryCodes.isEmpty()) {
            errorMessages.add(String.format("non ISO 3166-1 alpha-2 compliant country code(s) %s.", invalidCountryCodes));
        }
    }

    private void validateInputYear(String inputYear, ArrayList<String> errorMessages) {
        if (StringUtils.isEmpty(inputYear)) {
            errorMessages.add(String.format("empty or null input year, must be between %d and %d inclusive.", minHolidaySupportedYear, maxHolidaySupportedYear));
            return;
        }

        try {
            int inputYearValue = Integer.parseInt(inputYear);
            if (inputYearValue < minHolidaySupportedYear || inputYearValue > maxHolidaySupportedYear) {
                errorMessages.add(String.format("non-supported year '%s', must be between %d and %d inclusive.", inputYear, minHolidaySupportedYear, maxHolidaySupportedYear));
            }
        } catch (NumberFormatException e) {
            errorMessages.add(String.format("year '%s' is not a valid year, must be a number between %d and %d inclusive.", inputYear, minHolidaySupportedYear, maxHolidaySupportedYear));
        }
    }

    private void throwExceptionIfError(ArrayList <String> errorMessage){
        if (!errorMessage.isEmpty()) {
            String errorMessageStr = String.join(", ", errorMessage);
            log.warn(errorMessageStr);
            throw new InvalidParameterException(errorMessageStr);
        }
    }

    private void checkIfCountiesSupported(Set<AvailableCountry> supportedCountries, Set<String> inputCountryCodes, ArrayList<String> errorInInputCountryCodes) {
        StringBuilder unSupportedCountryCodes = new StringBuilder();
        Set<String> validInputCodes = inputCountryCodes;
        if (!errorInInputCountryCodes.isEmpty()) {
            validInputCodes = filterInvalidCountryCode(inputCountryCodes);
        }
        for (String countryCode : validInputCodes) {
            if (supportedCountries.stream().noneMatch(country ->
                    country.getCountryCode().equalsIgnoreCase(countryCode))) {
                if (!unSupportedCountryCodes.isEmpty()) {
                    unSupportedCountryCodes.append(", ");
                }
                unSupportedCountryCodes.append("'").append(countryCode.toUpperCase()).append("'");
            }
        }
        if (!unSupportedCountryCodes.isEmpty()) {
            errorInInputCountryCodes.add(String.format("holidays for input country code(s) %s is not supported as of now.", unSupportedCountryCodes));
        }
    }

    public void validateCountryCodesAndDays(Set<String> inputCountryCodes, int numberOfDays) {
        ArrayList <String> errorMessage = new ArrayList<>();
        if (numberOfDays > maxHolidayDays) {
            errorMessage.add(String.format("non-supported numberOfHolidays '%d', must be between 1 and %d inclusive.", numberOfDays, maxHolidayDays));
        }
        validateInputCountryCodes(inputCountryCodes, errorMessage);
        checkIfCountiesSupported(nagerDateApiService.getAvailableCountries()
                , inputCountryCodes, errorMessage);
        throwExceptionIfError(errorMessage);
    }

    public void validateCountryCodesAndYear(int year, Set<String> inputCountryCodes) {
        ArrayList <String> errorMessage = new ArrayList<>();
        validateInputCountryCodes(inputCountryCodes, errorMessage);
        checkIfCountiesSupported(nagerDateApiService.getAvailableCountries()
                , inputCountryCodes, errorMessage);
        validateInputYear(String.valueOf(year), errorMessage);
        throwExceptionIfError(errorMessage);
    }

    public void validateSharedHolidayCountryCodesAndYear(String year, Set<String> inputCountryCodes) {
        ArrayList <String> errorMessage = new ArrayList<>();
        if (inputCountryCodes.size() != 2) {
            errorMessage.add(String.format("input country code '%s' might be same, two different codes must be provided for shared holidays.", String.join("", inputCountryCodes)));
            throwExceptionIfError(errorMessage);
        }
        validateInputCountryCodes(inputCountryCodes, errorMessage);
        checkIfCountiesSupported(nagerDateApiService.getAvailableCountries()
                , inputCountryCodes, errorMessage);
        validateInputYear(year, errorMessage);
        throwExceptionIfError(errorMessage);
    }
}
