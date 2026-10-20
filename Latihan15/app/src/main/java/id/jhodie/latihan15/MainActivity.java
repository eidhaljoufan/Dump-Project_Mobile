package id.jhodie.latihan15;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocationName, tvCoordinates, tvTemperature, tvWeather;
    private Button btnRefresh;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Geocoder geocoder;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String API_KEY = "ee12943f3814f5ebe81b6ccb314e84ac";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocationName = findViewById(R.id.tv_location_name);
        tvCoordinates = findViewById(R.id.tv_coordinates);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvWeather = findViewById(R.id.tv_weather);
        btnRefresh = findViewById(R.id.btn_refresh);

        geocoder = new Geocoder(this, new Locale("id", "ID"));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnRefresh.setOnClickListener(v -> {
            if (latitude == 0.0 && longitude == 0.0) {
                ambilLokasi();
            } else {
                ambilDataCuaca();
            }
        });

        cekIzinLokasi();
    }

    private void cekIzinLokasi() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            ambilLokasi();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void ambilLokasi() {
        tvLocationName.setText("Lokasi: Mengambil lokasi...");
        tvCoordinates.setText("Koordinat: Mengambil data...");
        tvTemperature.setText("Suhu: -");
        tvWeather.setText("Cuaca: -");

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            cekIzinLokasi();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        prosesLokasi(location);
                    } else {
                        mintaUpdateLokasiBaru();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Gagal mengambil lokasi: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void mintaUpdateLokasiBaru() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if (location != null) {
                    prosesLokasi(location);
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Lokasi belum tersedia. Pastikan GPS aktif.",
                            Toast.LENGTH_LONG
                    ).show();
                }

                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void prosesLokasi(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        tvCoordinates.setText(
                "Koordinat: " + String.format(
                        Locale.US,
                        "%.4f, %.4f",
                        latitude,
                        longitude
                )
        );

        ambilNamaLokasi();
        ambilDataCuaca();
    }

    private void ambilNamaLokasi() {
        executorService.execute(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                String locationName = "Lokasi tidak dikenal";

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    if (address.getSubLocality() != null) {
                        locationName = address.getSubLocality();
                    } else if (address.getLocality() != null) {
                        locationName = address.getLocality();
                    } else if (address.getSubAdminArea() != null) {
                        locationName = address.getSubAdminArea();
                    } else if (address.getAdminArea() != null) {
                        locationName = address.getAdminArea();
                    }
                }

                String finalLocationName = locationName;

                runOnUiThread(() -> tvLocationName.setText("Lokasi: " + finalLocationName));

            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvLocationName.setText("Lokasi: Gagal memuat");
                    Toast.makeText(
                            MainActivity.this,
                            "Gagal mendapatkan nama lokasi.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private void ambilDataCuaca() {
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(
                    this,
                    "Lokasi belum tersedia. Coba perbarui lagi.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        tvTemperature.setText("Suhu: Memuat...");
        tvWeather.setText("Cuaca: Memuat...");

        executorService.execute(() -> {
            HttpURLConnection connection = null;

            try {
                String urlString =
                        "https://api.openweathermap.org/data/2.5/weather"
                                + "?lat=" + latitude
                                + "&lon=" + longitude
                                + "&appid=" + API_KEY
                                + "&units=metric"
                                + "&lang=id";

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream())
                    );

                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;

                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }

                    errorReader.close();

                    runOnUiThread(() -> Toast.makeText(
                            MainActivity.this,
                            "Gagal mengambil data cuaca. Kode: " + responseCode,
                            Toast.LENGTH_LONG
                    ).show());

                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());

                JSONObject main = jsonObject.getJSONObject("main");
                double temp = main.getDouble("temp");

                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String weatherDesc = weatherObject.getString("description");

                runOnUiThread(() -> {
                    tvTemperature.setText("Suhu: " + String.format(Locale.US, "%.1f", temp) + "°C");
                    tvWeather.setText("Cuaca: " + kapitalAwal(weatherDesc));
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvTemperature.setText("Suhu: -");
                    tvWeather.setText("Cuaca: -");

                    Toast.makeText(
                            MainActivity.this,
                            "Gagal mengambil data cuaca: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String kapitalAwal(String text) {
        if (text == null || text.isEmpty()) {
            return "-";
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ambilLokasi();
            } else {
                Toast.makeText(
                        this,
                        "Izin lokasi ditolak. Aplikasi tidak dapat menampilkan cuaca.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}