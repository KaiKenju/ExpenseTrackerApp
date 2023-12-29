package vn.edu.usth.expensetrackerfire;

import android.app.AlertDialog;
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
import android.widget.TextView;

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
import java.util.Date;

import vn.edu.usth.expensetrackerfire.model.Data;


public class ExpenseFragment extends Fragment {

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    // recyclervier
    private RecyclerView recyclerView;
    private TextView expenseSumResult;
    //edit data item
    private EditText edtAmount,edtType, edtNote;
    private Button btnUpdate, btnDelete;

    //data variable
    private String type,note,amount;
    private String post_key_ex;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        // sum of expense
        expenseSumResult = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int expenseSum = 0;
                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);
                    expenseSum+=data.getAmount();

                    String stExpensesum = String.valueOf(expenseSum);
                    expenseSumResult.setText(stExpensesum + " VND");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
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
                viewHolder.setNote(model.getNote());
                viewHolder.setType(model.getType());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key_ex = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = String.valueOf(model.getAmount());
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseFragment.MyViewHolder(view);
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
            TextView mAAmount = mView.findViewById(R.id.amount_txt_expense);
            String stamount = String.valueOf(amount);
            mAAmount.setText(stamount);

        }


    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtNote = myview.findViewById(R.id.note_edt);
        edtType = myview.findViewById(R.id.type_edt);



        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        AlertDialog dialog = mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type  = edtType.getText().toString().trim();
                note  = edtNote.getText().toString().trim();
                amount = edtAmount.getText().toString().trim();

                String stamount = String.valueOf(amount);
                stamount = edtAmount.getText().toString().trim();

                int intamount  = Integer.parseInt(stamount);
                String mDate  = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intamount, type, note,post_key_ex,mDate);

                mExpenseDatabase.child(post_key_ex).setValue(data);
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mExpenseDatabase.child(post_key_ex).removeValue();
                dialog.dismiss();

            }
        });

        dialog.show();

    }

}