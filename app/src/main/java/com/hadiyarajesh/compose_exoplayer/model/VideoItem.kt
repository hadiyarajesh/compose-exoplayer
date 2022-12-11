package com.hadiyarajesh.compose_exoplayer.model

data class VideoItem(
    val title: String,
    val description: String,
    val thumbUrl: String,
    val videoUrl: String
)

// Data collected from https://gist.github.com/jsturgis/3b19447b304616f18657
val videos = listOf(
    VideoItem(
        title = "Big Buck Bunny",
        description = "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.\\n\\nLicensed under the Creative Commons Attribution license\\nhttp://www.bigbuckbunny.org",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    ),
    VideoItem(
        title = "Elephant Dream",
        description = "The first Blender Open Movie from 2006",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    ),
    VideoItem(
        title = "For Bigger Blazes",
        description = "HBO GO now works with Chromecast -- the easiest way to enjoy online video on your TV. For when you want to settle into your Iron Throne to watch the latest episodes. For \$35.\\nLearn how to use Chromecast with HBO GO and more at google.com/chromecast.",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    ),
    VideoItem(
        title = "For Bigger Escape",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for when Batman's escapes aren't quite big enough. For \$35. Learn how to use Chromecast with Google Play Movies and more at google.com/chromecast.",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    ),
    VideoItem(
        title = "For Bigger Fun",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For \$35.  Find out more at google.com/chromecast.",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
    ), VideoItem(
        title = "For Bigger Joyrides",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for the times that call for bigger joyrides. For \$35. Learn how to use Chromecast with YouTube and more at google.com/chromecast.",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
    ),
    VideoItem(
        title = "For Bigger Meltdowns",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for when you want to make Buster's big meltdowns even bigger. For \$35. Learn how to use Chromecast with Netflix and more at google.com/chromecast.",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
        videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
    )
)
