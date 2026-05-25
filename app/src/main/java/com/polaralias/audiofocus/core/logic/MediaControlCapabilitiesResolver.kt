package com.polaralias.audiofocus.core.logic

import android.media.session.PlaybackState
import javax.inject.Inject
import javax.inject.Singleton

data class MediaControlCapabilities(
    val canPlay: Boolean,
    val canPause: Boolean,
    val canSeek: Boolean,
    val canSkipForward: Boolean,
    val canSkipBackward: Boolean
)

@Singleton
class MediaControlCapabilitiesResolver @Inject constructor() {
    fun resolve(actions: Long): MediaControlCapabilities {
        return MediaControlCapabilities(
            canPlay = hasAction(actions, PlaybackState.ACTION_PLAY),
            canPause = hasAction(actions, PlaybackState.ACTION_PAUSE),
            canSeek = hasAction(actions, PlaybackState.ACTION_SEEK_TO),
            canSkipForward = hasAnyAction(
                actions,
                PlaybackState.ACTION_SEEK_TO,
                PlaybackState.ACTION_SKIP_TO_NEXT,
                PlaybackState.ACTION_FAST_FORWARD
            ),
            canSkipBackward = hasAnyAction(
                actions,
                PlaybackState.ACTION_SEEK_TO,
                PlaybackState.ACTION_SKIP_TO_PREVIOUS,
                PlaybackState.ACTION_REWIND
            )
        )
    }

    private fun hasAnyAction(actions: Long, vararg requiredActions: Long): Boolean {
        return requiredActions.any { hasAction(actions, it) }
    }

    private fun hasAction(actions: Long, requiredAction: Long): Boolean {
        return actions and requiredAction != 0L
    }
}
