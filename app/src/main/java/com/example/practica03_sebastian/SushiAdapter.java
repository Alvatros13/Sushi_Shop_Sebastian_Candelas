package com.example.practica03_sebastian;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SushiAdapter extends RecyclerView.Adapter<SushiAdapter.SushiViewHolder> {

    private List<SushiItem> sushiList;
    private List<byte[]> imageList;
    private Context context;

    public SushiAdapter(List<SushiItem> sushiList, List<byte[]> imageList, Context context) {
        this.sushiList = sushiList;
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public SushiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sushi_admin, parent, false);
        return new SushiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SushiViewHolder holder, int position) {
        SushiItem sushiItem = sushiList.get(position);
        byte[] bytes = imageList.get(position);
        // Seteando los datos en las vistas del card
        holder.nameTextView.setText(sushiItem.getName());
        holder.priceTextView.setText(String.valueOf(sushiItem.getPrice()));
        // Aquí debes cargar la imagen usando sushiItem.getBytes() en el ImageView correspondiente
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.imageView.setImageBitmap(bitmap);
        // Podrías agregar un clic a los elementos del card si es necesario
        holder.cardView.setOnClickListener(v -> {
            // Lógica para manejar el clic en el card, si es necesaria
        });
    }

    @Override
    public int getItemCount() {
        return sushiList.size();
    }

    public static class SushiViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameTextView;
        TextView priceTextView;
        ImageView imageView;

        public SushiViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            nameTextView = itemView.findViewById(R.id.name);
            priceTextView = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.imageSushi);
        }
    }
}
