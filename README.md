# test_loadImage

Following are the File class path APIs with this usability:

1. String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

This will provide Suitable directory like Emulated/0/Pictures
Will not be deleted after app uninstalls.

2. String s = Environment.getExternalStorageDirectory().getAbsolutePath();

This will provide you default path like Emulated/0/

3. String s = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

THis will provide you app specific path like 0/Android/data/com.example.I20035.grayscale/files/pictures

4. String s = this.getExternalFilesDir(null).getAbsolutePath();

This will provide default path for External memory Emulated/0/
