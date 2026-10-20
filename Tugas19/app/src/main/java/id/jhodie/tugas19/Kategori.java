package id.jhodie.tugas19;

import org.json.JSONException;
import org.json.JSONObject;

public class Kategori {
    private String nama;
    private String kode;

    public Kategori() {}

    public Kategori(String nama, String kode) {
        this.nama = nama;
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("nama", nama);
            obj.put("kode", kode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Kategori fromJsonObject(JSONObject obj) {
        if (obj == null) return new Kategori("", "");
        try {
            String nama = obj.optString("nama", "");
            String kode = obj.optString("kode", "");
            return new Kategori(nama, kode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Kategori("", "");
        }
    }
}
