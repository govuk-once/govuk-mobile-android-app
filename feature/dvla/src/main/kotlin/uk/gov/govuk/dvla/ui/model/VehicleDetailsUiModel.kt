package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationUiModel

data class VehicleDetailsUiModel(
    val make: String,
    val model: String,
    val registration: String,
    val keeper: KeeperUiModel,
    val specifications: List<SpecificationUiModel>,
    val taxStatus: StatusRowUiModel,
    val motStatus: StatusRowUiModel,
    val extraDetails: List<InternalLinkListItemModel>
)
