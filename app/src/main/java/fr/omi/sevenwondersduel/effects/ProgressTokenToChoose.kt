package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.ProgressToken

data class ProgressTokenToChoose(val tokens: Set<ProgressToken>) : PendingAction