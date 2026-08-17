package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel

internal data class VehicleDetailsUiModel(
    val make: String,
    val model: String,
    val registration: String,
    val keeper: KeeperUiModel,
    val specificationsIcons: List<SpecificationIconUiModel>,
    val taxStatus: StatusUiModel,
    val motStatus: StatusUiModel,
    val specifications: List<InternalLinkListItemModel>
)
