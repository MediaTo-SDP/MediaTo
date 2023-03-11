package com.github.sdp.mediato.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class PhotoPicker {

  private final Fragment fragment;
  private final ImageView imageView;
  private Uri profileImageUri;

  public PhotoPicker(@NonNull Fragment fragment, @NonNull ImageView imageView) {
    this.fragment = fragment;
    this.imageView = imageView;
  }

  public View.OnClickListener getOnClickListener(ActivityResultRegistry activityResultRegistry) {
    ActivityResultLauncher<Intent> photoPickerResult = registerForPhotoPickerResult(activityResultRegistry);
    return v -> ImagePicker.Companion.with(fragment)
        .crop()
        .cropSquare()
        .compress(1024)
        .maxResultSize(620, 620)
        .createIntent( intent -> {
          photoPickerResult.launch(intent);
          return null;
        });
  }

  private ActivityResultLauncher<Intent> registerForPhotoPickerResult(ActivityResultRegistry activityResultRegistry) {
    return activityResultRegistry.register("photoPickerResult", new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            handlePhotoPickerResult(result.getResultCode(), result.getData());
          }
        });
  }

  private void handlePhotoPickerResult(int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      profileImageUri = data.getData();
      imageView.setImageURI(profileImageUri);
    } else if (resultCode == ImagePicker.RESULT_ERROR) {
      Toast.makeText(fragment.getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(fragment.getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
    }
  }

}
