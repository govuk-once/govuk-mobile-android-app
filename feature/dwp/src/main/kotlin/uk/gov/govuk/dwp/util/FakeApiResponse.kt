package uk.gov.govuk.dwp.util

import com.google.gson.Gson
import uk.gov.govuk.dwp.data.remote.model.AwardDetailsResponse
import uk.gov.govuk.dwp.data.remote.model.PaymentsQueryResponse

fun latestUpcomingPayments(): AwardDetailsResponse {
    val json = """
        {
            "statusCode": 200,
            "status": "success",
            "data": {
                "ame_last_issued": {
                    "statusCode": 200,
                    "status": "success",
                    "data": [
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitShortName": "PIP",
                                    "benefitName": "Personal Independence Payment",
                                    "amount": 1000.5,
                                    "benefitCode": 5100
                                },
                                {
                                    "benefitShortName": "PIP",
                                    "benefitName": "Personal Independence Payment",
                                    "amount": 999.5,
                                    "benefitCode": 5100
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 3088,
                            "deductionValue": 0.5,
                            "benefitCodes": [
                                5100
                            ],
                            "amount": 2000,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4100,
                                    "benefitShortName": "AA",
                                    "benefitName": "Attendance Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 4909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4100
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "PC",
                                    "benefitName": "Pension Credit",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 1239,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 2300,
                                    "benefitShortName": "IS",
                                    "benefitName": "Income Support",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 3211,
                            "deductionValue": 100,
                            "benefitCodes": [
                                2300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4300,
                                    "benefitShortName": "JSA",
                                    "benefitName": "Jobseeker's Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 8909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4300,
                                    "benefitShortName": "IB",
                                    "benefitName": "Incapacity Benefit",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 8909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        }
                    ]
                },
                "ame_upcoming": {
                    "statusCode": 200,
                    "status": "success",
                    "data": [
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4000,
                                    "benefitShortName": "DLA",
                                    "benefitName": "Disability Living Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 4000,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4000
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5200,
                                    "benefitShortName": "SP",
                                    "benefitName": "State Pension",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 9890,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5200
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "ESA",
                                    "benefitName": "Employment and Support Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 5455,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "CA",
                                    "benefitName": "Carer's Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 5455,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        }
                    ]
                },
                "sas_award_details": {
                    "AA": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "attendance_allowance",
                            "award_start_date": "1995-01-09",
                            "payment_frequency": null,
                            "award_amount_breakdown": [
                                {
                                    "name": "care",
                                    "amount": 250,
                                    "rate": "high",
                                    "startDate": "2018-01-01"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "DLA": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "disability_living_allowance",
                            "award_start_date": "2000-09-28",
                            "award_end_date": null,
                            "award_type": null,
                            "payment_frequency": null,
                            "award_amount": null,
                            "deduction_amount": null,
                            "award_amount_breakdown": [
                                {
                                    "name": "mobility",
                                    "amount": 990,
                                    "rate": "high",
                                    "startDate": "2000-09-28",
                                    "endDate": null
                                },
                                {
                                    "name": "care",
                                    "amount": 990,
                                    "rate": "medium",
                                    "startDate": "2000-09-28",
                                    "endDate": "2026-09-01"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "ESA": {
                        "statusCode": 404,
                        "status": "failure",
                        "message": "Request failed with status code 404"
                    },
                    "PIP": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "personal_independence_payment",
                            "award_start_date": "0000-00-00 [yyyy-mm-dd].",
                            "award_end_date": "0000-00-00 [yyyy-mm-dd].",
                            "award_type": "other",
                            "payment_frequency": null,
                            "award_amount": 2000,
                            "deduction_amount": 0.5,
                            "award_amount_breakdown": [
                                {
                                    "name": "mobility",
                                    "amount": 1999.5,
                                    "rate": "standard",
                                    "startDate": "2020-05-02",
                                    "endDate": "2029-12-02"
                                },
                                {
                                    "name": "daily_living",
                                    "amount": 1999.5,
                                    "rate": "enhanced",
                                    "startDate": "2019-06-06"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "SP": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "IS": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "JSA": {
                        "statusCode": 404,
                        "status": "failure",
                        "message": "Request failed with status code 404"
                    },
                    "PC": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "IB": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "SDA": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "CA": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    }
                }
            }
        }        
    """.trimIndent()

    return Gson().fromJson(json, AwardDetailsResponse::class.java)
}

