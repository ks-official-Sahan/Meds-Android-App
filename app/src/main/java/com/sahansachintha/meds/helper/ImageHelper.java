package com.sahansachintha.meds.helper;

public class ImageHelper {
    private static volatile ImageHelper instance;

    private ImageHelper() {
    }

    public static synchronized ImageHelper getInstance() {
        if (instance == null) {
            instance = new ImageHelper();
        }
        return instance;
    }

    /* Process Selected Image using Glide */
//    public void processSelectedImage(Uri imageUri) {
//        try {
//            File savedFile = saveImageToStorage(imageUri);
//            //loadImageFromStorage(); // Load from storage for better performance
//        } catch (IOException e) {
//            Log.e("ImageSelection", "Failed to save image", e);
//        }
//    }
//
//    public File saveImageToStorage(Uri imageUri) throws IOException {
//        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds");
//        if (!directory.exists()) directory.mkdirs();
//
//        String fileName = "prescription_" + System.currentTimeMillis() + ".jpg";
//        File imageFile = new File(directory, fileName);
//
//        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
//             FileOutputStream fos = new FileOutputStream(imageFile)) {
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                fos.write(buffer, 0, bytesRead);
//            }
//            fos.flush();
//        }
//
//        return imageFile;
//    }

}
