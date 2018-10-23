package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.ProgressToken

data class ChooseProgressToken(val tokens: Set<ProgressToken>) : Action