fun historicalPayments(): PaymentsQueryResponse {
    val json = """
        {
            "statusCode": 200,
            "status": "success",
            "data": [
                {
                    "benefitShortName": "DLA",
                    "benefitName": "Disability Living Allowance",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 4000,
                    "status": "Issued",
                    "dueDate": "2026-10-12",
                    "translatedDueDate": "12 October 2026"
                },
                {
                    "benefitShortName": "SP",
                    "benefitName": "State Pension",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 9890,
                    "status": "Issued",
                    "dueDate": "2026-10-11",
                    "translatedDueDate": "11 October 2026"
                },
                {
                    "benefitShortName": "DLA",
                    "benefitName": "Disability Living Allowance",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 4000,
                    "status": "Issued",
                    "dueDate": "2026-09-22",
                    "translatedDueDate": "22 September 2026"
                },
                {
                    "benefitShortName": "SP",
                    "benefitName": "State Pension",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 9890,
                    "status": "Issued",
                    "dueDate": "2026-09-12",
                    "translatedDueDate": "12 September 2026"
                },
                {
                    "benefitShortName": "AA",
                    "benefitName": "Attendance Allowance",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 2590,
                    "status": "Issued",
                    "dueDate": "2026-09-11",
                    "translatedDueDate": "11 September 2026"
                },
                {
                    "benefitShortName": "CA",
                    "benefitName": "Carer's Allowance",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 3500,
                    "status": "Issued",
                    "dueDate": "2026-08-15",
                    "translatedDueDate": "15 August 2026"
                },
                {
                    "benefitShortName": "IB",
                    "benefitName": "Incapacity Benefit",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 5200,
                    "status": "Issued",
                    "dueDate": "2026-08-13",
                    "translatedDueDate": "13 August 2026"
                },
                {
                    "benefitShortName": "AA",
                    "benefitName": "Attendance Allowance",
                    "awardamount": 500,
                    "deductionamount": 100,
                    "amount": 2590,
                    "status": "Issued",
                    "dueDate": "2026-08-12",
                    "translatedDueDate": "12 August 2026"
                }
            ]
        }
    """.trimIndent()

    return Gson().fromJson(json, PaymentsQueryResponse::class.java)
}

