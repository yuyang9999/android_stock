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
 * Created by yangyu on 9/12/17.
 */

public class AddProfileDialog extends DialogFragment {
    public interface AddProfileListener {
        String onDialogPositiveSelected(String profileName);
    }

    private AddProfileListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_profile, null);

        final EditText editText = (EditText) view.findViewById(R.id.et_add_profile_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("create a new profile")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pname = editText.getText().toString();
                        if (mListener != null) {
                            mListener.onDialogPositiveSelected(pname);
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
            mListener = (AddProfileListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + "must implement AddProfileListener");
        }
    }
}
