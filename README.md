# PhotoSelector

本地图片/视频（可以根据时长过滤）选择器，兼容AndroidQ存储新规则

```kotlin
  PhotoSelectorEngine
      .create(this)
      .setShowCamera(true)
      .setSelectMultiple(true)
      .setLoadMediaType(0)
      .setLimitVideoDuration(30)
      .start(PhotoSelectorEngine.REQUEST_PHOTO_CODE)
```

```kotlin
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK&&requestCode==PhotoSelectorEngine.REQUEST_PHOTO_CODE) {
            val photos=PhotoSelectorEngine.getResult(data)
        }
    }
```
直接调用拍照
```kotlin
PhotoSelectorEngine
    .create(this)
    .takePhoto(PhotoSelectorEngine.TAKE_PHOTO)
```

```
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK&&requestCode==PhotoSelectorEngine.TAKE_PHOTO) {
            val photo=PhotoSelectorEngine.getResult()
        }
    }
  ```
