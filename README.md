# PhotoSelector

本地图片/视频（可以根据时长过滤）选择器，兼容AndroidQ存储新规则

```kotlin
  PhotoSelectorEngine
      .create(this)
      .setShowCamera(true)
      .setSelectMultiple(true)
      .setLoadMediaType(0)
      .setLimitVideoDuration(30)
      .start()
```

```kotlin
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val photos=PhotoSelectorEngine.getResult(requestCode,data)
        }
    }
```

