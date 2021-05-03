package com.tedilabs.voca.model

import com.github.zafarkhaja.semver.Version

data class AppVersionStatus(
    private val currentVersion: Version,
    private val latestVersion: Version,
    private val versionRequirement: String
) {
    val updateAvailable: Boolean
        get() = currentVersion.lessThan(latestVersion)

    val updateRequired: Boolean
        get() = !currentVersion.satisfies(versionRequirement)
}
