package com.example.group1_projectwork;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class Fg_settingsPage extends Fragment {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private ImageView profileImageView;
    private Button logOutButton;
    private SQLiteHelper dbHelper;



    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    profileImageView.setImageURI(imageUri);
                    SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                    sharedViewModel.setProfileImageUri(imageUri);
                    // Save the URI to SharedPreferences for persistence
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("profile_image_uri", imageUri.toString()); // Save URI as a String
                    editor.apply();
                    Log.d("Fg_settingsPage", "Image selected: " + imageUri.toString());
                    // Call the method to update the profile image in MainScreen
                    ((MainScreen) getActivity()).changeProfileImage(imageUri);
                } else {
                    Log.d("Fg_settingsPage", "No image selected or error in selection");
                }
            }
    );

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "Storage permission is required to select an image", Toast.LENGTH_SHORT).show();
                    Log.d("Fg_settingsPage", "Storage permission denied");
                }
            }
    );

    public Fg_settingsPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile_settings, container, false);

        // Existing initialization code...
        profileImageView = view.findViewById(R.id.imageView2);
        logOutButton = view.findViewById(R.id.logOutButton);
        TextView settingsUsername = view.findViewById(R.id.settingsUsername);
        Switch themeSwitch = view.findViewById(R.id.themeSwitch);
        AppCompatButton helpButton = view.findViewById(R.id.helpButton);

        // Handle Theme Switch
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showComingSoonDialog(themeSwitch);
        });

        // Rest of the existing initialization code...
        dbHelper = new SQLiteHelper(requireContext());
        String username = dbHelper.getLoggedInUsername(requireContext());
        if (username != null) {
            settingsUsername.setText(username);
        } else {
            settingsUsername.setText("Guest");
        }

        helpButton.setOnClickListener(v -> showHelpDialog());
        profileImageView.setOnClickListener(v -> showImageOptionsDialog());
        logOutButton.setOnClickListener(v -> logOut());

        return view;
    }

    private void showImageOptionsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Profile Picture")
                .setMessage("Would you like to edit or keep your profile picture?")
                .setPositiveButton("Edit", (dialog, which) -> checkStoragePermission())
                .setNegativeButton("Keep", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        try {
            Log.d("Fg_settingsPage", "Opening gallery...");
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        } catch (Exception e) {
            Log.e("Fg_settingsPage", "Error opening gallery: ", e);
            Toast.makeText(requireContext(), "Error opening gallery", Toast.LENGTH_SHORT).show();
        }
    }

    public void logOut() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all user data
        editor.apply();

        Intent intent = new Intent(getActivity(), loginPageScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }

    private void showComingSoonDialog(Switch themeSwitch) {
        new AlertDialog.Builder(getContext())
                .setTitle("Coming Soon")
                .setMessage("This feature is not yet available.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    // Revert the Switch to its original state
                    themeSwitch.setChecked(false);
                })
                .setCancelable(false)
                .show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Help")
                .setMessage("This is the help section where you can find information about how to use the app. If you need assistance, please contact support. Tap on the 'Plus Button' on the mainPage to add the details required. Select 'Add' to confirm. Add a PDF if needed. Scroll downwards and select 'Log Out'.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())  // Dismiss the dialog when the button is clicked
                .setCancelable(true)  // Allow the user to dismiss the dialog by tapping outside
                .show();
    }


}
