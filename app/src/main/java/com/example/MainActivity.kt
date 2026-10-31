package com.example

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          VideoDownloaderScreen(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

data class DownloadFormat(val id: String, val type: String, val quality: String, val size: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDownloaderScreen(modifier: Modifier = Modifier) {
  var url by remember { mutableStateOf("") }
  var videoTitle by remember { mutableStateOf("YouTube Video") }
  var isVideoLoaded by remember { mutableStateOf(false) }
  var selectedFormatId by remember { mutableStateOf("vid_1080") }
  val context = LocalContext.current

  BackHandler(enabled = isVideoLoaded) {
    isVideoLoaded = false
  }

  val videoFormats = listOf(
    DownloadFormat("vid_1080", "Video", "1080p HD", "45.2 MB"),
    DownloadFormat("vid_720", "Video", "720p", "22.1 MB"),
    DownloadFormat("vid_480", "Video", "480p", "12.5 MB")
  )
  
  val audioFormats = listOf(
    DownloadFormat("aud_320", "Audio (MP3)", "320 kbps", "8.4 MB"),
    DownloadFormat("aud_128", "Audio (MP3)", "128 kbps", "3.2 MB")
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.Top
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isVideoLoaded) {
          IconButton(onClick = { isVideoLoaded = false }) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onBackground
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
        } else {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "App Icon",
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
          text = "TubeGet Pro",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
          fontWeight = FontWeight.Bold
        )
      }
      Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "Profile",
        modifier = Modifier
          .size(40.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
          .padding(8.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Search Bar Removed


    if (isVideoLoaded) {
      Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
        // Video Preview Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Thumbnail mock
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(16f / 9f)
              .clip(RoundedCornerShape(16.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
             Icon(
               imageVector = Icons.Default.PlayCircle,
               contentDescription = "Play Video",
               tint = Color.White,
               modifier = Modifier.size(64.dp)
             )
          }
          
          Spacer(modifier = Modifier.height(16.dp))
          
          Text(
            text = videoTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          
          Spacer(modifier = Modifier.height(4.dp))
          
          Text(
            text = "Nature Channel • 1.2M views • 08:24",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Select Video Quality",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        videoFormats.forEach { format ->
          FormatOptionCard(
            format = format,
            isSelected = selectedFormatId == format.id,
            icon = Icons.Default.VideoLibrary,
            onClick = { selectedFormatId = format.id }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Select MP3 Quality",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        audioFormats.forEach { format ->
          FormatOptionCard(
            format = format,
            isSelected = selectedFormatId == format.id,
            icon = Icons.Default.AudioFile,
            onClick = { selectedFormatId = format.id }
          )
        }
      }
      
      Spacer(modifier = Modifier.height(32.dp))

      Button(
        onClick = {
          downloadVideo(context, url, selectedFormatId)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("download_button"),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        val selectedFormatName = (videoFormats + audioFormats).find { it.id == selectedFormatId }?.quality ?: ""
        Icon(imageVector = Icons.Default.Download, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Download ($selectedFormatName)", style = MaterialTheme.typography.titleMedium)
      }
      }
    } else {
      // Empty state - YouTube WebView
      var webViewRef by remember { mutableStateOf<WebView?>(null) }

      BackHandler(enabled = !isVideoLoaded && webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(16.dp))
      ) {
        androidx.compose.ui.viewinterop.AndroidView(
          factory = { ctx ->
            WebView(ctx).apply {
              // Enable cookies and third-party cookies for streaming tokens
              val cookieManager = CookieManager.getInstance()
              cookieManager.setAcceptCookie(true)
              cookieManager.setAcceptThirdPartyCookies(this, true)

              // Use standard mobile Chrome user agent so YouTube does not block HTML5 video playback
              val chromeUa = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
              settings.apply {
                userAgentString = chromeUa
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
              }

              webChromeClient = object : WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap? {
                  return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                }
              }

              webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                  val reqUrl = request?.url?.toString() ?: return false
                  if (reqUrl.startsWith("intent://") || reqUrl.startsWith("vnd.youtube:") || reqUrl.startsWith("market://")) {
                    return true
                  }
                  return false
                }

                override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                  return true
                }
              }
              webViewRef = this
              loadUrl("https://m.youtube.com")
            }
          },
          modifier = Modifier.fillMaxSize()
        )
        FloatingActionButton(
          onClick = {
            url = webViewRef?.url ?: url
            val pageTitle = webViewRef?.title?.replace(" - YouTube", "")?.trim()
            if (!pageTitle.isNullOrBlank()) {
              videoTitle = pageTitle
            }
            isVideoLoaded = true
          },
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
          Icon(imageVector = Icons.Default.Download, contentDescription = "Download Video")
        }
      }
    }
  }
}

@Composable
fun FormatOptionCard(
  format: DownloadFormat,
  isSelected: Boolean,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
  val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
  
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(containerColor)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = format.quality,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = contentColor
      )
    }
    Text(
      text = format.size,
      style = MaterialTheme.typography.bodySmall,
      color = contentColor.copy(alpha = 0.8f)
    )
  }
}

fun downloadVideo(context: Context, url: String, formatId: String) {
  try {
    val extension = if (formatId.startsWith("aud")) ".mp3" else ".mp4"
    val request = DownloadManager.Request(Uri.parse(url))
      .setTitle("Downloading Media")
      .setDescription("Format: $formatId")
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
      .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "download_${System.currentTimeMillis()}$extension")
      .setAllowedOverMetered(true)
      .setAllowedOverRoaming(true)

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)

    Toast.makeText(context, "Download started... Check notifications.", Toast.LENGTH_SHORT).show()
  } catch (e: Exception) {
    Toast.makeText(context, "Error: Invalid URL or format", Toast.LENGTH_SHORT).show()
  }
}
