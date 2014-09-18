package ru.zzsdeo.mymoneybalance;
import android.app.Application;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes (
                    formKey = "", // This is required for backward compatibility but not used
                    formUri = "http://acra.mamarada.su/myMoneyBalance/acra.php",
                    formUriBasicAuthLogin = "admin",
                    formUriBasicAuthPassword = "yyuzypuv"
                )
public class Acra extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}