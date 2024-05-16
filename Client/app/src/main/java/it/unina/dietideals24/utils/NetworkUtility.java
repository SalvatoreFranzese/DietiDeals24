package it.unina.dietideals24.utils;

import android.content.Context;
import android.widget.Toast;

public class NetworkUtility {
    private NetworkUtility() {
    }

    public static synchronized void showNetworkErrorToast(Context context) {
        Toast.makeText(context, "Connessione non disponibile, riprova più tardi", Toast.LENGTH_SHORT).show();
    }
}
