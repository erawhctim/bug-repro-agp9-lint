package com.example.lint

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: LibrariesForLibs
  get() = this.extensions.getByName("libs") as LibrariesForLibs

internal val Project.versionCatalog
  get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")


