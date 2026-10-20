package id.jhodie.latihan19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private Context context;
    private List<Buku> bukuList;
    private OnItemClickListener listener;

    public BukuAdapter(Context context, List<Buku> bukuList, OnItemClickListener listener) {
        this.context = context;
        this.bukuList = bukuList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtJudul, txtPenulis, txtTahun, txtDeskripsi;

        public ViewHolder(View itemView) {
            super(itemView);
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtPenulis = itemView.findViewById(R.id.txtPenulis);
            txtTahun = itemView.findViewById(R.id.txtTahun);
            txtDeskripsi = itemView.findViewById(R.id.txtDeskripsi);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(position);
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(position);
                    return true;
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public BukuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buku, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BukuAdapter.ViewHolder holder, int position) {
        Buku buku = bukuList.get(position);
        holder.txtJudul.setText(buku.getJudul());
        holder.txtPenulis.setText("Penulis: " + buku.getPenulis());
        holder.txtTahun.setText("Tahun: " + buku.getTahun());
        holder.txtDeskripsi.setText(buku.getDeskripsi());

        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return bukuList.size();
    }
}
