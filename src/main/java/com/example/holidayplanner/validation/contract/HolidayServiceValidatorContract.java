package com.example.holidayplanner.validation.contract;

import java.util.Set;

/**
 * Validator interface for validating input parameters related to holiday services.
 * <p>
 * This interface defines methods to validate country codes, number of days, and year parameters
 * for holiday service operations, ensuring correct and consistent input data.
 */
public interface HolidayServiceValidatorContract {

    /**
     * Validates set of input country codes and number of days.
     * <p>
     * Ensures that the provided country codes and number of days string are valid for holiday queries.
     *
     * @param inputCountryCodes Set of ISO country codes to validate
     * @param numberOfDaysStr String representing the number of days to validate
     */
    void validateCountryCodesAndDays(Set<String> inputCountryCodes, String numberOfDaysStr);

    /**
     * Validates input year and set of country codes.
     * <p>
     * Checks that the year string and country codes are valid for holiday count queries.
     *
     * @param yearString String representing the year to validate
     * @param inputCountryCodes Set of ISO country codes to validate
     */
    void validateCountryCodesAndYear(String yearString, Set<String> inputCountryCodes);

    /**
     * Validates input year and exactly two different input country codes.
     * <p>
     * Ensures that the year and country codes are valid for shared holiday queries between two countries.
     *
     * @param year String representing the year to validate
     * @param inputCountryCodes Set of exactly two ISO country codes to validate
     */
    void validateSharedHolidayCountryCodesAndYear(String year, Set<String> inputCountryCodes);
}
