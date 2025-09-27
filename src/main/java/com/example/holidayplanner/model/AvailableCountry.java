package com.example.holidayplanner.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Model class used in Nager API response representing a country with its code and name.
 * <p>
 * This class encapsulates the country code and name as returned by the Nager Date API.
 */
@Getter
@Setter
public class AvailableCountry {
    /**
     * ISO country code representing the country.
     */
    private String countryCode;
    /**
     * Name of the country.
     */
    private String name;
}