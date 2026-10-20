package id.jhodie.tugas19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResepAdapter extends RecyclerView.Adapter<ResepAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private Context context;
    private List<Resep> resepList;
    private OnItemClickListener listener;

    public ResepAdapter(Context context, List<Resep> resepList, OnItemClickListener listener) {
        this.context = context;
        this.resepList = resepList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resep resep = resepList.get(position);

        // Set data utama
        holder.txtNamaResep.setText(resep.getNamaResep());
        holder.txtKategori.setText("Kategori: " + resep.getKategori().getNama());
        holder.txtJumlahBahan.setText(resep.getDaftarBahan().size() + " bahan");
        holder.txtJumlahLangkah.setText(resep.getLangkah().size() + " langkah");
        holder.ratingBar.setRating((float) resep.getRating());

        // Preview bahan
        StringBuilder previewBahan = new StringBuilder();
        for (int i = 0; i < Math.min(3, resep.getDaftarBahan().size()); i++) {
            previewBahan.append(resep.getDaftarBahan().get(i));
            if (i < Math.min(3, resep.getDaftarBahan().size()) - 1) {
                previewBahan.append(", ");
            }
        }
        if (resep.getDaftarBahan().size() > 3) {
            previewBahan.append("...");
        }
        holder.txtPreviewBahan.setText("Bahan: " + previewBahan.toString());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return resepList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNamaResep, txtKategori, txtJumlahBahan, txtJumlahLangkah, txtPreviewBahan;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaResep = itemView.findViewById(R.id.txtNamaResep);
            txtKategori = itemView.findViewById(R.id.txtKategori);
            txtJumlahBahan = itemView.findViewById(R.id.txtJumlahBahan);
            txtJumlahLangkah = itemView.findViewById(R.id.txtJumlahLangkah);
            txtPreviewBahan = itemView.findViewById(R.id.txtPreviewBahan); // <-- INI YANG DITAMBAHKAN
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

