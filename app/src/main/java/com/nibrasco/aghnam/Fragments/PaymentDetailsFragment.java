package com.nibrasco.aghnam.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nibrasco.aghnam.Model.Cart;
import com.nibrasco.aghnam.Model.Session;
import com.nibrasco.aghnam.Model.User;
import com.nibrasco.aghnam.R;
import com.nibrasco.aghnam.Utils.GMailSender;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends Fragment {
    private TextView txtCount, txtTotal;
    TextInputEditText edtAccount;
    private Button btnConfirm, btnUpload;

    Cart cart;
    public PaymentDetailsFragment() {
        // Required empty public constructor
        cart = Session.getInstance().Cart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.nibrasco.aghnam.R.layout.fragment_paymentdetails, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        final View v = getView();
        LoadContent(v);
    }

    private void LinkControls(View v){
        txtCount = (TextView)v.findViewById(com.nibrasco.aghnam.R.id.txtOrderCount);
        txtTotal = (TextView)v.findViewById(com.nibrasco.aghnam.R.id.txtOrderTotal);
        edtAccount = (TextInputEditText)v.findViewById(com.nibrasco.aghnam.R.id.edtPaymentAccount);
        btnConfirm = (Button)v.findViewById(com.nibrasco.aghnam.R.id.btnPaymentConfirm);
        btnUpload = (Button)v.findViewById(R.id.btnUpload);

    }
    private void LoadContent(View v){
        LinkControls(v);

        txtTotal.setText(Float.toString(cart.GetTotal()));
        txtCount.setText(Integer.toString(cart.GetCount()));

        LinkListeners(v);
    }
    private void LinkListeners(View v){
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = getResources().getString(com.nibrasco.aghnam.R.string.msgPaymentSaving);
                    Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    if (new MailTask().execute().get()) {
                        String Account = edtAccount.getText().toString();

                        cart.setBankAccount(Account);

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        final DatabaseReference tblCart = db.getReference("Cart");
                        final DatabaseReference tblUser = db.getReference("User");
                        final User user = Session.getInstance().User();

                        String cartId = user.getCart(),
                                usrId = user.getPhone();

                        cart.MapToDbRef(tblCart.child(cartId));
                        user.AddOrder(cartId);
                        user.setCart("0");

                        user.MapToDbRef(tblUser.child(usrId));
                        Session.getInstance().User(user);
                        Session.getInstance().Cart(new Cart(cart.getAddress()));
                        snackbar.dismiss();

                        CartFragment cartFragment = new CartFragment();
                        getActivity()
                                .getSupportFragmentManager().beginTransaction()
                                .replace(R.id.homeContainer, cartFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }catch(Exception e){
                    Log.e(PaymentDetailsFragment.class.getName(), e.getMessage());
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK)
                                .setType("image/*"),
                        1
                );
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.getData() != null) {
            StorageReference imgRef = storageRef.child(Session.getInstance().User().getCart());
            imgRef.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }


    private String buildMailBody(){
        String body = cart.ToString(Session.getInstance().User());
        //TODO: Fill the body with proper values & format string with string builder
        return body;
    }

    boolean SendMail(){
        GMailSender mailSender = new GMailSender("aghnamofficiel@gmail.com","aghnam2019");

        try {
            ///Toast.makeText(getApplicationContext(), "Connect", Toast.LENGTH_LONG).show();
            //sender.addAttachment(filename);
            //mailSender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/sdcard/mysdfile.txt");
            //TODO: Change with arabic plz "New Commande"
            mailSender.sendMail("New Commande", buildMailBody(),"aghnamofficiel@gmail.com",
                    "aghnamofficiel@gmail.com");
            Log.i("Mail", "Sent");
            //Toast.makeText(getApplicationContext(),"Your mail has been sent",Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
            Log.i("Mail", "Failed "+e);
            return false;
        }
    }
    private class MailTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return SendMail();
        }
    }
}
