package id.jhodie.latihan19;

public class Buku {
    private String judul;
    private String penulis;
    private int tahun;
    private String deskripsi;

    public Buku(String judul, String penulis, int tahun, String deskripsi) {
        this.judul = judul;
        this.penulis = penulis;
        this.tahun = tahun;
        this.deskripsi = deskripsi;
    }

    // Getter dan Setter (untuk edit, butuh setter)
    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Konversi ke JSONObject untuk penyimpanan
    public org.json.JSONObject toJsonObject() {
        org.json.JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("judul", judul);
            obj.put("penulis", penulis);
            obj.put("tahun", tahun);
            obj.put("deskripsi", deskripsi);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Factory method dari JSONObject
    public static Buku fromJsonObject(org.json.JSONObject obj) {
        String judul = obj.optString("judul", "");
        String penulis = obj.optString("penulis", "");
        int tahun = obj.optInt("tahun", 0);
        String deskripsi = obj.optString("deskripsi", "");
        return new Buku(judul, penulis, tahun, deskripsi);
    }
}
