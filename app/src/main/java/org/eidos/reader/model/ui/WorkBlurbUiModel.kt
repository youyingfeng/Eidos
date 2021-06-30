package org.eidos.reader.model.ui

import org.eidos.reader.model.domain.WorkBlurb

sealed class WorkBlurbUiModel {
    data class WorkBlurbItem(val workBlurb: WorkBlurb): WorkBlurbUiModel()
    data class SeparatorItem(val description: String): WorkBlurbUiModel()
}
