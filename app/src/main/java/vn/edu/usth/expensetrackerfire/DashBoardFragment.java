package vn.edu.usth.expensetrackerfire;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

import vn.edu.usth.expensetrackerfire.model.Data;


public class DashBoardFragment extends Fragment {

    private FloatingActionButton fab_main_btn, fab_income_btn, fab_expense_btn;
    private TextView fab_income_txt, fab_expense_txt;
    private boolean isOpen = false;
    //animation
    private Animation FadOpen, FadeClose;
    //dashboard income and expense
    private TextView totalIncomeResult, totalExpenseResult;


    //Firebase insert data
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase, mExpenseDatabase;
    //recycler view
    private RecyclerView mRecyclerIncome,mRecyclerExpense;

    private int totalSum = 0;
    private int totalSum_ex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);



        //connect
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);
        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //animation connect
        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);
        // dashboard total income expense
        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        // anh xa Recycler
        mRecyclerIncome = myview.findViewById(R.id.recycler_income_dash);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense_dash);
        //total balance



        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;


                }else{
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }


            }
        });

        //calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalSum = 0;
                for (DataSnapshot mysnap:dataSnapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);
                    totalSum += data.getAmount();
                }

                String stResult;
                if (totalSum < 0) {
                    stResult = "- $" + String.valueOf(Math.abs(totalSum));
                } else if (totalSum > 0) {
                    stResult = "+ $" + String.valueOf(totalSum);
                } else {
                    stResult = "$0";
                }

                totalIncomeResult.setText(stResult);
                updateTotalBalance();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //calculate toatal expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalSum_ex = 0;
                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalSum_ex += data.getAmount();
                }

                String stResult_ex;
                if (totalSum_ex > 0) {
                    stResult_ex = "- $" + String.valueOf(Math.abs(totalSum_ex));
                } else {
                    stResult_ex = "$0";
                }

                totalExpenseResult.setText(stResult_ex);
                updateTotalBalance();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // recycler
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);




        return myview;
    }
    private void updateTotalBalance() {
        int totalBalanceValue = totalSum - totalSum_ex;
        String totalBalanceText = "$" + String.valueOf(totalBalanceValue);
        if (getActivity() != null) {
            TextView totalBalance = getActivity().findViewById(R.id.total_balance);
            totalBalance.setText(totalBalanceText);
        }
    }

    //float button animation
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;


        }else{
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }

    private void addData(){
        //fab button
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }
    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myviewm = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myviewm);

        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText edtAmount = myviewm.findViewById(R.id.amount_edt);
        EditText edtType = myviewm.findViewById(R.id.type_edt);
        EditText edtNote = myviewm.findViewById(R.id.note_edt);

        Button btnSave = myviewm.findViewById(R.id.btnSave);
        Button btnCancel = myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field..");
                    return;
                }
                int ouramountint = Integer.parseInt(amount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field..");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ouramountint,type,note,id, mDate);

//                mIncomeDatabase.child(id).setValue(data);
//                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
//
//                dialog.dismiss();
                mIncomeDatabase.child(id).setValue(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                                    ftAnimation();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to add data", Toast.LENGTH_SHORT).show();
                                    Log.e("FirebaseError", "Failed to add data: " + task.getException());
                                }
                            }
                        });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);


        EditText amount  = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmount)){
                    amount.setError("Required field..");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);

                if(TextUtils.isEmpty(tmType)){
                    type.setError("Required field..");
                    return;

                }
                if(TextUtils.isEmpty(tmNote)){
                    note.setError("Required field..");
                    return;

                }
                //add to firebase
                String id  = mExpenseDatabase.push().getKey();
                String imDate  = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount, tmType,tmNote, id, imDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ftAnimation();

            }
        });
        dialog.show();


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setmIncomeType(model.getType());
                viewHolder.setmIncomeAmount(model.getAmount());
                viewHolder.setmIncomeDate(model.getDate());

            }

            @NonNull
            @Override
            public DashBoardFragment.IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashborad_income_detail, parent, false);
                return new DashBoardFragment.IncomeViewHolder (view);
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        /// truot ngang tach biet
        FirebaseRecyclerOptions<Data> options1 =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setmExpenseType(model.getType());
                viewHolder.setmExpenseAmount(model.getAmount());
                viewHolder.setmExpenseDate(model.getDate());

            }

            @NonNull
            @Override
            public DashBoardFragment.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashborad_expense_detail, parent, false);
                return new DashBoardFragment.ExpenseViewHolder(view);
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();



    }

    //for income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }
        public void setmIncomeType(String type){
            TextView mtype = mIncomeView.findViewById(R.id.type_income_dash);
            mtype.setText(type);
        }

        public void setmIncomeAmount(int amount){
            TextView mAmount = mIncomeView.findViewById(R.id.amount_income_dash);
            String strAmount= String.valueOf(amount);

            mAmount.setText(strAmount);

        }
        public void setmIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.date_income_dash);
            mDate.setText(date);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }
        public void setmExpenseType(String type){
            TextView mtype = mExpenseView.findViewById(R.id.type_expense_dash);
            mtype.setText(type);
        }

        public void setmExpenseAmount(int amount){
            TextView mAmount = mExpenseView.findViewById(R.id.amount_expense_dash);
            String strAmount= String.valueOf(amount);

            mAmount.setText(strAmount);

        }
        public void setmExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_dash);
            mDate.setText(date);
        }
    }
}