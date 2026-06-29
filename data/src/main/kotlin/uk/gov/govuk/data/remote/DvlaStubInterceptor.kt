package uk.gov.govuk.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

// Todo - remove!!!
internal class DvlaStubInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val body = when {
            method == "GET" && path.endsWith("/app/udp/v1/identity") -> IDENTITY
            method == "GET" && path.endsWith("/app/dvla/v1/driver-summary") -> DRIVER_SUMMARY
            method == "GET" && path.endsWith("/app/dvla/v1/customer-summary") -> CUSTOMER_SUMMARY
            method == "POST" && path.endsWith("/app/dvla/v1/share-code") -> SINGLE_SHARE_CODE
            method == "GET" && path.endsWith("/app/dvla/v1/share-codes") -> MULTI_SHARE_CODE
            method == "POST" && path.contains("/app/dvla/v1/share-code/") -> SINGLE_SHARE_CODE
            else -> null
        } ?: return chain.proceed(request)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    companion object {
        private val IDENTITY = """{"services":["dvla"]}"""

        private val DRIVER_SUMMARY = """
            {
              "driverViewResponse": {
                "driver": {
                  "drivingLicenceNumber": "MORGA753116SM9IJ",
                  "firstNames": "Sarah",
                  "lastName": "Morgan",
                  "title": "Ms",
                  "address": {
                    "unstructuredAddress": {
                      "line1": "1 Test Street",
                      "line5": "London",
                      "postcode": "SW1A 1AA"
                    }
                  }
                },
                "licence": {
                  "type": "Full",
                  "status": "Valid"
                },
                "token": {
                  "validToDate": "2031-07-12"
                }
              },
              "hasErrors": false
            }
        """.trimIndent()

        private val CUSTOMER_SUMMARY = """
            {
              "customerResponse": {
                "customer": {
                  "customerId": "stub-customer-001",
                  "recordStatus": "Active",
                  "customerType": "Individual",
                  "suppressions": [],
                  "individualDetails": {
                    "firstNames": "Sarah",
                    "lastName": "Morgan",
                    "dateOfBirth": "1990-01-15"
                  }
                }
              },
              "vehicleResponse": [
                {
                  "registrationNumber": "AA19 AAA",
                  "vehicleId": 1,
                  "chassisVin": "WF0FXXGAJF1A00001",
                  "make": "FORD",
                  "model": "FIESTA",
                  "manufacturerVehicleType": "M1",
                  "typeApprovalVariant": "1",
                  "typeApprovalVersion": "1",
                  "typeApprovalCategory": "M1",
                  "engineNumber": "1234567",
                  "euroStatus": "EURO 6",
                  "dateOfFirstRegistration": "2019-03-01",
                  "taxedUntil": "2025-12-01",
                  "taxClass": "PRIVATE/LIGHT GOODS (PLG)",
                  "taxStatus": "Taxed",
                  "motExpiryDate": "2025-11-30",
                  "motStatus": "Valid",
                  "colour": "Blue",
                  "fuelType": "Petrol",
                  "maxNetPower": 74,
                  "bodyType": "SALOON",
                  "seatingCapacity": 5,
                  "standingCapacity": 0,
                  "autonomousVehicle": false,
                  "maxPermissibleMass": 1600,
                  "powerToWeightRatio": 0.0,
                  "roadFriendlySuspensionApplied": false,
                  "realDrivingEmissions": "RDE2",
                  "numberOfPreviousKeepers": 0,
                  "wheelplan": "2 AXLE RIGID BODY"
                },
                {
                  "registrationNumber": "BB20 BBB",
                  "vehicleId": 2,
                  "chassisVin": "VNKKTUD362A000002",
                  "make": "TOYOTA",
                  "model": "YARIS",
                  "manufacturerVehicleType": "M1",
                  "typeApprovalVariant": "1",
                  "typeApprovalVersion": "1",
                  "typeApprovalCategory": "M1",
                  "engineNumber": "7654321",
                  "euroStatus": "EURO 6",
                  "dateOfFirstRegistration": "2020-06-15",
                  "taxedUntil": "2026-06-01",
                  "taxClass": "PRIVATE/LIGHT GOODS (PLG)",
                  "taxStatus": "Untaxed",
                  "motExpiryDate": "2026-05-20",
                  "sornStart": "2026-05-20",
                  "motStatus": "Valid",
                  "colour": "Red",
                  "fuelType": "Petrol",
                  "maxNetPower": 52,
                  "bodyType": "HATCHBACK",
                  "seatingCapacity": 5,
                  "standingCapacity": 0,
                  "autonomousVehicle": false,
                  "maxPermissibleMass": 1415,
                  "powerToWeightRatio": 0.0,
                  "roadFriendlySuspensionApplied": false,
                  "realDrivingEmissions": "RDE2",
                  "numberOfPreviousKeepers": 1,
                  "wheelplan": "2 AXLE RIGID BODY"
                }
              ],
              "hasErrors": false
            }
        """.trimIndent()

        private val SINGLE_SHARE_CODE = """
            {
              "linkingId": "stub-linking-id",
              "shareCode": {
                "state": "valid",
                "tokenId": "stub-token-id",
                "token": "STUB1234",
                "drivingLicenceNumber": "MORGA753116SM9IJ",
                "driverId": "stub-driver-id",
                "documentReference": "stub-doc-ref",
                "created": "2026-01-01T00:00:00.000Z",
                "expiry": "2026-01-08T00:00:00.000Z"
              }
            }
        """.trimIndent()

        private val MULTI_SHARE_CODE = """
            {
              "linkingId": "stub-linking-id",
              "shareCodes": []
            }
        """.trimIndent()
    }
}
