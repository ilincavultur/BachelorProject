package com.example.digitaldiaryba.data.models.responses.dbpedia

data class Results(
    val bindings: List<Binding>,
    val distinct: Boolean,
    val ordered: Boolean
)