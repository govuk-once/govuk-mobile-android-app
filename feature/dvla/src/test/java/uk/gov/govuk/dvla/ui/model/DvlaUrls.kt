package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls

internal val dvlaUrls = DvlaUrls(
    addVehicle = "https://add-vehicle",
    renewLicence = "https://renew-licence",
    soldVehicle = "https://sold-vehicle",
    sornRules = "https://sorn-rules",
    makeSorn = "https://make-sorn",
    getLogbook = "https://get-logbook",
    changeLogbookAddress = "https://change-logbook-address",
    cancelTax = "https://cancel-tax",
    changeLicenceAddress = "https://www.gov.uk/change-licence-address",
    changeNameGenderLicence = "https://www.gov.uk/change-name-gender-licence",
    replaceLicence = "https://www.gov.uk/replace-licence",
    manageTaxPayment = "https://www.gov.uk/vehicle-tax-direct-debit/renewing",
    taxVehicle = "https://www.gov.uk/vehicle-tax",
    historicVehicles = "https://www.gov.uk/historic-vehicles",
    checkMot = "https://www.check-mot.service.gov.uk/results?registration=[NUMBER PLATE]&checkRecalls=true",
    driverDetails = "https://driver-and-vehicles-account.service.gov.uk/driver_details",
    account = "https://driver-and-vehicles-account.service.gov.uk",
    drivingRecord = "https://driver-and-vehicles-account.service.gov.uk/driver_details?locale=en#Entitlements",
    contact = "https://www.gov.uk/contact-the-dvla"
)
