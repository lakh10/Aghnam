package com.nibrasco.aghnam.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.database.*;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nibrasco.aghnam.Model.Cart;
import com.nibrasco.aghnam.Model.Session;
import com.nibrasco.aghnam.R;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SheepFragment extends Fragment {

    private Button btnConfirm;
    private MaterialSpinner spSlicing, spWeight, spPackaging;
    private TextView txtTotal;
    private NumberPicker edtQuantity;
    EditText edtNotes;
    private RadioGroup rdGrpIntestine;
    Cart.Item currentItem;
    public SheepFragment() {
        currentItem = Session.getInstance().Item();
        currentItem.setQuantity(1);
        currentItem.setWeight(0);
        currentItem.setIntestine(false);
        currentItem.setSlicing(Cart.eSlicing.Fridge.Value());
        currentItem.setPackaging(Cart.ePackaging.None.Value());
        currentItem.setTotal(Session.getInstance().Item().getDefaultPrice());
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.nibrasco.aghnam.R.layout.fragment_naemiorder, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        final View v = getView();
        assert v != null;
        LoadContent(v);
    }

    private void LinkControls(View v)
    {
        btnConfirm = (Button)v.findViewById(com.nibrasco.aghnam.R.id.btnItemOrder);
        spSlicing = (MaterialSpinner )v.findViewById(com.nibrasco.aghnam.R.id.spSlicing);
        spWeight = (MaterialSpinner )v.findViewById(com.nibrasco.aghnam.R.id.spWeight);
        spPackaging = (MaterialSpinner )v.findViewById(com.nibrasco.aghnam.R.id.spPackaging);
        edtQuantity = (NumberPicker)v.findViewById(com.nibrasco.aghnam.R.id.edtQuantity);
        rdGrpIntestine = (RadioGroup)v.findViewById(com.nibrasco.aghnam.R.id.rdGrpIntestine);
        edtNotes = (EditText)v.findViewById(com.nibrasco.aghnam.R.id.edtNotes);
        txtTotal = (TextView)v.findViewById(com.nibrasco.aghnam.R.id.txtTotalItem);
    }
    private void LinkListeners()
    {
        rdGrpIntestine.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case com.nibrasco.aghnam.R.id.rdInt_Yes:
                        currentItem.setIntestine(true);
                        break;
                    case com.nibrasco.aghnam.R.id.rdInt_No:
                        currentItem.setIntestine(false);
                        break;
                }
            }
        });
        spPackaging.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //if(position != spPackaging.getSelectedItemPosition()) {
                    currentItem.setPackaging(view.getSelectedIndex());
                    txtTotal.setText(Float.toString(currentItem.getTotal()));
                //}
            }
        });
        spWeight.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //if(position != parent.getSelectedItemPosition()) {
                    currentItem.setWeight(view.getSelectedIndex());
                    txtTotal.setText(Float.toString(currentItem.getTotal()));
                //}
            }
        });
        spSlicing.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               // if(position != spSlicing.getSelectedItemPosition()) {
                currentItem.setSlicing(view.getSelectedIndex());
            }
        });
        edtQuantity.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                int qte = value;
                currentItem.setQuantity(qte);
                txtTotal.setText(Float.toString(currentItem.getTotal()));
            }
        });
        edtNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentItem.setNotes(s.toString());
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem.getCategory() != Cart.eCategory.None) {
                    //Add the item to the cart at this point
                    if(SaveChanges(v))
                    {
                        CartFragment f = new CartFragment();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.homeContainer, f);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }
    private void LoadContent(View v)
    {
        LinkControls(v);

        ArrayList<String> list = Cart.Lists.GetWeightNames(Session.getInstance().Item().getCategory());
        ArrayAdapter adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWeight.setAdapter(adapter);
        //spWeight.setSelection(0);

        list = Cart.Lists.GetSlicingNames();
        adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSlicing.setAdapter(adapter);
        //spSlicing.setSelection(0);

        list = Cart.Lists.GetPackagingNames();
        adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPackaging.setAdapter(adapter);
        //spPackaging.setSelection(0);
        txtTotal.setText(Float.toString(currentItem.getTotal()));

        LinkListeners();
    }
    private Boolean SaveChanges(View v) {
        try {
            final Snackbar snack = Snackbar.make(v, "Saving Your Order", Snackbar.LENGTH_LONG);
            snack.show();
            final Boolean[] success = {true};
            currentItem.setId(Session.getInstance().Cart().GetCount());
            Session.getInstance().Cart().AddItem(currentItem);
            //if(Session.getInstance().User().getCart().equals("0"))
            //{
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            final DatabaseReference tblCart = db.getReference("Cart");
            tblCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot cartsSnap) {

                    DatabaseReference cartRef = tblCart.child(Session.getInstance().User().getCart());
                    currentItem.MapToDbRef(cartRef.child("Items").child(Integer.toString(currentItem.getId())));
                    Session.getInstance().Item(currentItem);
                    snack.dismiss();
                    success[0] = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    success[0] = false;
                }
            });
            //}
            return success[0];
        }catch(Exception e){
            Log.e(SheepFragment.class.getName(), e.getMessage());
            return false;
        }
    }
}
