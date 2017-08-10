package com.chenesseau.denis.spyfall;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Denis on 31/07/2017.
 */

public class PlayerAdapter extends ArrayAdapter<Player> implements View.OnClickListener {

    private ArrayList<Player> listPlayer;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        ImageView btnClose;
    }

    public PlayerAdapter(ArrayList<Player> listPlayer, Context context) {
        super(context, R.layout.player_row_item, R.id.namePlayer, listPlayer);
        this.listPlayer = listPlayer;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        Object object = (Object) getItem(position);
        Player player = (Player) object;

        listPlayer.remove(player);
        Toast.makeText(mContext, player.toString() + " has been successfully removed.", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Player player = getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.player_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.namePlayer);
            viewHolder.txtPhone = (TextView) convertView.findViewById(R.id.phoneNumber);
            viewHolder.btnClose = (ImageView) convertView.findViewById(R.id.imageDelete);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(player.getName());
        viewHolder.txtPhone.setText(player.getPhoneNumber());
        viewHolder.btnClose.setOnClickListener(this);
        viewHolder.btnClose.setTag(position);
        // Return the completed view to render on screen
        return convertView;


    }
}
