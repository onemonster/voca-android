package com.tedilabs.voca.model

import com.github.zafarkhaja.semver.Version
import com.tedilabs.voca.BuildConfig

data class AppVersionStatus(
    private val latestVersion: Version,
    private val versionRequirement: String
) {
    private val currentVersion = Version.valueOf(BuildConfig.VERSION_NAME)

    val updateAvailable: Boolean
        get() = currentVersion.lessThan(latestVersion)

    val updateRequired: Boolean
        get() = !currentVersion.satisfies(versionRequirement)
}
