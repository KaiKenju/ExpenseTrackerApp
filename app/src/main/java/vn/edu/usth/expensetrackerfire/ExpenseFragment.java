package vn.edu.usth.expensetrackerfire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.edu.usth.expensetrackerfire.model.Data;


public class ExpenseFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;;
    //Recyle view
    private RecyclerView recyclerView;
    //textview
    private TextView expenseSumResult;;
    //update edittext
    private EditText edtAmount,edtType, edtNote;
    private Button  btnUpdate;
    private ImageView btnDelete;
    //data item value
    private String type,note;
    private double amount;
    private String post_key_ex;;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), "No network connection, You are offline", Toast.LENGTH_LONG).show();
        }

        loadLocale();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase  = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseSumResult  = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double expenseSum  = 0.0;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);
                    expenseSum+=data.getAmount();

//                    String stTotalvalue = String.valueOf(totlavalue);
                    String stTotalvalue = String.format("%.1f", expenseSum);
                    expenseSumResult.setText("-$ " + stTotalvalue);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }
    //network connecting
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    // save language
    private void loadLocale() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key_ex  = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);

        }

        private void setAmount(double amount){
            TextView mAAmount  = mView.findViewById(R.id.amount_txt_expense);
            String stamount  = String.valueOf(amount);
            mAAmount.setText("-$ " + stamount);

        }


    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        //set data to edit text
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        //
        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete= myview.findViewById(R.id.btnDelete);

        AlertDialog dialog = mydialog.create();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type  = edtType.getText().toString().trim();
                note  = edtNote.getText().toString().trim();
                String stamount  = String.valueOf(amount);
                stamount  = edtAmount.getText().toString().trim();

                double intamount   = Double.parseDouble(stamount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intamount, type, note,post_key_ex,mDate);

                mExpenseDatabase.child(post_key_ex).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Inflate the custom layout for the dialog
                View customView = getLayoutInflater().inflate(R.layout.custom_dialog_layout_de, null);
                builder.setView(customView);

                // Create the AlertDialog
                AlertDialog cancelDialog = builder.create();

                // Find buttons in the custom dialog layout
                Button btnYes = customView.findViewById(R.id.btnYes);
                Button btnNo = customView.findViewById(R.id.btnNo);

                // Set click listeners for Yes and No buttons
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Perform cancellation action
                        cancelDialog.dismiss();
                        mExpenseDatabase.child(post_key_ex).removeValue();
                        dialog.dismiss();
                        // Additional actions if needed
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog without taking any action
                        cancelDialog.dismiss();
                        // Additional actions if needed
                    }
                });

                // Show the custom dialog
                cancelDialog.show();

//                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}