fun benefitFrequencyContentItem(): Map<String, String> {
    // TODO: Content item: https://www.gov.uk/api/content/how-to-have-your-benefits-paid

    val data = """
        {
            "details": {
                "body": "\u003Cp\u003EBenefits are usually paid straight into your bank, building society or credit union account.\u003C/p\u003E\n\n\u003Cp\u003EIf your payment date is on a weekend or a bank holiday you’ll usually be paid on the working day before. This may be different for \u003Ca href=\"/child-benefit-payment-dates\"\u003EChild Benefit\u003C/a\u003E.\u003C/p\u003E\n\n\u003Cdiv role=\"note\" aria-label=\"Information\" class=\"application-notice info-notice\"\u003E\n  \u003Cp\u003EThis guide is also available in \u003Ca href=\"/sut-a-phryd-caiff-eich-budd-daliadau-eu-talu\"\u003EWelsh (Cymraeg)\u003C/a\u003E.\u003C/p\u003E\n\u003C/div\u003E\n\n\u003Ch2 id=\"how-often-youre-paid\"\u003EHow often you’re paid\u003C/h2\u003E\n\n\u003Ctable\u003E\n  \u003Cthead\u003E\n    \u003Ctr\u003E\n      \u003Ctd\u003E\u003C/td\u003E\n      \u003Cth scope=\"col\"\u003EHow often it’s paid\u003C/th\u003E\n    \u003C/tr\u003E\n  \u003C/thead\u003E\n  \u003Ctbody\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EAttendance Allowance\u003C/th\u003E\n      \u003Ctd\u003EUsually every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003ECarer's Allowance\u003C/th\u003E\n      \u003Ctd\u003EWeekly in advance or every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EChild Benefit\u003C/th\u003E\n      \u003Ctd\u003E\n\u003Ca href=\"/child-benefit-payment-dates\"\u003EUsually every 4 weeks\u003C/a\u003E - or weekly if you’re a single parent or you or your partner get certain benefits.\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EDisability Living Allowance\u003C/th\u003E\n      \u003Ctd\u003EUsually every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EEmployment and Support Allowance\u003C/th\u003E\n      \u003Ctd\u003EUsually every 2 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EJobseeker's Allowance\u003C/th\u003E\n      \u003Ctd\u003EUsually every 2 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EMaternity Allowance\u003C/th\u003E\n      \u003Ctd\u003EEvery 2 or 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EPension Credit\u003C/th\u003E\n      \u003Ctd\u003EUsually every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EPersonal Independence Payment\u003C/th\u003E\n      \u003Ctd\u003EUsually every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EState Pension\u003C/th\u003E\n      \u003Ctd\u003EUsually every 4 weeks\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n      \u003Cth scope=\"row\"\u003EUniversal Credit\u003C/th\u003E\n      \u003Ctd\u003EEvery month\u003C/td\u003E\n    \u003C/tr\u003E\n  \u003C/tbody\u003E\n\u003C/table\u003E\n\n\u003Ch2 id=\"how-your-benefits-are-paid\"\u003EHow your benefits are paid\u003C/h2\u003E\n\n\u003Cp\u003EYou’ll be asked for bank, building society or credit union account details when you claim. You can only get paid in a different way if you have problems opening or managing an account.\u003C/p\u003E\n\n\u003Ch3 id=\"if-you-cannot-open-or-manage-a-bank-building-society-or-credit-union-account\"\u003EIf you cannot open or manage a bank, building society or credit union account\u003C/h3\u003E\n\n\u003Cp\u003EContact the office that pays your benefit to find out how to get it paid.\u003C/p\u003E\n\n\u003Cp\u003EIf you receive Universal Credit, call the \u003Ca href=\"/universal-credit/contact-universal-credit\"\u003EUniversal Credit helpline\u003C/a\u003E.\u003C/p\u003E\n\n\u003Ch2 id=\"paying-back-benefits\"\u003EPaying back benefits\u003C/h2\u003E\n\n\u003Cp\u003EYou can repay benefits and allowances you get but feel you do not need.\u003C/p\u003E\n\n\u003Cp\u003EWrite to the department that paid the benefit. Their address will be on any letter you’ve received from them.\u003C/p\u003E\n\n\u003Cp\u003EInclude a cheque made payable to the department, along with:\u003C/p\u003E\n\n\u003Cul\u003E\n  \u003Cli\u003Eyour National Insurance number\u003C/li\u003E\n  \u003Cli\u003Edetails of the payment, such as the date and the amount\u003C/li\u003E\n\u003C/ul\u003E\n\n\u003Cdiv role=\"note\" aria-label=\"Information\" class=\"application-notice info-notice\"\u003E\n  \u003Cp\u003EYou can only repay Child Benefit if you’ve been overpaid.\u003C/p\u003E\n\u003C/div\u003E\n\n",
            }
        }
    """.trimIndent()

    val decoded = data
        .replace("\\u003C", "<")
        .replace("\\u003E", ">")
        .replace("\\\"", "\"")
        .replace("\\n", "\n")

    val regex = Regex("""<th scope="row">(.+?)</th>\s*<td>(.*?)</td>""", RegexOption.DOT_MATCHES_ALL)
    val benefits = regex.findAll(decoded).associate { match ->
        match.groupValues[1].replace(Regex("<[^>]+>"), "").trim() to
            match.groupValues[2].replace(Regex("<[^>]+>"), "").trim()
    }

    return benefits
}
