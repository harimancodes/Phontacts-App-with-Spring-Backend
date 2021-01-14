package com.example.phontacts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> implements Filterable {
private ArrayList<ContactItem> contactItemArrayList;
private ArrayList<ContactItem> contactItemArrayListFull;

    public ContactsAdapter(ArrayList<ContactItem> contactItemArrayList) {
        this.contactItemArrayList = contactItemArrayList;
        contactItemArrayListFull=new ArrayList<>(contactItemArrayList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);

       ContactsViewHolder contactsViewHolder=new ContactsViewHolder(v);
       return contactsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
ContactItem currentItem=contactItemArrayList.get(position);
holder.imageView.setImageResource(currentItem.getImgSrc());
holder.contactNameTxtView.setText(currentItem.getcName());
holder.contactNumberTxtView.setText(currentItem.getcNumber());

    }

public void remove(int position){
        contactItemArrayList.remove(position);
        contactItemArrayListFull.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position,contactItemArrayList);
    notifyItemChanged(position,contactItemArrayListFull);
}
    @Override
    public int getItemCount() {
        return contactItemArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }
private Filter contactFilter=new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        ArrayList<ContactItem> filteredList=new ArrayList<>();
        if(constraint==null||constraint.length()==0){
            filteredList.addAll(contactItemArrayListFull);
        }else {
            String filterPattern=constraint.toString().toLowerCase().trim();
            for(ContactItem item:contactItemArrayListFull){
                if(item.getcName().toLowerCase().contains(filterPattern)||item.getcNumber().toLowerCase().contains(filterPattern)){
                    filteredList.add(item);
                }
            }
        }
        FilterResults filterResults=new FilterResults();
        filterResults.values=filteredList;
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
contactItemArrayList.clear();
contactItemArrayList.addAll((ArrayList<ContactItem>) results.values);
notifyDataSetChanged();
    }
};
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
public ImageView imageView;
public TextView contactNameTxtView;
public TextView contactNumberTxtView;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.contactIcon);
            contactNameTxtView=itemView.findViewById(R.id.contactName);
            contactNumberTxtView=itemView.findViewById(R.id.contactNumber);

            itemView.setOnClickListener(v -> {
                String number=contactNumberTxtView.getText().toString();
                if (ActivityCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(v.getContext(),"Permission required!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent callIntent=new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+number));
                Log.v("call number ",number);
v.getContext().startActivity(callIntent);
            });

        }
    }
}
