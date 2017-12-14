package com.naughtypiggy.android.stock.uis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.naughtypiggy.android.stock.R;

/**
 * Created by yangyu on 12/12/17.
 */

public class AddProfileStockDialog extends DialogFragment {
    public interface AddProfileStockListener {
        String onDialogPositiveSelected(String sname, int share, float price, String boughtDate);
    }

    private AddProfileStockListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_profile_stock, null);

        final EditText addName = (EditText) view.findViewById(R.id.et_add_stock_name);
        final EditText addShare = (EditText) view.findViewById(R.id.et_add_stock_share);
        final EditText addPrice = (EditText) view.findViewById(R.id.et_add_stock_price);
        final EditText addDate = (EditText) view.findViewById(R.id.et_add_stock_date);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("create a new profile")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sname = addName.getText().toString();
                        int shares = Integer.parseInt(addShare.getText().toString());
                        float price = Float.parseFloat(addPrice.getText().toString());
                        String date = addDate.getText().toString();
                        if (mListener != null) {
                            mListener.onDialogPositiveSelected(sname, shares, price, date);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (AddProfileStockListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + "must implement AddProfileListener");
        }
    }
}
