package id.jhodie.tugas19;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Resep {
    private String namaResep;
    private List<String> daftarBahan;
    private List<Langkah> langkah;
    private Kategori kategori;
    private double rating;

    public Resep() {
        this.daftarBahan = new ArrayList<>();
        this.langkah = new ArrayList<>();
    }

    public Resep(String namaResep, List<String> daftarBahan, List<Langkah> langkah, Kategori kategori, double rating) {
        this.namaResep = namaResep;
        this.daftarBahan = daftarBahan;
        this.langkah = langkah;
        this.kategori = kategori;
        this.rating = rating;
    }

    // Getter dan Setter
    public String getNamaResep() {
        return namaResep;
    }

    public void setNamaResep(String namaResep) {
        this.namaResep = namaResep;
    }

    public List<String> getDaftarBahan() {
        return daftarBahan;
    }

    public void setDaftarBahan(List<String> daftarBahan) {
        this.daftarBahan = daftarBahan;
    }

    public List<Langkah> getLangkah() {
        return langkah;
    }

    public void setLangkah(List<Langkah> langkah) {
        this.langkah = langkah;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Konversi ke JSONObject
    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("namaResep", namaResep);

            JSONArray bahanArray = new JSONArray();
            for (String bahan : daftarBahan) {
                bahanArray.put(bahan);
            }
            obj.put("daftarBahan", bahanArray);

            JSONArray langkahArray = new JSONArray();
            for (Langkah l : langkah) {
                langkahArray.put(l.toJsonObject());
            }
            obj.put("langkah", langkahArray);

            obj.put("kategori", kategori.toJsonObject());
            obj.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Factory method dari JSONObject
    public static Resep fromJsonObject(JSONObject obj) {
        try {
            String namaResep = obj.optString("namaResep", "");

            List<String> daftarBahan = new ArrayList<>();
            JSONArray bahanArray = obj.optJSONArray("daftarBahan");
            if (bahanArray != null) {
                for (int i = 0; i < bahanArray.length(); i++) {
                    daftarBahan.add(bahanArray.getString(i));
                }
            }

            List<Langkah> langkah = new ArrayList<>();
            JSONArray langkahArray = obj.optJSONArray("langkah");
            if (langkahArray != null) {
                for (int i = 0; i < langkahArray.length(); i++) {
                    langkah.add(Langkah.fromJsonObject(langkahArray.getJSONObject(i)));
                }
            }

            Kategori kategori = Kategori.fromJsonObject(obj.optJSONObject("kategori"));
            double rating = obj.optDouble("rating", 0);

            return new Resep(namaResep, daftarBahan, langkah, kategori, rating);
        } catch (JSONException e) {
            e.printStackTrace();
            return new Resep();
        }
    }
}
