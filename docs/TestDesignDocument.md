# Functional Test Document  
**API:** Holiday Planner API  
**Version:** 2.1.0  
**Base URLs:**  
- Development: `http://localhost:8080`  
- Production: `/`  

---
## 1. Introduction
This document outlines the functional test plan for the Holiday Planner API. 
The API provides endpoints to fetch holiday data using the Nager Date API. 
The test plan includes various test scenarios, including positive and negative test cases, 
to ensure the API functions as expected.

## 2. Version History

| Version | Date       | Author         | Description                                                                                   |
|---------|------------|----------------|-----------------------------------------------------------------------------------------------|
| 1.0.0   | 2025-09-10 | Paresh Agrawal | Initial version                                                                               |
| 2.0.0   | 2025-09-19 | Paresh Agrawal | Added some negative test cases                                                                |
| 2.1.0   | 2025-09-23 | Paresh Agrawal | Added performance test, javadoc, improved code structure and added clean test design document |

---
## 3. API Endpoints and Test Scenarios
Below can be endpoints and detailed test scenarios for each endpoint including positive and negative test cases.

### 3.1 Get Shared Holidays Between Two Countries

**Endpoint:** `GET /api/holidays/shared/{year}/{countryCode1}/{countryCode2}`  
**Description:** Returns a deduplicated list of holidays celebrated on the same date in both countries for the given year, including local names.

#### Test Scenarios

| Test Case | Description                                   | Input                                       | Expected Output                               |
|-----------|-----------------------------------------------|---------------------------------------------|-----------------------------------------------|
| TC-1.1    | Valid request for shared holidays             | year=2025, countryCode1=NL, countryCode2=FR | 200 OK, JSON array of `SharedHoliday` objects |
| TC-1.2    | Invalid year (not 4 digits)                   | year=25, countryCode1=NL, countryCode2=FR   | 400 Bad Request, error message                |
| TC-1.3    | Invalid year (not a digits)                   | year=AB, countryCodes=NL,DE                 | 400 Bad Request, error message                |
| TC-1.4    | Invalid country code (not ISO 3166-1 alpha-2) | year=2025, countryCode1=XX, countryCode2=YY | 400 Bad Request, error message                |
| TC-1.5    | Non supported one country code                | year=2025, countryCode1=NL, countryCode2=SA | 400 Bad Request, error message                |
| TC-1.6    | Non supported both country code               | year=2025, countryCode1=AE, countryCode2=SA | 400 Bad Request, error message                |
| TC-1.7    | Valid and Invalid country code                | year=2025, countryCode1=NL, countryCode2=YY | 400 Bad Request, error message                |
| TC-1.8    | Year out of supported range                   | year=2076, countryCode1=NL, countryCode2=FR | 400 Bad Request, error message                |
| TC-1.9    | Server error (simulate backend failure)       | Valid input                                 | 500 Internal Server Error, error message      |

---

### 3.2 Get Count of Non-Weekend Holidays

**Endpoint:** `GET /api/holidays/non-weekend/{year}?countryCodes={comma-separated list of country codes}`
**Description:** Returns the number of public holidays not falling on weekends for each country in the given year, sorted in descending order.

#### Test Scenarios

| Test Case | Description                             | Input                            | Expected Output                                     |
|-----------|-----------------------------------------|----------------------------------|-----------------------------------------------------|
| TC-2.1    | Valid request for non-weekend holidays  | year=2025, countryCodes=NL,DE    | 200 OK, JSON array of `CountryHolidayCount` objects |
| TC-2.2    | Invalid year (not 4 digits)             | year=25, countryCodes=NL,DE      | 400 Bad Request, error message                      |
| TC-2.3    | Invalid year (not a digits)             | year=AB, countryCodes=NL,DE      | 400 Bad Request, error message                      |
| TC-2.4    | Invalid country code in list            | year=2025, countryCodes=NL,XX    | 400 Bad Request, error message                      |
| TC-2.5    | Non-supported country code in list      | year=2025, countryCodes=NL,AU,AE | 400 Bad Request, error message                      |
| TC-2.6    | Year out of supported range             | year=1970, countryCodes=NL,DE    | 400 Bad Request, error message                      |
| TC-2.7    | Server error (simulate backend failure) | Valid input                      | 500 Internal Server Error, error message            |

---

### 3.3 Get Last Number of Celebrated Holidays for a Country

**Endpoint:** `GET /api/holidays/last-number-of-holidays/{countryCode}?numberOfHolidays={number}`
**Description:** Returns the most recent provided number of holidays (default 3, max 12) celebrated in the specified country.

#### Test Scenarios

| Test Case | Description                                | Input                               | Expected Output                                  |
|-----------|--------------------------------------------|-------------------------------------|--------------------------------------------------|
| TC-3.1    | Valid request for last holidays (default)  | countryCode=NL                      | 200 OK, JSON array of up to 3 `Holiday` objects  |
| TC-3.2    | Valid request for last holidays (max 12)   | countryCode=NL, numberOfHolidays=12 | 200 OK, JSON array of up to 12 `Holiday` objects |
| TC-3.3    | Invalid country code                       | countryCode=XX, numberOfHolidays=12 | 400 Bad Request, error message                   |
| TC-3.4    | Non supported one country code             | countryCode=SA, numberOfHolidays=5  | 400 Bad Request, error message                   |
| TC-3.5    | Invalid numberOfHolidays (not a number)    | countryCode=NL, numberOfHolidays=AB | 400 Bad Request, error message                   |
| TC-3.6    | Invalid numberOfHolidays (out of range)    | countryCode=NL, numberOfHolidays=20 | 400 Bad Request, error message                   |
| TC-3.7    | Server error (simulate backend failure)    | Valid input                         | 500 Internal Server Error, error message         |

---

## 4. Response Schema Validation

### 4.1 SharedHoliday Object
- `date`: string, format `YYYY-MM-DD`
- `localNameCountry1`: string
- `localNameCountry2`: string

### 4.2 CountryHolidayCount Object
- `countryCode`: string (ISO 3166-1 alpha-2)
- `holidayCount`: integer

### 4.3 Holiday Object
- `date`: string, format `YYYY-MM-DD`
- `localName`: string

### 4.4 ErrorResponse Object
- `timestamp`: string (date-time)
- `status`: integer
- `error`: string
- `message`: string
- `path`: string

---

## 5. Negative Test Scenarios (General)

- Missing required path or query parameters
- Invalid parameter formats (e.g., characters and numbers, special characters)
- Empty responses
- Unexpected HTTP methods (e.g., POST, PUT, DELETE)

---
