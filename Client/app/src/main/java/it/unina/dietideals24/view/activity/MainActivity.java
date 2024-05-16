package it.unina.dietideals24.view.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.analytics.FirebaseAnalytics;

import it.unina.dietideals24.R;
import it.unina.dietideals24.databinding.ActivityMainBinding;
import it.unina.dietideals24.enumerations.FragmentTagEnum;
import it.unina.dietideals24.pushnotifications.PushNotificationService;
import it.unina.dietideals24.utils.localstorage.BadgeVisibilityStatus;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import it.unina.dietideals24.utils.localstorage.TokenManagement;
import it.unina.dietideals24.view.fragment.AuctionFragment;
import it.unina.dietideals24.view.fragment.HomeFragment;
import it.unina.dietideals24.view.fragment.NotificationFragment;
import it.unina.dietideals24.view.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static BadgeDrawable badge;
    private ActivityMainBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static void setBadgeNotificationVisibility(boolean isVisible) {
        if (badge != null)
            badge.setVisible(isVisible);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName()) && service.pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        String redirect = getIntent().getStringExtra("redirect");
        if (redirect != null && redirect.equals("notificationFragment")) {
            if (TokenManagement.isExpired())
                backToLogin();
            else
                replaceFragment(new NotificationFragment(), FragmentTagEnum.NOTIFICATION);
        }
        else
            replaceFragment(new HomeFragment(), FragmentTagEnum.HOME);

        askNotificationPermission();
        startNotificationService();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new HomeFragment(), FragmentTagEnum.HOME);
            } else if (itemId == R.id.nav_auction) {
                replaceFragment(new AuctionFragment(), FragmentTagEnum.AUCTION);
            } else if (itemId == R.id.nav_add) {
                showCreateAuctionDialog();
            } else if (itemId == R.id.nav_notify) {
                replaceFragment(new NotificationFragment(), FragmentTagEnum.NOTIFICATION);
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment(), FragmentTagEnum.PROFILE);
            }

            return true;
        });

        setBadge();
        backButtonManagement();
    }

    private void backToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment, FragmentTagEnum fragmentTagEnum) {
        logNavbarButton(fragmentTagEnum);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragmentTagEnum.toString()).addToBackStack(fragmentTagEnum.toString()).commit();
    }

    private void backButtonManagement() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();

            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);

            if (backStackEntry.getName() == null) return;

            if (backStackEntry.getName().equals(FragmentTagEnum.HOME.toString())) {
                binding.bottomNavigation.getMenu().getItem(0).setChecked(true);
            } else if (backStackEntry.getName().equals(FragmentTagEnum.AUCTION.toString())) {
                binding.bottomNavigation.getMenu().getItem(1).setChecked(true);
            } else if (backStackEntry.getName().equals(FragmentTagEnum.NOTIFICATION.toString())) {
                binding.bottomNavigation.getMenu().getItem(3).setChecked(true);
            } else if (backStackEntry.getName().equals(FragmentTagEnum.PROFILE.toString())) {
                binding.bottomNavigation.getMenu().getItem(4).setChecked(true);
            }
        });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    private void startNotificationService() {
        if (!isServiceRunning(this, PushNotificationService.class)) {
            Intent intent = new Intent(this, PushNotificationService.class);
            intent.setAction(PushNotificationService.ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra("receiverId", LocalDietiUser.getLocalDietiUser(getApplicationContext()).getId());
            startForegroundService(intent);
        }
    }

    private synchronized void setBadge() {
        if (badge == null)
            badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notify);

        badge.setVisible(BadgeVisibilityStatus.getBadgeVisibilityStatus(getApplicationContext()));
    }

    private void showCreateAuctionDialog() {
        ConstraintLayout addAuctionConstraintLayout = findViewById(R.id.createAuctionConstraintLayout);
        View viewCreateAuctionDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.create_auction_dialog, addAuctionConstraintLayout);

        Button englishAuctionBtn = viewCreateAuctionDialog.findViewById(R.id.englishAuctionBtn);
        Button reverseAuctionBtn = viewCreateAuctionDialog.findViewById(R.id.downwardAuctionBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(viewCreateAuctionDialog);
        final AlertDialog alertDialog = builder.create();

        englishAuctionBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent createEnglishAuctionActivity = new Intent(getApplicationContext(), CreateEnglishAuctionActivity.class);
            startActivity(createEnglishAuctionActivity);
        });

        reverseAuctionBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent createDownwardAuctionActivity = new Intent(getApplicationContext(), CreateDownwardAuctionActivity.class);
            startActivity(createDownwardAuctionActivity);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setOnDismissListener(v -> {
            alertDialog.dismiss();
            resetNavbarPosition();
        });

        alertDialog.show();
    }

    private void resetNavbarPosition() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof HomeFragment) {
            binding.bottomNavigation.getMenu().getItem(0).setChecked(true);
        } else if (currentFragment instanceof AuctionFragment) {
            binding.bottomNavigation.getMenu().getItem(1).setChecked(true);
        } else if (currentFragment instanceof NotificationFragment) {
            binding.bottomNavigation.getMenu().getItem(3).setChecked(true);
        } else if (currentFragment instanceof ProfileFragment) {
            binding.bottomNavigation.getMenu().getItem(4).setChecked(true);
        }
    }

    private void logNavbarButton(FragmentTagEnum fragmentTagEnum) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, fragmentTagEnum.toString());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, fragmentTagEnum + " button");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Navbar" + fragmentTagEnum + "button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBadge();
    }
}