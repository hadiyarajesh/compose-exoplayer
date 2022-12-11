package com.hadiyarajesh.compose_exoplayer.ui.component

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.hadiyarajesh.compose_exoplayer.R
import com.hadiyarajesh.compose_exoplayer.ui.theme.Purple200
import com.hadiyarajesh.compose_exoplayer.utility.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun MediaPlayer(
    modifier: Modifier = Modifier,
    title: String,
    uri: Uri,
    showBottomControl: Boolean = true
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val lifecycle = lifecycleOwner.lifecycle

    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var shouldShowControls by remember { mutableStateOf(true) }
    var totalDuration by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var bufferedPercentage by remember { mutableStateOf(0) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
            .setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
            .build()
            .apply {
                setMediaItem(
                    MediaItem.Builder()
                        .setUri(uri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setDisplayTitle(title)
                                .build()
                        )
                        .build()
                )
                prepare()
                playWhenReady = false
            }
    }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var playbackState by remember { mutableStateOf(exoPlayer.playbackState) }

    LaunchedEffect(isPlaying) {
        // Hide controls if video is playing
        shouldShowControls = !isPlaying
    }

    LaunchedEffect(playbackState) {
        // Show loading if player is buffering
        isLoading = playbackState == STATE_BUFFERING
    }

    if (isPlaying and showBottomControl) {
        LaunchedEffect(Unit) {
            while (true) {
                currentTime = exoPlayer.currentPosition
                delay(1.seconds / 30)
            }
        }
    }

    Box(modifier = modifier) {
        DisposableEffect(
            AndroidView(
                modifier = Modifier.clickable(enabled = !isLoading && !isError) {
                    if (isPlaying) {
                        // Only hide controls if video is not playing
                        shouldShowControls = shouldShowControls.not()
                        scope.launch {
                            delay(4000)
                            if (isPlaying) {
                                // Hide the controls automatically after 4 seconds if video playing
                                shouldShowControls = false
                            }
                        }
                    }
                },
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }
            )
        ) {
            val playerListener = object : Player.Listener {
                override fun onEvents(
                    player: Player,
                    events: Player.Events
                ) {
                    super.onEvents(player, events)
                    totalDuration = player.duration.coerceAtLeast(0L)
                    currentTime = player.currentPosition.coerceAtLeast(0L)
                    bufferedPercentage = player.bufferedPercentage
                    isPlaying = player.isPlaying
                    playbackState = player.playbackState
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.d(TAG, "onPlayerError: $error")
                    shouldShowControls = false
                    isError = true
                }
            }

            exoPlayer.addListener(playerListener)

            val lifecycleObserver = LifecycleEventObserver { owner, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        exoPlayer.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        if (isPlaying) {
                            exoPlayer.play()
                        }
                    }

                    else -> {}
                }
            }

            lifecycle.addObserver(lifecycleObserver)

            onDispose {
                exoPlayer.removeListener(playerListener)
                exoPlayer.release()
                lifecycle.removeObserver(lifecycleObserver)
            }
        }

        PlayerControls(
            modifier = Modifier.fillMaxSize(),
            isVisible = if (!isLoading && !isError) shouldShowControls else false,
            isPlaying = isPlaying,
            title = exoPlayer.mediaMetadata.displayTitle.toString(),
            playbackState = { playbackState },
            onReplayClick = { exoPlayer.seekBack() },
            onForwardClick = { exoPlayer.seekForward() },
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> {
                        // pause the video
                        exoPlayer.pause()
                    }

                    exoPlayer.isPlaying.not() && playbackState == STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> {
                        // play the video as it's already paused
                        exoPlayer.play()
                    }
                }
                isPlaying = isPlaying.not()
            },
            showBottomControl = showBottomControl,
            totalDuration = totalDuration,
            currentTime = currentTime,
            bufferedPercentage = bufferedPercentage,
            onSeekChanged = { timeMs: Float ->
                exoPlayer.seekTo(timeMs.toLong())
            }
        )

        if (isLoading) {
            LoadingProgressBar(modifier = Modifier.fillMaxSize())
        }

        if (isError) {
            ErrorItem(modifier = Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isPlaying: Boolean,
    title: String,
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    showBottomControl: Boolean,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    playbackState: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
//        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
        Box {
            TopControl(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(),
                title = title
            )

            CenterControls(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = isPlaying,
                onReplayClick = onReplayClick,
                onForwardClick = onForwardClick,
                onPauseToggle = onPauseToggle,
                playbackState = playbackState
            )

            if (showBottomControl) {
                BottomControls(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { fullHeight: Int -> fullHeight }
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { fullHeight: Int -> fullHeight }
                            )
                        ),
                    totalDuration = totalDuration,
                    currentTime = currentTime,
                    bufferedPercentage = bufferedPercentage,
                    onSeekChanged = onSeekChanged
                )
            }
        }
    }
}

@Composable
private fun TopControl(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    playbackState: () -> Int,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit
) {
    val playerState = remember(playbackState()) { playbackState() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
//        IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_replay_5_sec_outlined),
//                contentDescription = null
//            )
//        }

        IconButton(
            modifier = Modifier.size(48.dp),
            onClick = onPauseToggle
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = when {
                    isPlaying -> {
                        painterResource(id = R.drawable.ic_pause_outlined)
                    }

                    playerState == STATE_ENDED -> {
                        painterResource(id = R.drawable.ic_replay_outlined)
                    }

                    else -> {
                        painterResource(id = R.drawable.ic_play_outlined)
                    }
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background
            )
        }

//        IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_forward_5_sec_outlined),
//                contentDescription = null
//            )
//        }
    }
}

@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = bufferedPercentage.toFloat(),
                enabled = false,
                onValueChange = { /*Do nothing*/ },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = currentTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..totalDuration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Purple200,
                    activeTickColor = Purple200
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    text = currentTime.formatMinSec(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = "/")
                Text(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    text = totalDuration.formatMinSec(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

//            IconButton(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                onClick = {}
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_fullscreen_expand_outlined),
//                    contentDescription = null
//                )
//            }
        }
    }
}

private fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "00:00"
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
        )
    }
}
