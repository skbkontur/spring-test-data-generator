package ru.kontur.kinfra.kfixture.api

annotation class FixtureGeneratorMeta(
    val pathes: Array<String>,
    val defaultSize: Int = 10,
    val collectionLength: Int = 10,
    val mapSize: Int = 10
)