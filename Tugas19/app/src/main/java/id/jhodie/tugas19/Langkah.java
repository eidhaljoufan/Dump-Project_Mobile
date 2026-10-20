package id.jhodie.tugas19;

import org.json.JSONException;
import org.json.JSONObject;

public class Langkah {
    private int urutan;
    private String instruksi;

    public Langkah() {}

    public Langkah(int urutan, String instruksi) {
        this.urutan = urutan;
        this.instruksi = instruksi;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }

    public String getInstruksi() {
        return instruksi;
    }

    public void setInstruksi(String instruksi) {
        this.instruksi = instruksi;
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("urutan", urutan);
            obj.put("instruksi", instruksi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Langkah fromJsonObject(JSONObject obj) {
        try {
            int urutan = obj.optInt("urutan", 0);
            String instruksi = obj.optString("instruksi", "");
            return new Langkah(urutan, instruksi);
        } catch (Exception e) {
            e.printStackTrace();
            return new Langkah();
        }
    }
}

