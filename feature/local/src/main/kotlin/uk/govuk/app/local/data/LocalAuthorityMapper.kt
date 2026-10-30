package uk.govuk.app.local.data

import uk.govuk.app.local.data.local.model.LocalAuthorityEntity
import uk.govuk.app.local.data.local.model.LocalAuthorityParentEntity
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.domain.model.Address
import uk.govuk.app.local.domain.model.LocalAuthority

internal fun RemoteLocalAuthority.toLocalAuthority(): LocalAuthority {
    return LocalAuthority(
        name = this.name,
        url = this.homePageUrl,
        slug = this.slug,
        parent = this.parent?.let { parent ->
            LocalAuthority(
                name = parent.name,
                url = parent.homePageUrl,
                slug = parent.slug
            )
        }
    )
}

internal fun LocalAuthorityEntity.toLocalAuthority(): LocalAuthority {
    return LocalAuthority(
        name = this.name,
        url = this.url,
        slug = this.slug,
        parent = this.parent?.let {
            LocalAuthority(
                name = it.name,
                url = it.url,
                slug = it.slug
            )
        }
    )
}

internal fun RemoteAddress.toAddress(): Address {
    return Address(
        address = this.address,
        slug = this.slug,
        name = this.name
    )
}
